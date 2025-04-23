package co.kr.cocomu.study.service.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.StudyWorkbook;
import co.kr.cocomu.study.repository.StudyWorkbookJpaRepository;
import co.kr.cocomu.study.service.StudyWorkbookService;
import co.kr.cocomu.study.service.business.StudyWorkbookDomainService;
import co.kr.cocomu.tag.domain.WorkbookTag;
import co.kr.cocomu.tag.service.WorkbookService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyWorkbookTagServiceTest {

    @Mock private StudyWorkbookJpaRepository studyWorkbookJpaRepository;
    @Mock private WorkbookService workbookService;
    @Mock private StudyWorkbookDomainService studyWorkbookDomainService;
    @InjectMocks private StudyWorkbookService studyWorkbookService;

    @Test
    void 스터디_문제집_목록을_추가한다() {
        // given
        Study mockStudy = mock(Study.class);
        List<Long> workbookIds = List.of(1L);
        List<WorkbookTag> mockWorkbookTags = List.of(mock(WorkbookTag.class));
        when(workbookService.getWorkbooksByIdIn(workbookIds)).thenReturn(mockWorkbookTags);

        // when
        studyWorkbookService.addWorkbooksToStudy(mockStudy, workbookIds);

        // then
        ArgumentCaptor<List<StudyWorkbook>> captor = ArgumentCaptor.forClass(List.class);
        verify(studyWorkbookJpaRepository).saveAll(captor.capture());
    }

    @Test
    void 스터디_문제집_목록을_변경한다() {
        Study mockStudy = mock(Study.class);
        List<Long> workbookIds = List.of(1L);
        List<WorkbookTag> mockWorkbookTags = List.of(mock(WorkbookTag.class));
        when(workbookService.getWorkbooksByIdIn(workbookIds)).thenReturn(mockWorkbookTags);

        List<StudyWorkbook> mockStudyWorkbooks = List.of(mock(StudyWorkbook.class));
        when(studyWorkbookJpaRepository.findAllByStudyId(mockStudy.getId())).thenReturn(mockStudyWorkbooks);
        when(studyWorkbookDomainService.extractNewWorkbooks(mockWorkbookTags, mockStudyWorkbooks)).thenReturn(List.of());

        // when
        studyWorkbookService.changeWorkbooksToStudy(mockStudy, workbookIds);

        // then
        verify(studyWorkbookDomainService).activateSelectedWorkbooks(mockStudyWorkbooks, mockWorkbookTags);
        verify(studyWorkbookDomainService).deactivateUnselectedWorkbooks(mockStudyWorkbooks, mockWorkbookTags);
        verify(studyWorkbookJpaRepository).saveAll(List.of());
    }

}