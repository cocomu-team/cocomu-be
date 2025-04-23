package co.kr.cocomu.study.service.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.study.domain.LanguageRelation;
import co.kr.cocomu.tag.domain.LanguageTag;
import co.kr.cocomu.tag.service.LanguageTagService;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.repository.LanguageRelationRepository;
import co.kr.cocomu.study.service.LanguageRelationService;
import co.kr.cocomu.study.service.business.LanguageRelationDomainService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LanguageRelationTagServiceTest {

    @Mock private LanguageRelationRepository languageRelationRepository;
    @Mock private LanguageTagService languageTagService;
    @Mock private LanguageRelationDomainService languageRelationDomainService;

    @InjectMocks private LanguageRelationService languageRelationService;

    @Test
    void 스터디_언어_태그_목록을_추가한다() {
        // given
        Study mockStudy = mock(Study.class);
        List<Long> languageIds = List.of(1L);
        List<LanguageTag> mockLanguageTags = List.of(mock(LanguageTag.class));
        when(languageTagService.getTagsByIdIn(languageIds)).thenReturn(mockLanguageTags);

        // when
        languageRelationService.addRelationToStudy(mockStudy, languageIds);

        // then
        ArgumentCaptor<List<LanguageRelation>> captor = ArgumentCaptor.forClass(List.class);
        verify(languageRelationRepository).saveAll(captor.capture());
    }

    @Test
    void 스터디_문제집_목록을_변경한다() {
        Study mockStudy = mock(Study.class);
        List<Long> languageIds = List.of(1L);
        List<LanguageTag> mockLanguageTags = List.of(mock(LanguageTag.class));
        when(languageTagService.getTagsByIdIn(languageIds)).thenReturn(mockLanguageTags);

        List<LanguageRelation> mockLanguageRelations = List.of(mock(LanguageRelation.class));
        when(languageRelationRepository.findAllByStudyId(mockStudy.getId())).thenReturn(mockLanguageRelations);
        when(languageRelationDomainService.extractNewTags(mockLanguageTags, mockLanguageRelations)).thenReturn(List.of());

        // when
        languageRelationService.changeRelationToStudy(mockStudy, languageIds);

        // then
        verify(languageRelationDomainService).activateSelectedRelations(mockLanguageRelations, mockLanguageTags);
        verify(languageRelationDomainService).deactivateUnselectedRelations(mockLanguageRelations, mockLanguageTags);
        verify(languageRelationRepository).saveAll(List.of());
    }

}