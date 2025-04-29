package co.kr.cocomu.study.service.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.WorkbookRelation;
import co.kr.cocomu.study.repository.WorkbookRelationRepository;
import co.kr.cocomu.study.service.WorkbookRelationService;
import co.kr.cocomu.study.service.business.WorkbookRelationBusiness;
import co.kr.cocomu.tag.domain.WorkbookTag;
import co.kr.cocomu.tag.service.WorkbookTagService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkbookRelationTagServiceTest {

    @Mock private WorkbookRelationRepository workbookRelationRepository;
    @Mock private WorkbookTagService workbookTagService;
    @Mock private WorkbookRelationBusiness workbookRelationBusiness;
    @InjectMocks private WorkbookRelationService workbookRelationService;

    @Test
    void 스터디_문제집_목록을_추가한다() {
        // given
        Study mockStudy = mock(Study.class);
        List<Long> workbookIds = List.of(1L);
        List<WorkbookTag> mockWorkbookTags = List.of(mock(WorkbookTag.class));
        when(workbookTagService.getTagsByIdIn(workbookIds)).thenReturn(mockWorkbookTags);

        // when
        workbookRelationService.addWorkbooksToStudy(mockStudy, workbookIds);

        // then
        ArgumentCaptor<List<WorkbookRelation>> captor = ArgumentCaptor.forClass(List.class);
        verify(workbookRelationRepository).saveAll(captor.capture());
    }

    @Test
    void 스터디_문제집_목록을_변경한다() {
        Study mockStudy = mock(Study.class);
        List<Long> workbookIds = List.of(1L);
        List<WorkbookTag> mockWorkbookTags = List.of(mock(WorkbookTag.class));
        when(workbookTagService.getTagsByIdIn(workbookIds)).thenReturn(mockWorkbookTags);

        List<WorkbookRelation> mockWorkbookRelations = List.of(mock(WorkbookRelation.class));
        when(workbookRelationRepository.findAllByStudyId(mockStudy.getId())).thenReturn(mockWorkbookRelations);
        when(workbookRelationBusiness.extractNewTags(mockWorkbookTags, mockWorkbookRelations)).thenReturn(List.of());

        // when
        workbookRelationService.changeWorkbooksToStudy(mockStudy, workbookIds);

        // then
        verify(workbookRelationBusiness).activateSelectedRelations(mockWorkbookRelations, mockWorkbookTags);
        verify(workbookRelationBusiness).deactivateUnselectedRelations(mockWorkbookRelations, mockWorkbookTags);
        verify(workbookRelationRepository).saveAll(List.of());
    }

}