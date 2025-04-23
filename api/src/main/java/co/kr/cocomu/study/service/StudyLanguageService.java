package co.kr.cocomu.study.service;

import co.kr.cocomu.tag.domain.LanguageTag;
import co.kr.cocomu.tag.service.LanguageService;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.StudyLanguage;
import co.kr.cocomu.study.repository.StudyLanguageJpaRepository;
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
    private final LanguageService languageService;
    private final StudyLanguageDomainService studyLanguageDomainService;

    public void addLanguagesToStudy(final Study study, final List<Long> languageIds) {
        final List<LanguageTag> languageTags = languageService.getLanguagesByIdIn(languageIds);
        final List<StudyLanguage> studyLanguages = StudyLanguage.createStudyLanguages(study, languageTags);
        studyLanguageJpaRepository.saveAll(studyLanguages);
    }

    public void changeLanguagesToStudy(final Study study, final List<Long> languageIds) {
        final List<LanguageTag> allLg = languageService.getLanguagesByIdIn(languageIds);
        final List<StudyLanguage> existing = studyLanguageJpaRepository.findAllByStudyId(study.getId());
        studyLanguageDomainService.activateSelectedLanguages(existing, allLg);
        studyLanguageDomainService.deactivateUnselectedLanguages(existing, allLg);

        final List<LanguageTag> newLanguageTags = studyLanguageDomainService.extractNewLanguages(allLg, existing);
        final List<StudyLanguage> newStudyLanguages = StudyLanguage.createStudyLanguages(study, newLanguageTags);
        studyLanguageJpaRepository.saveAll(newStudyLanguages);
    }

}
