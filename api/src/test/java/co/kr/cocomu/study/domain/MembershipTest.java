package co.kr.cocomu.study.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.vo.MembershipStatus;
import co.kr.cocomu.study.domain.vo.MembershipRole;
import co.kr.cocomu.study.dto.request.EditStudyDto;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.user.domain.User;
import org.junit.jupiter.api.Test;

class MembershipTest {

    @Test
    void 스터디에_리더로_참여_할_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);

        // when
        Membership membership = Membership.createLeader(mockStudy, mockUser);

        // then
        assertThat(membership.getRole()).isEqualTo(MembershipRole.LEADER);
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.JOIN);
    }

    @Test
    void 스터디는_일반_스터디원으로_참여_할_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);

        // when
        Membership membership = Membership.createMember(mockStudy, mockUser);
        // then
        assertThat(membership.getRole()).isEqualTo(MembershipRole.MEMBER);
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.JOIN);
    }

    @Test
    void 일반_회원은_스터디에서_나갈_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        Membership membership = Membership.createMember(mockStudy, mockUser);
        doNothing().when(mockStudy).leaveUser();

        // when
        membership.leaveStudy();

        // then
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.LEAVE);
    }

    @Test
    void 스터디장은_스터디를_나갈_수_없다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        Membership membership = Membership.createLeader(mockStudy, mockUser);

        // when & then
        assertThatThrownBy(membership::leaveStudy)
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.LEADER_MUST_USE_REMOVE);
    }

    @Test
    void 스터디장은_스터디를_제거할_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        Membership membership = Membership.createLeader(mockStudy, mockUser);
        doNothing().when(mockStudy).leaveUser();

        // when
        membership.removeStudy();

        // then
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.LEAVE);
    }

    @Test
    void 일반_스터디원은_스터디를_제거할_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        Membership membership = Membership.createMember(mockStudy, mockUser);

        // when & then
        assertThatThrownBy(membership::removeStudy)
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.USER_IS_NOT_LEADER);
    }

    @Test
    void 스터디_식별자를_가져올_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        when(mockStudy.getId()).thenReturn(1L);
        User mockUser = mock(User.class);
        Membership membership = Membership.createMember(mockStudy, mockUser);

        // when
        Long studyId = membership.getStudyId();

        // then
        assertThat(studyId).isEqualTo(1L);
    }

    @Test
    void 스터디_리더인지_알_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        Membership membership = Membership.createLeader(mockStudy, mockUser);

        // when
        boolean leader = membership.isLeader();

        // then
        assertThat(leader).isTrue();
    }

    @Test
    void 스터디_리더가_아닌지_알_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        Membership membership = Membership.createMember(mockStudy, mockUser);

        // when
        boolean leader = membership.isLeader();

        // then
        assertThat(leader).isFalse();
    }

    @Test
    void 스터디_공개_수정이_가능하다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        EditStudyDto dto = mock(EditStudyDto.class);
        Membership membership = Membership.createLeader(mockStudy, mockUser);

        // when
        membership.editPublicStudy(dto);

        // then
        verify(mockStudy).changeToPublic();
    }

    @Test
    void 스터디_비공개_수정이_가능하다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        EditStudyDto dto = mock(EditStudyDto.class);
        Membership membership = Membership.createLeader(mockStudy, mockUser);

        // when
        membership.editPrivateStudy(dto, "pass");

        // then
        verify(mockStudy).changeToPrivate("pass");
    }

    @Test
    void 스터디에_참여후_나간_경우_재참여가_된다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        Membership membership = Membership.createMember(mockStudy, mockUser);
        membership.leaveStudy();

        // when
        Membership reJoinedUser = membership.reJoin();

        // then
        assertThat(reJoinedUser.getStatus()).isEqualTo(MembershipStatus.JOIN);
        verify(mockStudy).increaseCurrentUserCount();
    }

    @Test
    void 스터디에_참여되어있는데_재참여시_예외가_발생한다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        Membership membership = Membership.createMember(mockStudy, mockUser);

        // when & then
        assertThatThrownBy(() -> membership.reJoin())
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.ALREADY_PARTICIPATION_STUDY);
    }

}