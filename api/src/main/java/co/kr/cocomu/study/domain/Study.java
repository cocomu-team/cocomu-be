package co.kr.cocomu.study.domain;

import static co.kr.cocomu.study.domain.vo.StudyStatus.PRIVATE;
import static co.kr.cocomu.study.domain.vo.StudyStatus.PUBLIC;
import static co.kr.cocomu.study.domain.vo.StudyStatus.REMOVE;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.common.repository.TimeBaseEntity;
import co.kr.cocomu.study.domain.vo.StudyStatus;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
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

    private Study(
        final Long leaderId,
        final String name,
        final String description,
        final int totalUserCount
    ) {
        this.leaderId = leaderId;
        this.name = name;
        this.description = description;
        this.currentUserCount = 0;
        this.totalUserCount = totalUserCount;
    }

    public static Study create(
        final String name,
        final String description,
        final int totalUserCount,
        final Long leaderId
    ) {
        return new Study(leaderId, name, description, totalUserCount);
    }

    public void increaseCurrentUserCount() {
        if (currentUserCount >= totalUserCount) {
            throw new BadRequestException(StudyExceptionCode.STUDY_IS_FULL);
        }
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

    public boolean isLeader(final Long userId) {
        return leaderId.equals(userId);
    }

    public void updateBasicInfo(final String name, final String description, final int totalUserCount) {
        if (currentUserCount > totalUserCount) {
            throw new BadRequestException(StudyExceptionCode.STUDY_IS_FULL);
        }
        this.name = name;
        this.description = description;
        this.totalUserCount = totalUserCount;
    }

    public void updatePublicStatus() {
        this.status = PUBLIC;
        this.password = null;
    }

    public void updatePrivateStatus(final String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            throw new BadRequestException(StudyExceptionCode.REQUIRED_STUDY_PASSWORD);
        }
        this.status = PRIVATE;
        this.password = encodedPassword;
    }

    public boolean isPublic() {
        return status == PUBLIC;
    }

}
