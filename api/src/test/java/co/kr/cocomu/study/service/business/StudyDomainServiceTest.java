package co.kr.cocomu.study.service.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.common.exception.domain.NotFoundException;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.StudyUser;
import co.kr.cocomu.study.dto.request.CreatePublicStudyDto;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.study.repository.jpa.StudyRepository;
import co.kr.cocomu.study.repository.jpa.StudyUserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyDomainServiceTest {

    @Mock private StudyRepository studyRepository;
    @Mock private StudyUserRepository studyUserRepository;

    @InjectMocks private StudyDomainService studyDomainService;

    @Test
    void 스터디_ID로_스터디를_찾을_수_있다() {
        // given
        final CreatePublicStudyDto dto = new CreatePublicStudyDto("스터디", null, null, null, 0);
        final Study mockStudy = Study.createPublicStudy(dto);

        when(studyRepository.findById(anyLong())).thenReturn(Optional.of(mockStudy));

        // when
        Study actualStudy = studyDomainService.getStudyWithThrow(1L);

        // then
        assertThat(actualStudy).isEqualTo(mockStudy);
    }

    @Test
    void 존재하지_않는_스터디_조회_시_예외가_발생한다() {
        // given
        when(studyRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyDomainService.getStudyWithThrow(1L))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.NOT_FOUND_STUDY);
    }

    @Test
    void 스터디에_참여중이지_않으면_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long studyId = 1L;

        when(studyUserRepository.isUserJoinedStudy(userId, studyId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> studyDomainService.validateStudyMembership(userId, studyId))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.NO_PARTICIPATION_USER);
    }

    @Test
    void 참여중인_스터디라면_정상_통과한다() {
        // given
        Long userId = 1L;
        Long studyId = 1L;

        when(studyUserRepository.isUserJoinedStudy(userId, studyId)).thenReturn(true);

        // when & then
        assertThatCode(() -> studyDomainService.validateStudyMembership(userId, studyId))
            .doesNotThrowAnyException();
    }

    @Test
    void 스터디_사용자를_찾을_수_있다() {
        // given
        StudyUser mockStudyUser = mock(StudyUser.class);

        when(studyUserRepository.findByUser_IdAndStudy_Id(anyLong(), anyLong())).thenReturn(Optional.of(mockStudyUser));

        // when
        StudyUser result = studyDomainService.getStudyUserWithThrow(1L, 1L);

        // then
        assertThat(result).isEqualTo(mockStudyUser);
    }

    @Test
    void 스터디_사용자가_없으면_예외가_발생한다() {
        // given
        when(studyUserRepository.findByUser_IdAndStudy_Id(anyLong(), anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyDomainService.getStudyUserWithThrow(1L, 1L))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.NOT_FOUND_STUDY_USER);
    }

    @Test
    void 스터디_생성시_문제집_정보가_비어있으면_예외가_발생한다() {
        // given
        List<Long> workbookIds = List.of();
        CreatePublicStudyDto mockDto = mock(CreatePublicStudyDto.class);
        when(mockDto.workbooks()).thenReturn(workbookIds);

        // when & then
        assertThatThrownBy(() -> studyDomainService.validateCreateStudy(mockDto))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.WORKBOOK_IS_REQUIRED_VALUE);
    }

    @Test
    void 스터디_생성시_문제집_정보는_필수이다() {
        // given
        List<Long> workbookIds = List.of(1L);
        CreatePublicStudyDto mockDto = mock(CreatePublicStudyDto.class);
        when(mockDto.workbooks()).thenReturn(workbookIds);

        // when & then
        assertThatCode(() -> studyDomainService.validateCreateStudy(mockDto))
            .doesNotThrowAnyException();
    }

}