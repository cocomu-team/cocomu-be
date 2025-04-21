package co.kr.cocomu.study.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.vo.StudyStatus;
import co.kr.cocomu.study.dto.request.CreatePrivateStudyDto;
import co.kr.cocomu.study.dto.request.CreatePublicStudyDto;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.user.domain.User;
import java.util.List;

import co.kr.cocomu.workbook.domain.Workbook;
import org.junit.jupiter.api.Test;

class StudyTest {

    @Test
    void 공개방_스터디_생성_시_처음_회원_수는_0명이다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 10);

        // when
        Study publicStudy = Study.createPublicStudy(dto);

        // then
        assertThat(publicStudy.getCurrentUserCount()).isEqualTo(0);
    }

    @Test
    void 비공개_스터디_생성_시_비밀번호가_입력된다() {
        // given
        CreatePrivateStudyDto dto = new CreatePrivateStudyDto("코딩 스터디", "", List.of(), List.of(), "스터디", 10);

        // when
        Study privateStudy = Study.createPrivateStudy(dto, "password");

        // then
        assertThat(privateStudy.getPassword()).isEqualTo("password");
    }

    @Test
    void 스터디장으로_참여가_된다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 10);
        Study publicStudy = Study.createPublicStudy(dto);
        User mockUser = mock(User.class);
        int currentUserCount = publicStudy.getCurrentUserCount();

        // when
        publicStudy.joinLeader(mockUser);

        // then
        assertThat(publicStudy.getCurrentUserCount()).isEqualTo(currentUserCount + 1);
    }

    @Test
    void 스터디장은_한명이다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 2);
        Study publicStudy = Study.createPublicStudy(dto);
        User mockUser = mock(User.class);
        publicStudy.joinLeader(mockUser);

        // when & then
        assertThatThrownBy(() -> publicStudy.joinLeader(mockUser))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.ALREADY_LEADER_EXISTS);
    }

    @Test
    void 공개_스터디_회원으로_참여가_된다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 2);
        Study publicStudy = Study.createPublicStudy(dto);
        User leader = mock(User.class);
        User member = mock(User.class);
        publicStudy.joinLeader(leader);

        // when
        publicStudy.joinPublicMember(member);

        // then
        assertThat(publicStudy.getCurrentUserCount()).isEqualTo(2);
    }

    @Test
    void 스터디_인원이_가득찬_경우_참여가_불가능하다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 2);
        Study publicStudy = Study.createPublicStudy(dto);
        publicStudy.joinLeader(mock(User.class));
        publicStudy.joinPublicMember(mock(User.class));

        // when & then
        assertThatThrownBy(() -> publicStudy.joinPublicMember(mock(User.class)))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.STUDY_IS_FULL);
    }

    @Test
    void 이미_참여한_경우_예외가_발생한다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 2);
        Study publicStudy = Study.createPublicStudy(dto);
        final User user = mock(User.class);
        publicStudy.joinLeader(user);

        // when & then
        assertThatThrownBy(() -> publicStudy.joinPublicMember(user))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.ALREADY_PARTICIPATION_STUDY);
    }

    @Test
    void 스터디장이_없으면_회원으로_참여하지_못한다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 2);
        Study publicStudy = Study.createPublicStudy(dto);
        User mockUser = mock(User.class);

        // when & then
        assertThatThrownBy(() -> publicStudy.joinPublicMember(mockUser))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.STUDY_REQUIRES_LEADER);
    }

    @Test
    void 공개_스터디에_비공개_스터디_참여를_하면_예외가_발생한다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 2);
        Study publicStudy = Study.createPublicStudy(dto);
        User mockUser = mock(User.class);

        // when & then
        assertThatThrownBy(() -> publicStudy.joinPrivateMember(mockUser))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.USE_PUBLIC_JOIN);
    }

    @Test
    void 비공개_스터디에_참여가_된다() {
        // given
        CreatePrivateStudyDto dto = new CreatePrivateStudyDto("코딩 스터디", "", List.of(), List.of(), "스터디", 2);
        Study privateStudy = Study.createPrivateStudy(dto, "password");
        privateStudy.joinLeader(mock(User.class));

        // when
        privateStudy.joinPrivateMember(mock(User.class));

        // then
        assertThat(privateStudy.getCurrentUserCount()).isEqualTo(2);
    }

    @Test
    void 비공개_스터디에_공개_스터디_참여를_하면_예외가_발생한다() {
        // given
        CreatePrivateStudyDto dto = new CreatePrivateStudyDto("코딩 스터디", "", List.of(), List.of(), "스터디", 2);
        Study privateStudy = Study.createPrivateStudy(dto, "password");
        User mockUser = mock(User.class);

        // when & then
        assertThatThrownBy(() -> privateStudy.joinPublicMember(mockUser))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.USE_PRIVATE_JOIN);
    }

    @Test
    void 스터디에서_회원이_탈퇴하면_현재_인원수가_감소한다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 2);
        Study publicStudy = Study.createPublicStudy(dto);
        publicStudy.joinLeader(mock(User.class));
        publicStudy.joinPublicMember(mock(User.class));
        int currentUserCount = publicStudy.getCurrentUserCount();

        // when
        publicStudy.leaveUser();

        // then
        assertThat(publicStudy.getCurrentUserCount()).isEqualTo(currentUserCount - 1);
    }

    @Test
    void 스터디장은_스터디를_삭제할_수_있다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 2);
        Study publicStudy = Study.createPublicStudy(dto);
        User mockUser = mock(User.class);
        publicStudy.joinLeader(mockUser);

        // when
        publicStudy.remove();

        // then
        assertThat(publicStudy.getCurrentUserCount()).isEqualTo(0);
        assertThat(publicStudy.getStatus()).isEqualTo(StudyStatus.REMOVE);
    }

    @Test
    void 회원이_남아있는_경우_스터디_삭제가_되지_않는다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 2);
        Study publicStudy = Study.createPublicStudy(dto);
        publicStudy.joinLeader(mock(User.class));
        publicStudy.joinPublicMember(mock(User.class));

        // when & then
        assertThatThrownBy(() -> publicStudy.remove())
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.REMAINING_MEMBER);
    }

    @Test
    void 스터디_리더가_없으면_참여_할_수_없다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 2);
        Study publicStudy = Study.createPublicStudy(dto);
        User mockUser = mock(User.class);

        // when & then
        assertThatThrownBy(() -> publicStudy.joinPublicMember(mockUser))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.STUDY_REQUIRES_LEADER);
    }

    @Test
    void 스터디에서_활용할_언어_리스트를_추가한다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 10);
        Study publicStudy = Study.createPublicStudy(dto);

        // when
        publicStudy.addLanguages(List.of(mock(Language.class), mock(Language.class)));

        // then
        assertThat(publicStudy.getLanguages()).hasSize(2);
    }

    @Test
    void 스터디_언어_정보가_수정이_된다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 10);
        Study publicStudy = Study.createPublicStudy(dto);
        publicStudy.addLanguages(List.of(mock(Language.class), mock(Language.class)));

        // when
        publicStudy.addLanguages(List.of(mock(Language.class)));

        // then
        assertThat(publicStudy.getLanguages()).hasSize(1);
    }

    @Test
    void 스터디에서_사용하는_언어_정보를_가져올_수_있다() {
        // given
        Study study = new Study();
        Language mockLanguage = mock(Language.class);
        when(mockLanguage.getId()).thenReturn(1L);
        study.addLanguages(List.of(mockLanguage));

        // when
        Language result = study.getLanguage(1L);

        // then
        assertThat(result).isEqualTo(mockLanguage);
    }

    @Test
    void 스터디에서_사용하는_언어_정보가_없다면_예외가_발생한다() {
        // given
        Study study = new Study();

        // when & then
        assertThatThrownBy(() -> study.getLanguage(1L))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.INVALID_STUDY_LANGUAGE);
    }

    @Test
    void 스터디_정보_수정이_된다() {
        // given
        Study study = new Study();

        // when
        study.updateStudyInfo("스터디", "내용", 10);

        // then
        assertThat(study.getName()).isEqualTo("스터디");
        assertThat(study.getDescription()).isEqualTo("내용");
        assertThat(study.getTotalUserCount()).isEqualTo(10);
    }

    @Test
    void 스터디가_공개_스터디로_변경된다() {
        // given
        CreatePrivateStudyDto dto = mock(CreatePrivateStudyDto.class);
        Study study = Study.createPrivateStudy(dto, "pass");

        // when
        study.changeToPublic();

        // then
        assertThat(study.getPassword()).isNull();
        assertThat(study.getStatus()).isEqualTo(StudyStatus.PUBLIC);
    }

    @Test
    void 스터디가_비공개_스터디로_변경된다() {
        // given
        CreatePublicStudyDto dto = mock(CreatePublicStudyDto.class);
        Study study = Study.createPublicStudy(dto);

        // when
        study.changeToPrivate("pass");

        // then
        assertThat(study.getPassword()).isEqualTo("pass");
        assertThat(study.getStatus()).isEqualTo(StudyStatus.PRIVATE);
    }

}