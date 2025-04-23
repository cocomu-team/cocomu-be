package co.kr.cocomu.study.domain;


import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.common.repository.TimeBaseEntity;
import co.kr.cocomu.study.domain.vo.StudyRole;
import co.kr.cocomu.study.domain.vo.StudyUserStatus;
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
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cocomu_study_user", uniqueConstraints = @UniqueConstraint(columnNames = {"study_id", "user_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper = false, of = "id")
public class StudyUser extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(20)")
    private StudyRole role;

    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(20)")
    private StudyUserStatus status;

    private StudyUser(final Study study, final User user, final StudyRole role) {
        this.study = study;
        this.user = user;
        this.role = role;
        this.status = StudyUserStatus.JOIN;
    }

    public static StudyUser createLeader(final Study study, final User user) {
        return new StudyUser(study, user, StudyRole.LEADER);
    }

    public static StudyUser createMember(final Study study, final User user) {
        return new StudyUser(study, user, StudyRole.MEMBER);
    }

    public void leaveStudy() {
        validateMemberRole();
        study.leaveUser();
        status = StudyUserStatus.LEAVE;
    }

    public void removeStudy() {
        validateLeaderRole();
        study.remove();
        status = StudyUserStatus.LEAVE;
    }

    public Long getStudyId() {
        return study.getId();
    }

    public boolean isLeader() {
        return this.role == StudyRole.LEADER;
    }

    private void validateMemberRole() {
        if (role != StudyRole.MEMBER) {
            throw new BadRequestException(StudyExceptionCode.LEADER_MUST_USE_REMOVE);
        }
    }

    private void validateLeaderRole() {
        if (role != StudyRole.LEADER) {
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

    public StudyUser reJoin() {
        if (this.status == StudyUserStatus.JOIN) {
            throw new BadRequestException(StudyExceptionCode.ALREADY_PARTICIPATION_STUDY);
        }
        status = StudyUserStatus.JOIN;
        study.increaseCurrentUserCount();
        return this;
    }

}
