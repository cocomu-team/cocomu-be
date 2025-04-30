package co.kr.cocomu.study.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.study.domain.LanguageRelation;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.WorkbookRelation;
import co.kr.cocomu.study.repository.LanguageRelationRepository;
import co.kr.cocomu.study.repository.WorkbookRelationRepository;
import co.kr.cocomu.study.service.business.RelationBusiness;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RelationServiceTest {

    @Mock private RelationBusiness relationBusiness;
    @Mock private LanguageRelationRepository languageRelationRepository;
    @Mock private WorkbookRelationRepository workbookRelationRepository;
    @InjectMocks private RelationService relationService;

    @Test
    void 스터디_태그_관계들을_추가한다() {
        // given
        Study mockStudy = mock(Study.class);
        List<Long> tagIds = List.of(1L);

        // when
        relationService.addTags(mockStudy, tagIds, tagIds);

        // then
        verify(relationBusiness).validateLanguageTags(tagIds);
        verify(relationBusiness).validateWorkbookTags(tagIds);
        verify(languageRelationRepository).saveAll(anyList());
        verify(workbookRelationRepository).saveAll(anyList());
    }

    @Test
    void 스터디_태그_관계들을_변경한다() {
        // given
        Study mockStudy = mock(Study.class);
        List<Long> tagIds = List.of(1L);
        List<LanguageRelation> mockLanguageRelations = List.of(mock(LanguageRelation.class));
        List<WorkbookRelation> mockWorkbookRelations = List.of(mock(WorkbookRelation.class));
        when(languageRelationRepository.findAllByStudyId(mockStudy.getId())).thenReturn(mockLanguageRelations);
        when(workbookRelationRepository.findAllByStudyId(mockStudy.getId())).thenReturn(mockWorkbookRelations);

        List<Long> newTagIds = List.of(2L);
        when(relationBusiness.extractNewTagIds(tagIds, mockLanguageRelations)).thenReturn(newTagIds);
        when(relationBusiness.extractNewTagIds(tagIds, mockWorkbookRelations)).thenReturn(newTagIds);

        // when
        relationService.changeTags(mockStudy, tagIds, tagIds);

        // then
        verify(relationBusiness).activateRelations(mockLanguageRelations, tagIds);
        verify(relationBusiness).activateRelations(mockWorkbookRelations, tagIds);
        verify(relationBusiness).deactivateRelations(mockLanguageRelations, tagIds);
        verify(relationBusiness).deactivateRelations(mockWorkbookRelations, tagIds);
        verify(relationBusiness).validateLanguageTags(tagIds);
        verify(relationBusiness).validateWorkbookTags(tagIds);
        verify(languageRelationRepository).saveAll(anyList());
        verify(workbookRelationRepository).saveAll(anyList());
    }

}