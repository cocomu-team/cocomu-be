package co.kr.cocomu.study.service.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.language.domain.Language;
import co.kr.cocomu.language.service.LanguageQueryService;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.StudyLanguage;
import co.kr.cocomu.study.repository.jpa.StudyLanguageJpaRepository;
import co.kr.cocomu.study.service.business.StudyLanguageDomainService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyLanguageCommandServiceTest {

    @Mock private StudyLanguageJpaRepository studyLanguageJpaRepository;
    @Mock private LanguageQueryService languageQueryService;
    @Mock private StudyLanguageDomainService studyLanguageDomainService;

    @InjectMocks private StudyLanguageCommandService studyLanguageCommandService;

    @Test
    void 스터디_언어_태그_목록을_추가한다() {
        // given
        Study mockStudy = mock(Study.class);
        List<Long> languageIds = List.of(1L);
        List<Language> mockLanguages = List.of(mock(Language.class));
        when(languageQueryService.getLanguagesByIdIn(languageIds)).thenReturn(mockLanguages);

        // when
        studyLanguageCommandService.addLanguagesToStudy(mockStudy, languageIds);

        // then
        ArgumentCaptor<List<StudyLanguage>> captor = ArgumentCaptor.forClass(List.class);
        verify(studyLanguageJpaRepository).saveAll(captor.capture());
    }

    @Test
    void 스터디_문제집_목록을_변경한다() {
        Study mockStudy = mock(Study.class);
        List<Long> languageIds = List.of(1L);
        List<Language> mockLanguages = List.of(mock(Language.class));
        when(languageQueryService.getLanguagesByIdIn(languageIds)).thenReturn(mockLanguages);

        List<StudyLanguage> mockStudyLanguages = List.of(mock(StudyLanguage.class));
        when(studyLanguageJpaRepository.findAllByStudyId(mockStudy.getId())).thenReturn(mockStudyLanguages);
        when(studyLanguageDomainService.extractNewLanguages(mockLanguages, mockStudyLanguages)).thenReturn(List.of());

        // when
        studyLanguageCommandService.changeLanguagesToStudy(mockStudy, languageIds);

        // then
        verify(studyLanguageDomainService).activateSelectedLanguages(mockStudyLanguages, mockLanguages);
        verify(studyLanguageDomainService).deactivateUnselectedLanguages(mockStudyLanguages, mockLanguages);
        verify(studyLanguageJpaRepository).saveAll(List.of());
    }

}