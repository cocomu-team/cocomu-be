package co.kr.cocomu.study.domain;


import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.common.repository.TimeBaseEntity;
import co.kr.cocomu.study.domain.vo.MembershipRole;
import co.kr.cocomu.study.domain.vo.MembershipStatus;
import co.kr.cocomu.study.dto.request.EditStudyDto;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cocomu_study_membership", uniqueConstraints = @UniqueConstraint(columnNames = {"study_id", "user_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper = false, of = "id")
public class Membership extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_membership_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Study study;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private MembershipRole role;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus status;

    protected Membership(final Study study, final User user, final MembershipRole role) {
        this.study = study;
        this.user = user;
        this.role = role;
        this.status = MembershipStatus.JOIN;
    }

    protected Membership(final Study study, final Long userId, final MembershipRole role) {
        this.study = study;
        this.userId = userId;
        this.role = role;
        this.status = MembershipStatus.JOIN;
    }

    public static Membership createLeader(final Study study, final Long userId) {
        return new Membership(study, userId, MembershipRole.LEADER);
    }

    public static Membership createLeader(final Study study, final User user) {
        return new Membership(study, user, MembershipRole.LEADER);
    }

    public static Membership createMember(final Study study, final User user) {
        return new Membership(study, user, MembershipRole.MEMBER);
    }

    public void leaveStudy() {
        validateMemberRole();
        study.leaveUser();
        status = MembershipStatus.LEAVE;
    }

    public void removeStudy() {
        validateLeaderRole();
        study.remove();
        status = MembershipStatus.LEAVE;
    }

    public Long getStudyId() {
        return study.getId();
    }

    public boolean isLeader() {
        return this.role == MembershipRole.LEADER;
    }

    private void validateMemberRole() {
        if (role != MembershipRole.MEMBER) {
            throw new BadRequestException(StudyExceptionCode.LEADER_MUST_USE_REMOVE);
        }
    }

    private void validateLeaderRole() {
        if (role != MembershipRole.LEADER) {
            throw new BadRequestException(StudyExceptionCode.USER_IS_NOT_LEADER);
        }
    }

    public void editPublicStudy(final EditStudyDto dto) {
        validateLeaderRole();
        study.updateStudyInfo(dto.name(), dto.description(), dto.totalUserCount());
        study.changeToPublic();
    }

    public void editPrivateStudy(final EditStudyDto dto, final String newPassword) {
        validateLeaderRole();
        study.updateStudyInfo(dto.name(), dto.description(), dto.totalUserCount());
        study.changeToPrivate(newPassword);
    }

    public Membership reJoin() {
        if (this.status == MembershipStatus.JOIN) {
            throw new BadRequestException(StudyExceptionCode.ALREADY_PARTICIPATION_STUDY);
        }
        status = MembershipStatus.JOIN;
        study.increaseCurrentUserCount();
        return this;
    }

}
