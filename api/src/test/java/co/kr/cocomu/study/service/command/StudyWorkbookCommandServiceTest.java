package co.kr.cocomu.study.service.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.StudyWorkbook;
import co.kr.cocomu.study.repository.jpa.StudyWorkbookJpaRepository;
import co.kr.cocomu.study.service.business.StudyWorkbookDomainService;
import co.kr.cocomu.workbook.domain.Workbook;
import co.kr.cocomu.workbook.service.WorkbookQueryService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyWorkbookCommandServiceTest {

    @Mock private StudyWorkbookJpaRepository studyWorkbookJpaRepository;
    @Mock private WorkbookQueryService workbookQueryService;
    @Mock private StudyWorkbookDomainService studyWorkbookDomainService;
    @InjectMocks private StudyWorkbookCommandService studyWorkbookCommandService;

    @Test
    void 스터디_문제집_목록을_추가한다() {
        // given
        Study mockStudy = mock(Study.class);
        List<Long> workbookIds = List.of(1L);
        List<Workbook> mockWorkbooks = List.of(mock(Workbook.class));
        when(workbookQueryService.getWorkbooksByIdIn(workbookIds)).thenReturn(mockWorkbooks);

        // when
        studyWorkbookCommandService.addWorkbooksToStudy(mockStudy, workbookIds);

        // then
        ArgumentCaptor<List<StudyWorkbook>> captor = ArgumentCaptor.forClass(List.class);
        verify(studyWorkbookJpaRepository).saveAll(captor.capture());
    }

    @Test
    void 스터디_문제집_목록을_변경한다() {
        Study mockStudy = mock(Study.class);
        List<Long> workbookIds = List.of(1L);
        List<Workbook> mockWorkbooks = List.of(mock(Workbook.class));
        when(workbookQueryService.getWorkbooksByIdIn(workbookIds)).thenReturn(mockWorkbooks);

        List<StudyWorkbook> mockStudyWorkbooks = List.of(mock(StudyWorkbook.class));
        when(studyWorkbookJpaRepository.findAllByStudyId(mockStudy.getId())).thenReturn(mockStudyWorkbooks);
        when(studyWorkbookDomainService.extractNewWorkbooks(mockWorkbooks, mockStudyWorkbooks)).thenReturn(List.of());

        // when
        studyWorkbookCommandService.changeWorkbooksToStudy(mockStudy, workbookIds);

        // then
        verify(studyWorkbookDomainService).activateSelectedWorkbooks(mockStudyWorkbooks, mockWorkbooks);
        verify(studyWorkbookDomainService).deactivateUnselectedWorkbooks(mockStudyWorkbooks, mockWorkbooks);
        verify(studyWorkbookJpaRepository).saveAll(List.of());
    }

}