package co.kr.cocomu.study.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.vo.StudyStatus;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class StudyTest {

    @Test
    void 스터디_기본_생성이_된다() {
        // when
        Study study = Study.create("name", "description", 1, 1L);
        // then
        assertThat(study.getCurrentUserCount()).isEqualTo(0);
        assertThat(study.getTotalUserCount()).isEqualTo(1);
        assertThat(study.getLeaderId()).isEqualTo(1L);
        assertThat(study.getName()).isEqualTo("name");
        assertThat(study.getDescription()).isEqualTo("description");
    }

    @Test
    void 스터디_리더인지_알_수_있다() {
        // given
        Study study = Study.create("name", "description", 1, 1L);

        // when
        boolean sameLeader = study.isLeader(1L);
        boolean notSameLeader = study.isLeader(2L);

        // then
        assertThat(sameLeader).isTrue();
        assertThat(notSameLeader).isFalse();
    }

    @Test
    void 스터디_기본_정보_수정이_된다() {
        // given
        Study study = new Study();

        // when
        study.updateBasicInfo("스터디", "내용", 10);

        // then
        assertThat(study.getName()).isEqualTo("스터디");
        assertThat(study.getDescription()).isEqualTo("내용");
        assertThat(study.getTotalUserCount()).isEqualTo(10);
    }

    @Test
    void 스터디_기본_정보_수정시_현재_인원보다_최대_인원이_적다면_예외가_발생한다() {
        // given
        Study study = Study.create("name", "description", 1, 1L);
        study.increaseCurrentUserCount();

        // when & then
        assertThatThrownBy(() -> study.updateBasicInfo("스터디", "내용", 0))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.STUDY_IS_FULL);
    }

    @Test
    void 스터디가_비공개_스터디로_변경된다() {
        // given
        Study study = Study.create("name", "description", 1, 1L);

        // when
        study.updatePrivateStatus("password");

        // then
        assertThat(study.getPassword()).isEqualTo("password");
        assertThat(study.getStatus()).isEqualTo(StudyStatus.PRIVATE);
    }

    @NullAndEmptySource
    @ParameterizedTest
    void 비공개_스터디로_전환시_비밀번호가_없으면_안된다(String password) {
        // given
        Study study = Study.create("name", "description", 1, 1L);

        // when & then
        assertThatThrownBy(() -> study.updatePrivateStatus(password))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.REQUIRED_STUDY_PASSWORD);
    }

    @Test
    void 스터디가_공개_스터디로_변경된다() {
        // given
        Study study = Study.create("name", "description", 1, 1L);
        study.updatePrivateStatus("password");

        // when
        study.updatePublicStatus();

        // then
        assertThat(study.getPassword()).isNull();
        assertThat(study.getStatus()).isEqualTo(StudyStatus.PUBLIC);
    }

    @Test
    void 스터디_인원수가_증가한다() {
        // given
        Study study = Study.create("name", "description", 1, 1L);
        int currentUserCount = study.getCurrentUserCount();

        // when
        study.increaseCurrentUserCount();

        // then
        assertThat(study.getCurrentUserCount()).isEqualTo(currentUserCount + 1);
    }

    @Test
    void 스터디_최대_인원수를_초과할_경우_예외가_발생한다() {
        // given
        Study study = Study.create("name", "description", 1, 1L);
        study.increaseCurrentUserCount();

        // when & then
        assertThatThrownBy(() -> study.increaseCurrentUserCount())
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.STUDY_IS_FULL);
    }

    @Test
    void 스터디_인원수가_감소한다() {
        // given
        Study study = Study.create("name", "description", 1, 1L);
        study.increaseCurrentUserCount();

        // when
        study.decreaseCurrentUserCount();

        // then
        assertThat(study.getCurrentUserCount()).isEqualTo(0);
    }

    @Test
    void 스터디_인원수는_0보다_낮아질_수_없다() {
        // given
        Study study = Study.create("name", "description", 1, 1L);

        // when
        study.decreaseCurrentUserCount();

        // then
        assertThat(study.getCurrentUserCount()).isEqualTo(0);
    }

    @Test
    void 스터디_제거가_된다() {
        // given
        Study study = Study.create("name", "description", 1, 1L);

        // when
        study.remove();

        // then
        assertThat(study.getStatus()).isEqualTo(StudyStatus.REMOVE);
    }

    @Test
    void 스터디_인원이_존재한다면_제거가_되지_않는다() {
        // given
        Study study = Study.create("name", "description", 1, 1L);
        study.increaseCurrentUserCount();

        // when & then
        assertThatThrownBy(() -> study.remove())
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.CAN_NOT_REMOVE);
    }

}