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

    public void joinLeader(final User user) {
        validateNoLeaderExists();

        final Membership leaderUser = Membership.createLeader(this, user);
        this.memberships.add(leaderUser);
        increaseCurrentUserCount();
    }

    public void increaseCurrentUserCount() {
        this.currentUserCount++;
    }

    public void joinPublicMember(final User user) {
        if (status != PUBLIC) {
            throw new BadRequestException(StudyExceptionCode.USE_PRIVATE_JOIN);
        }
        joinMember(user);
    }

    public void joinPrivateMember(final User user) {
        if (status != PRIVATE) {
            throw new BadRequestException(StudyExceptionCode.USE_PUBLIC_JOIN);
        }
        joinMember(user);
    }

    private void joinMember(final User user) {
        validateStudyUserCount(this.totalUserCount);
        validateLeaderExists();

        memberships.stream()
            .filter(studyUser -> studyUser.getUser().equals(user))
            .findFirst()
            .map(Membership::reJoin)
            .orElseGet(() -> joinNewMember(user));
    }

    private Membership joinNewMember(final User user) {
        final Membership memberUser = Membership.createMember(this, user);
        this.memberships.add(memberUser);
        increaseCurrentUserCount();

        return memberUser;
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

    private void validateNoLeaderExists() {
        if (this.memberships.stream().anyMatch(Membership::isLeader)) {
            throw new BadRequestException(StudyExceptionCode.ALREADY_LEADER_EXISTS);
        }
    }

    private void validateStudyUserCount(final int totalUserCount) {
        if (this.currentUserCount >= totalUserCount) {
            throw new BadRequestException(StudyExceptionCode.STUDY_IS_FULL);
        }
    }

    private void validateLeaderExists() {
        if (this.memberships.stream().noneMatch(Membership::isLeader)) {
            throw new BadRequestException(StudyExceptionCode.STUDY_REQUIRES_LEADER);
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
