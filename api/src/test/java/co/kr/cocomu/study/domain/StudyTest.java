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