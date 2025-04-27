package co.kr.cocomu.study.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.vo.MembershipRole;
import co.kr.cocomu.study.domain.vo.MembershipStatus;
import co.kr.cocomu.study.dto.request.EditStudyDto;
import co.kr.cocomu.study.exception.MembershipExceptionCode;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.user.domain.User;
import org.junit.jupiter.api.Test;

class MembershipTest {

    @Test
    void 스터디에_리더로_참여_할_수_있다() {
        // given
        Study mockStudy = mock(Study.class);

        // when
        Membership membership = Membership.createLeader(mockStudy, 1L);

        // then
        assertThat(membership.getRole()).isEqualTo(MembershipRole.LEADER);
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.JOIN);
    }

    @Test
    void 스터디는_일반_스터디원으로_참여_할_수_있다() {
        // given
        Study mockStudy = mock(Study.class);

        // when
        Membership membership = Membership.createMember(mockStudy, 1L);
        // then
        assertThat(membership.getRole()).isEqualTo(MembershipRole.MEMBER);
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.JOIN);
    }

    @Test
    void 일반_회원은_스터디에서_나갈_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        Membership membership = Membership.createMember(mockStudy, 1L);

        // when
        membership.leave();

        // then
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.LEFT);
    }

    @Test
    void 스터디장은_스터디를_제거할_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        Membership membership = Membership.createLeader(mockStudy, 1L);
        doNothing().when(mockStudy).decreaseCurrentUserCount();

        // when
        membership.removeStudy();

        // then
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.LEFT);
    }

    @Test
    void 일반_스터디원은_스터디를_제거할_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        doNothing().when(mockStudy).remove();
        Membership membership = Membership.createMember(mockStudy, 1L);

        // when & then
        assertThatThrownBy(membership::removeStudy)
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.USER_IS_NOT_LEADER);
    }

    @Test
    void 스터디_식별자를_가져올_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        Membership membership = Membership.createMember(mockStudy, 1L);

        // when
        Long studyId = membership.getStudyId();

        // then
        assertThat(studyId).isEqualTo(mockStudy.getId());
    }

    @Test
    void 스터디_리더인지_알_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        Membership membership = Membership.createLeader(mockStudy, 1L);

        // when
        boolean leader = membership.isLeader();

        // then
        assertThat(leader).isTrue();
    }

    @Test
    void 스터디_리더가_아닌지_알_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        Membership membership = Membership.createMember(mockStudy, 1L);

        // when
        boolean leader = membership.isLeader();

        // then
        assertThat(leader).isFalse();
    }

    @Test
    void 스터디_공개_수정이_가능하다() {
        // given
        Study mockStudy = mock(Study.class);
        EditStudyDto dto = mock(EditStudyDto.class);
        Membership membership = Membership.createLeader(mockStudy, 1L);

        // when
        membership.editPublicStudy(dto);

        // then
        verify(mockStudy).changeToPublic();
    }

    @Test
    void 스터디_비공개_수정이_가능하다() {
        // given
        Study mockStudy = mock(Study.class);
        EditStudyDto dto = mock(EditStudyDto.class);
        Membership membership = Membership.createLeader(mockStudy, 1L);

        // when
        membership.editPrivateStudy(dto, "pass");

        // then
        verify(mockStudy).changeToPrivate("pass");
    }

    @Test
    void 스터디에_참여후_나간_경우_재참여가_된다() {
        // given
        Study mockStudy = mock(Study.class);
        Membership membership = Membership.createMember(mockStudy, 1L);
        membership.leave();

        // when
        membership.reJoin();

        // then
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.JOIN);
    }

    @Test
    void 스터디에_참여되어있는데_재참여시_예외가_발생한다() {
        // given
        Study mockStudy = mock(Study.class);
        Membership membership = Membership.createMember(mockStudy, 1L);

        // when & then
        assertThatThrownBy(() -> membership.reJoin())
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.ALREADY_PARTICIPATION_STUDY);
    }

}