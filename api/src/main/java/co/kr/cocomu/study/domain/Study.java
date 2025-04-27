package co.kr.cocomu.study.domain;

import static co.kr.cocomu.study.domain.vo.StudyStatus.PRIVATE;
import static co.kr.cocomu.study.domain.vo.StudyStatus.PUBLIC;
import static co.kr.cocomu.study.domain.vo.StudyStatus.REMOVE;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.common.repository.TimeBaseEntity;
import co.kr.cocomu.study.domain.vo.StudyStatus;
import co.kr.cocomu.study.dto.request.CreatePrivateStudyDto;
import co.kr.cocomu.study.dto.request.CreatePublicStudyDto;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cocomu_study")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Study extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    @Column(nullable = false)
    private Long leaderId;

    @Column(nullable = false)
    private String name;
    private String password;

    @Lob
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private StudyStatus status;

    @Column(nullable = false)
    private int currentUserCount;
    @Column(nullable = false)
    private int totalUserCount;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membership> memberships = new ArrayList<>();

    private Study(
        final Long leaderId,
        final String name,
        final String password,
        final String description,
        final StudyStatus status,
        final int totalUserCount
    ) {
        this.leaderId = leaderId;
        this.name = name;
        this.password = password;
        this.description = description;
        this.status = status;
        this.currentUserCount = 0;
        this.totalUserCount = totalUserCount;
    }

    public static Study createPublicStudy(final CreatePublicStudyDto dto, final Long leaderId) {
        return new Study(leaderId, dto.name(), null, dto.description(), PUBLIC, dto.totalUserCount());
    }

    public static Study createPrivateStudy(
        final CreatePrivateStudyDto dto,
        final String password,
        final Long leaderId
    ) {
        return new Study(leaderId, dto.name(), password, dto.description(), PRIVATE, dto.totalUserCount());
    }

    public void increaseCurrentUserCount() {
        validateStudyUserCount(totalUserCount);
        this.currentUserCount++;
    }

    public void decreaseCurrentUserCount() {
        if (this.currentUserCount > 0) {
            this.currentUserCount--;
        }
    }

    public void remove() {
        if (this.currentUserCount > 0) {
            throw new BadRequestException(StudyExceptionCode.CAN_NOT_REMOVE);
        }
        this.status = REMOVE;
    }

    private void validateStudyUserCount(final int totalUserCount) {
        if (currentUserCount >= totalUserCount) {
            throw new BadRequestException(StudyExceptionCode.STUDY_IS_FULL);
        }
    }

    public void updateStudyInfo(final String name, final String description, final int totalUserCount) {
        validateStudyUserCount(totalUserCount);
        this.name = name;
        this.description = description;
        this.totalUserCount = totalUserCount;
    }

    public void changeToPublic() {
        status = PUBLIC;
        password = null;
    }

    public void changeToPrivate(final String newPassword) {
        status = PRIVATE;
        password = newPassword;
    }

    public boolean isLeader(final Long userId) {
        return leaderId.equals(userId);
    }

}
