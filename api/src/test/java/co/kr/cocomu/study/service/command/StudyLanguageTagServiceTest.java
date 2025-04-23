package co.kr.cocomu.study.service.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.tag.domain.LanguageTag;
import co.kr.cocomu.tag.service.LanguageService;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.StudyLanguage;
import co.kr.cocomu.study.repository.StudyLanguageJpaRepository;
import co.kr.cocomu.study.service.StudyLanguageService;
import co.kr.cocomu.study.service.business.StudyLanguageDomainService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyLanguageTagServiceTest {

    @Mock private StudyLanguageJpaRepository studyLanguageJpaRepository;
    @Mock private LanguageService languageService;
    @Mock private StudyLanguageDomainService studyLanguageDomainService;

    @InjectMocks private StudyLanguageService studyLanguageService;

    @Test
    void 스터디_언어_태그_목록을_추가한다() {
        // given
        Study mockStudy = mock(Study.class);
        List<Long> languageIds = List.of(1L);
        List<LanguageTag> mockLanguageTags = List.of(mock(LanguageTag.class));
        when(languageService.getLanguagesByIdIn(languageIds)).thenReturn(mockLanguageTags);

        // when
        studyLanguageService.addLanguagesToStudy(mockStudy, languageIds);

        // then
        ArgumentCaptor<List<StudyLanguage>> captor = ArgumentCaptor.forClass(List.class);
        verify(studyLanguageJpaRepository).saveAll(captor.capture());
    }

    @Test
    void 스터디_문제집_목록을_변경한다() {
        Study mockStudy = mock(Study.class);
        List<Long> languageIds = List.of(1L);
        List<LanguageTag> mockLanguageTags = List.of(mock(LanguageTag.class));
        when(languageService.getLanguagesByIdIn(languageIds)).thenReturn(mockLanguageTags);

        List<StudyLanguage> mockStudyLanguages = List.of(mock(StudyLanguage.class));
        when(studyLanguageJpaRepository.findAllByStudyId(mockStudy.getId())).thenReturn(mockStudyLanguages);
        when(studyLanguageDomainService.extractNewLanguages(mockLanguageTags, mockStudyLanguages)).thenReturn(List.of());

        // when
        studyLanguageService.changeLanguagesToStudy(mockStudy, languageIds);

        // then
        verify(studyLanguageDomainService).activateSelectedLanguages(mockStudyLanguages, mockLanguageTags);
        verify(studyLanguageDomainService).deactivateUnselectedLanguages(mockStudyLanguages, mockLanguageTags);
        verify(studyLanguageJpaRepository).saveAll(List.of());
    }

}