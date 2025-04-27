package co.kr.cocomu.study.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.Membership;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.dto.request.CreatePrivateStudyDto;
import co.kr.cocomu.study.dto.request.CreatePublicStudyDto;
import co.kr.cocomu.study.dto.request.EditStudyDto;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.study.repository.StudyRepository;
import co.kr.cocomu.study.service.LanguageRelationService;
import co.kr.cocomu.study.service.MembershipService;
import co.kr.cocomu.study.service.PasswordService;
import co.kr.cocomu.study.service.WorkbookRelationService;
import co.kr.cocomu.study.service.business.StudyDomainService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyCommandServiceTest {

    @Mock private StudyRepository studyRepository;
    @Mock private StudyDomainService studyDomainService;
    @Mock private PasswordService passwordService;
    @Mock private WorkbookRelationService workbookRelationService;
    @Mock private MembershipService membershipService;
    @Mock private LanguageRelationService languageRelationService;

    @InjectMocks private StudyCommandService studyCommandService;

    @Test
    void 공개_스터디방_생성과_리더로_참여에_성공한다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("스터디명", List.of(), List.of(), "설명", 10);
        Study mockStudy = mock(Study.class);
        when(studyRepository.save(any(Study.class))).thenReturn(mockStudy);

        // when
        Long result = studyCommandService.createPublicStudy(1L, dto);

        // then
        verify(workbookRelationService).addWorkbooksToStudy(any(Study.class), anyList());
        verify(workbookRelationService).addWorkbooksToStudy(any(Study.class), anyList());
        verify(membershipService).joinLeader(any(Study.class), any(Long.class));
        assertThat(result).isEqualTo(mockStudy.getId());
    }

    @Test
    void 비공개_스터디방_생성과_리더로_참여에_성공한다() {
        // given
        CreatePrivateStudyDto dto = new CreatePrivateStudyDto("스터디명", "", List.of(), List.of(), "설명", 10);
        Study mockStudy = mock(Study.class);
        when(studyRepository.save(any(Study.class))).thenReturn(mockStudy);
        when(passwordService.encodeStudyPassword(anyString())).thenReturn("password");

        // when
        Long result = studyCommandService.createPrivateStudy(dto, 1L);

        // then
        verify(workbookRelationService).addWorkbooksToStudy(any(Study.class), anyList());
        verify(workbookRelationService).addWorkbooksToStudy(any(Study.class), anyList());
        verify(membershipService).joinLeader(any(Study.class), anyLong());
        assertThat(result).isEqualTo(mockStudy.getId());
    }

    @Test
    void 스터디에_일반_사용자로_참여에_성공한다() {
        // given
        Study mockStudy = mock(Study.class);
        when(studyDomainService.getStudyWithThrow(1L)).thenReturn(mockStudy);

        // when
        Long result = studyCommandService.joinPublicStudy(1L, 1L);

        // then
        verify(membershipService).joinMember(mockStudy, 1L);
        assertThat(result).isEqualTo(mockStudy.getId());
    }

    @Test
    void 비공개_스터디에_일반_사용자로_참여한다() {
        // given
        Study mockStudy = mock(Study.class);
        when(studyDomainService.getStudyWithThrow(1L)).thenReturn(mockStudy);

        // when
        Long result = studyCommandService.joinPrivateStudy(1L, 1L, "password");

        // then
        verify(passwordService).validatePassword("password", mockStudy.getPassword());
        verify(membershipService).joinMember(mockStudy, 1L);
        assertThat(result).isEqualTo(mockStudy.getId());
    }

    @Test
    void 스터디_리더가_아닐_경우_스터디에서_나간다() {
        // given
        Study mockStudy = mock(Study.class);
        when(studyDomainService.getStudyWithThrow(1L)).thenReturn(mockStudy);
        when(mockStudy.isLeader(anyLong())).thenReturn(false);

        // when
        studyCommandService.leaveStudy(1L, 1L);

        // then
        verify(membershipService).leave(mockStudy, 1L);
    }

    @Test
    void 스터디_리더일_경우_스터디에서_나갈_수_없다() {
        // given
        Study mockStudy = mock(Study.class);
        when(studyDomainService.getStudyWithThrow(1L)).thenReturn(mockStudy);
        when(mockStudy.isLeader(anyLong())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> studyCommandService.leaveStudy(1L, 1L))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.LEADER_CAN_NOT_LEAVE);
    }

    @Test
    void 스터디를_삭제한다() {
        // given
        Membership mockMembership = mock(Membership.class);
        doNothing().when(mockMembership).removeStudy();
        when(studyDomainService.getStudyUserWithThrow(1L, 1L)).thenReturn(mockMembership);

        // when
        studyCommandService.removeStudy(1L, 1L);

        // then
        verify(mockMembership).removeStudy();
    }

    @Test
    void 공개_스터디_수정을_한다() {
        // given
        Membership mockMembership = mock(Membership.class);
        EditStudyDto mockDto = mock(EditStudyDto.class);
        when(studyDomainService.getStudyUserWithThrow(1L, 1L)).thenReturn(mockMembership);
        when(mockMembership.getStudyId()).thenReturn(1L);

        // when
        Long result = studyCommandService.editPublicStudy(1L, 1L, mockDto);

        // then
        verify(workbookRelationService).changeWorkbooksToStudy(mockMembership.getStudy(), mockDto.workbooks());
        verify(languageRelationService).changeRelationToStudy(mockMembership.getStudy(), mockDto.languages());
        verify(mockMembership).editPublicStudy(mockDto);
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void 비공개_스터디_수정을_한다() {
        // given
        Membership mockMembership = mock(Membership.class);
        EditStudyDto mockDto = mock(EditStudyDto.class);
        when(mockDto.password()).thenReturn("pass");
        when(studyDomainService.getStudyUserWithThrow(1L, 1L)).thenReturn(mockMembership);
        when(passwordService.encodeStudyPassword(anyString())).thenReturn("password");
        when(mockMembership.getStudyId()).thenReturn(1L);

        // when
        Long result = studyCommandService.editPrivateStudy(1L, 1L, mockDto);

        // then
        verify(workbookRelationService).changeWorkbooksToStudy(mockMembership.getStudy(), mockDto.workbooks());
        verify(languageRelationService).changeRelationToStudy(mockMembership.getStudy(), mockDto.languages());
        verify(mockMembership).editPrivateStudy(mockDto, "password");
        assertThat(result).isEqualTo(1L);
    }

}