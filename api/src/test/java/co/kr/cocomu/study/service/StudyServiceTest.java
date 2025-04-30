package co.kr.cocomu.study.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.vo.StudyStatus;
import co.kr.cocomu.study.dto.request.CreateStudyDto;
import co.kr.cocomu.study.dto.request.EditStudyDto;
import co.kr.cocomu.study.dto.request.PasswordDto;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.study.repository.StudyRepository;
import co.kr.cocomu.study.business.PasswordBusiness;
import co.kr.cocomu.study.factory.StudyFactory;
import co.kr.cocomu.study.strategy.StudyStrategy;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock private StudyRepository studyRepository;
    @Mock private RelationService relationService;
    @Mock private MembershipService membershipService;
    @Mock private StudyFactory studyFactory;

    @InjectMocks private StudyService studyService;

    @Test
    void 스터디_생성과_리더로_참여에_성공한다() {
        // given
        CreateStudyDto mockDto = mock(CreateStudyDto.class);
        when(mockDto.publicStudy()).thenReturn(true);

        Study mockStudy = mock(Study.class);
        when(studyFactory.generateStudy(mockDto, 1L)).thenReturn(mockStudy);

        StudyStrategy mockStudyStrategy = mock(StudyStrategy.class);
        when(studyFactory.resolveStudyStrategy(mockDto.publicStudy())).thenReturn(mockStudyStrategy);

        // when
        Long result = studyService.create(1L, mockDto);

        // then
        verify(mockStudyStrategy).updateStatus(mockStudy, mockDto.password());
        verify(relationService).addTags(mockStudy, mockDto.workbookTagIds(), mockDto.languageTagIds());
        verify(membershipService).joinLeader(mockStudy, 1L);
        verify(studyRepository).save(mockStudy);
        assertThat(result).isEqualTo(mockStudy.getId());
    }

    @Test
    void 비밀번호가_맞을_경우_스터디_참여에_성공한다() {
        // given
        PasswordDto mockDto = mock(PasswordDto.class);
        Study mockStudy = mock(Study.class);
        when(studyRepository.findByIdAndStatusNot(1L, StudyStatus.REMOVE)).thenReturn(Optional.of(mockStudy));
        StudyStrategy mockStudyStrategy = mock(StudyStrategy.class);
        when(studyFactory.resolveStudyStrategy(mockStudy.isPublic())).thenReturn(mockStudyStrategy);
        when(mockStudyStrategy.matchPassword(mockDto.password(), mockStudy.getPassword())).thenReturn(true);

        // when
        Long result = studyService.join(1L, 1L, mockDto);

        // then
        verify(membershipService).joinMember(mockStudy, 1L);
        assertThat(result).isEqualTo(mockStudy.getId());
    }

    @Test
    void 비밀번호가_틀려서_비공걔_스터디_참여에_실패한다() {
        // given
        Study mockStudy = mock(Study.class);
        PasswordDto mockDto = mock(PasswordDto.class);
        when(studyRepository.findByIdAndStatusNot(1L, StudyStatus.REMOVE)).thenReturn(Optional.of(mockStudy));
        StudyStrategy mockStudyStrategy = mock(StudyStrategy.class);
        when(studyFactory.resolveStudyStrategy(mockStudy.isPublic())).thenReturn(mockStudyStrategy);
        when(mockStudyStrategy.matchPassword(mockDto.password(), mockStudy.getPassword())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> studyService.join(1L, 1L, mockDto))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.STUDY_PASSWORD_WRONG);
    }

    @Test
    void 스터디_리더가_아닐_경우_스터디에서_나간다() {
        // given
        Study mockStudy = mock(Study.class);
        when(studyRepository.findByIdAndStatusNot(1L, StudyStatus.REMOVE)).thenReturn(Optional.of(mockStudy));
        when(mockStudy.isLeader(anyLong())).thenReturn(false);

        // when
        studyService.leaveMember(1L, 1L);

        // then
        verify(membershipService).leave(mockStudy, 1L);
    }

    @Test
    void 스터디_리더일_경우_스터디에서_나갈_수_없다() {
        // given
        Study mockStudy = mock(Study.class);
        when(studyRepository.findByIdAndStatusNot(1L, StudyStatus.REMOVE)).thenReturn(Optional.of(mockStudy));
        when(mockStudy.isLeader(anyLong())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> studyService.leaveMember(1L, 1L))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.LEADER_CAN_NOT_LEAVE);
    }

    @Test
    void 스터디_리더가_스터디를_삭제한다() {
        // given
        Study mockStudy = mock(Study.class);
        when(studyRepository.findByIdAndStatusNot(1L, StudyStatus.REMOVE)).thenReturn(Optional.of(mockStudy));
        when(mockStudy.isLeader(1L)).thenReturn(true);

        // when
        studyService.removeStudy(1L, 1L);

        // then
        verify(membershipService).leave(mockStudy, 1L);
        verify(mockStudy).remove();
    }

    @Test
    void 스터디_멤버가_스터디를_삭제할_수_없다() {
        // given
        Study mockStudy = mock(Study.class);
        when(studyRepository.findByIdAndStatusNot(1L, StudyStatus.REMOVE)).thenReturn(Optional.of(mockStudy));
        when(mockStudy.isLeader(1L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> studyService.removeStudy(1L, 1L))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.REQUIRES_LEADER_PERMISSION);
    }

    @Test
    void 스터디_수정을_한다() {
        // given
        EditStudyDto mockDto = mock(EditStudyDto.class);
        when(mockDto.publicStudy()).thenReturn(false);

        Study mockStudy = mock(Study.class);
        when(mockStudy.isLeader(1L)).thenReturn(true);
        when(studyRepository.findByIdAndStatusNot(1L, StudyStatus.REMOVE)).thenReturn(Optional.of(mockStudy));

        StudyStrategy mockStudyStrategy = mock(StudyStrategy.class);
        when(studyFactory.resolveStudyStrategy(mockDto.publicStudy())).thenReturn(mockStudyStrategy);

        // when
        Long result = studyService.editStudy(1L, 1L, mockDto);

        // then
        verify(mockStudy).updateBasicInfo(mockDto.name(), mockDto.description(), mockDto.totalUserCount());
        verify(mockStudyStrategy).updateStatus(mockStudy, mockDto.password());
        verify(relationService).changeTags(mockStudy, mockDto.workbooks(), mockDto.languages());
        assertThat(result).isEqualTo(mockStudy.getId());
    }

    @Test
    void 스터디장이_아니면_스터디_수정을_할_수_없다() {
        // given
        Study mockStudy = mock(Study.class);
        when(mockStudy.isLeader(1L)).thenReturn(false);
        EditStudyDto mockDto = mock(EditStudyDto.class);
        when(studyRepository.findByIdAndStatusNot(1L, StudyStatus.REMOVE)).thenReturn(Optional.of(mockStudy));

        // when & then
        assertThatThrownBy(() -> studyService.editStudy(1L, 1L, mockDto))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.REQUIRES_LEADER_PERMISSION);
    }

}