package co.kr.cocomu.study.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.vo.StudyStatus;
import co.kr.cocomu.study.dto.request.CreatePrivateStudyDto;
import co.kr.cocomu.study.dto.request.CreatePublicStudyDto;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.Test;

class StudyTest {

    @Test
    void 공개방_스터디_생성_시_처음_회원_수는_0명이다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 10);

        // when
        Study publicStudy = Study.createPublicStudy(dto, 1L);

        // then
        assertThat(publicStudy.getCurrentUserCount()).isEqualTo(0);
    }

    @Test
    void 비공개_스터디_생성_시_비밀번호가_입력된다() {
        // given
        CreatePrivateStudyDto dto = new CreatePrivateStudyDto("코딩 스터디", "", List.of(), List.of(), "스터디", 10);

        // when
        Study privateStudy = Study.createPrivateStudy(dto, "password", 1L);

        // then
        assertThat(privateStudy.getPassword()).isEqualTo("password");
    }

    @Test
    void 스터디_리더인지_알_수_있다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 10);
        Study study = Study.createPublicStudy(dto, 1L);

        // when
        boolean sameLeader = study.isLeader(1L);
        boolean notSameLeader = study.isLeader(2L);

        // then
        assertThat(sameLeader).isTrue();
        assertThat(notSameLeader).isFalse();
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
        Study study = Study.createPrivateStudy(dto, "pass", 1L);

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
        Study study = Study.createPublicStudy(dto, 1L);

        // when
        study.changeToPrivate("pass");

        // then
        assertThat(study.getPassword()).isEqualTo("pass");
        assertThat(study.getStatus()).isEqualTo(StudyStatus.PRIVATE);
    }

    @Test
    void 스터디_인원수가_증가한다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 10);
        Study study = Study.createPublicStudy(dto, 1L);
        int currentUserCount = study.getCurrentUserCount();

        // when
        study.increaseCurrentUserCount();

        // then
        assertThat(study.getCurrentUserCount()).isEqualTo(currentUserCount + 1);
    }

    @Test
    void 스터디_최대_인원수를_초과할_경우_예외가_발생한다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 1);
        Study study = Study.createPublicStudy(dto, 1L);
        study.increaseCurrentUserCount();

        // when & then
        assertThatThrownBy(() -> study.increaseCurrentUserCount())
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.STUDY_IS_FULL);
    }

    @Test
    void 스터디_인원수가_감소한다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 10);
        Study study = Study.createPublicStudy(dto, 1L);
        study.increaseCurrentUserCount();

        // when
        study.decreaseCurrentUserCount();

        // then
        assertThat(study.getCurrentUserCount()).isEqualTo(0);
    }

    @Test
    void 스터디_인원수는_0보다_낮아질_수_없다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 10);
        Study study = Study.createPublicStudy(dto, 1L);

        // when
        study.decreaseCurrentUserCount();

        // then
        assertThat(study.getCurrentUserCount()).isEqualTo(0);
    }

    @Test
    void 스터디_제거가_된다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 10);
        Study study = Study.createPublicStudy(dto, 1L);

        // when
        study.remove();

        // then
        assertThat(study.getStatus()).isEqualTo(StudyStatus.REMOVE);
    }

    @Test
    void 스터디_인원이_존재한다면_제거가_되지_않는다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코딩 스터디", List.of(), List.of(), "스터디", 10);
        Study study = Study.createPublicStudy(dto, 1L);
        study.increaseCurrentUserCount();

        // when & thenr
        assertThatThrownBy(() -> study.remove())
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.CAN_NOT_REMOVE);
    }

}