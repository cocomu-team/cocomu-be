package co.kr.cocomu.study.service;

import co.kr.cocomu.language.domain.Language;
import co.kr.cocomu.language.service.LanguageQueryService;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.StudyLanguage;
import co.kr.cocomu.study.repository.jpa.StudyLanguageJpaRepository;
import co.kr.cocomu.study.service.business.StudyLanguageDomainService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyLanguageService {

    private final StudyLanguageJpaRepository studyLanguageJpaRepository;
    private final LanguageQueryService languageQueryService;
    private final StudyLanguageDomainService studyLanguageDomainService;

    public void addLanguagesToStudy(final Study study, final List<Long> languageIds) {
        final List<Language> languages = languageQueryService.getLanguagesByIdIn(languageIds);
        final List<StudyLanguage> studyLanguages = StudyLanguage.createStudyLanguages(study, languages);
        studyLanguageJpaRepository.saveAll(studyLanguages);
    }

    public void changeLanguagesToStudy(final Study study, final List<Long> languageIds) {
        final List<Language> allLg = languageQueryService.getLanguagesByIdIn(languageIds);
        final List<StudyLanguage> existing = studyLanguageJpaRepository.findAllByStudyId(study.getId());
        studyLanguageDomainService.activateSelectedLanguages(existing, allLg);
        studyLanguageDomainService.deactivateUnselectedLanguages(existing, allLg);

        final List<Language> newLanguages = studyLanguageDomainService.extractNewLanguages(allLg, existing);
        final List<StudyLanguage> newStudyLanguages = StudyLanguage.createStudyLanguages(study, newLanguages);
        studyLanguageJpaRepository.saveAll(newStudyLanguages);
    }

}
