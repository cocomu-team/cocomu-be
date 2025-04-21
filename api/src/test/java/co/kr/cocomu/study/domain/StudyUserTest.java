package co.kr.cocomu.study.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.vo.StudyRole;
import co.kr.cocomu.study.domain.vo.StudyUserStatus;
import co.kr.cocomu.study.dto.request.EditStudyDto;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.Test;

class StudyUserTest {

    @Test
    void 스터디에_리더로_참여_할_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);

        // when
        StudyUser studyUser = StudyUser.createLeader(mockStudy, mockUser);

        // then
        assertThat(studyUser.getRole()).isEqualTo(StudyRole.LEADER);
        assertThat(studyUser.getStatus()).isEqualTo(StudyUserStatus.JOIN);
    }

    @Test
    void 스터디는_일반_스터디원으로_참여_할_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);

        // when
        StudyUser studyUser = StudyUser.createMember(mockStudy, mockUser);
        // then
        assertThat(studyUser.getRole()).isEqualTo(StudyRole.MEMBER);
        assertThat(studyUser.getStatus()).isEqualTo(StudyUserStatus.JOIN);
    }

    @Test
    void 일반_회원은_스터디에서_나갈_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        StudyUser studyUser = StudyUser.createMember(mockStudy, mockUser);
        doNothing().when(mockStudy).leaveUser();

        // when
        studyUser.leaveStudy();

        // then
        assertThat(studyUser.getStatus()).isEqualTo(StudyUserStatus.LEAVE);
    }

    @Test
    void 스터디장은_스터디를_나갈_수_없다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        StudyUser studyUser = StudyUser.createLeader(mockStudy, mockUser);

        // when & then
        assertThatThrownBy(studyUser::leaveStudy)
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.LEADER_MUST_USE_REMOVE);
    }

    @Test
    void 스터디장은_스터디를_제거할_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        StudyUser studyUser = StudyUser.createLeader(mockStudy, mockUser);
        doNothing().when(mockStudy).leaveUser();

        // when
        studyUser.removeStudy();

        // then
        assertThat(studyUser.getStatus()).isEqualTo(StudyUserStatus.LEAVE);
    }

    @Test
    void 일반_스터디원은_스터디를_제거할_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        StudyUser studyUser = StudyUser.createMember(mockStudy, mockUser);

        // when & then
        assertThatThrownBy(studyUser::removeStudy)
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.USER_IS_NOT_LEADER);
    }

    @Test
    void 스터디_식별자를_가져올_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        when(mockStudy.getId()).thenReturn(1L);
        User mockUser = mock(User.class);
        StudyUser studyUser = StudyUser.createMember(mockStudy, mockUser);

        // when
        Long studyId = studyUser.getStudyId();

        // then
        assertThat(studyId).isEqualTo(1L);
    }

    @Test
    void 스터디_리더인지_알_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        StudyUser studyUser = StudyUser.createLeader(mockStudy, mockUser);

        // when
        boolean leader = studyUser.isLeader();

        // then
        assertThat(leader).isTrue();
    }

    @Test
    void 스터디_리더가_아닌지_알_수_있다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        StudyUser studyUser = StudyUser.createMember(mockStudy, mockUser);

        // when
        boolean leader = studyUser.isLeader();

        // then
        assertThat(leader).isFalse();
    }

    @Test
    void 스터디_공개_수정이_가능하다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        EditStudyDto dto = mock(EditStudyDto.class);
        StudyUser studyUser = StudyUser.createLeader(mockStudy, mockUser);

        // when
        studyUser.editPublicStudy(dto, List.of());

        // then
        verify(mockStudy).changeToPublic();
    }

    @Test
    void 스터디_비공개_수정이_가능하다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        EditStudyDto dto = mock(EditStudyDto.class);
        StudyUser studyUser = StudyUser.createLeader(mockStudy, mockUser);

        // when
        studyUser.editPrivateStudy(dto, List.of(), "pass");

        // then
        verify(mockStudy).changeToPrivate("pass");
    }

    @Test
    void 스터디에_참여후_나간_경우_재참여가_된다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        StudyUser studyUser = StudyUser.createMember(mockStudy, mockUser);
        studyUser.leaveStudy();

        // when
        StudyUser reJoinedUser = studyUser.reJoin();

        // then
        assertThat(reJoinedUser.getStatus()).isEqualTo(StudyUserStatus.JOIN);
        verify(mockStudy).increaseCurrentUserCount();
    }

    @Test
    void 스터디에_참여되어있는데_재참여시_예외가_발생한다() {
        // given
        Study mockStudy = mock(Study.class);
        User mockUser = mock(User.class);
        StudyUser studyUser = StudyUser.createMember(mockStudy, mockUser);

        // when & then
        assertThatThrownBy(() -> studyUser.reJoin())
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.ALREADY_PARTICIPATION_STUDY);
    }

}