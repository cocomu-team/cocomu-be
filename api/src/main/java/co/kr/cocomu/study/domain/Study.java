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
import co.kr.cocomu.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(length = 100, nullable = false)
    private String name;
    private String password;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(20)")
    private StudyStatus status;

    @Column(nullable = false)
    private int currentUserCount;
    @Column(nullable = false)
    private int totalUserCount;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membership> memberships = new ArrayList<>();

    private Study(
        final String name,
        final String password,
        final String description,
        final StudyStatus status,
        final int totalUserCount
    ) {
        this.name = name;
        this.password = password;
        this.description = description;
        this.status = status;
        this.currentUserCount = 0;
        this.totalUserCount = totalUserCount;
    }

    public static Study createPublicStudy(final CreatePublicStudyDto dto) {
        return new Study(dto.name(), null, dto.description(), PUBLIC, dto.totalUserCount());
    }

    public static Study createPrivateStudy(final CreatePrivateStudyDto dto, final String password) {
        return new Study(dto.name(), password, dto.description(), PRIVATE, dto.totalUserCount());
    }

    public void increaseCurrentUserCount() {
        this.currentUserCount++;
    }

    protected void leaveUser() {
        this.currentUserCount--;
    }

    protected void remove() {
        if (this.currentUserCount > 1) {
            throw new BadRequestException(StudyExceptionCode.REMAINING_MEMBER);
        }
        this.currentUserCount = 0;
        this.status = REMOVE;
    }

    private void validateStudyUserCount(final int totalUserCount) {
        if (this.currentUserCount >= totalUserCount) {
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

}
