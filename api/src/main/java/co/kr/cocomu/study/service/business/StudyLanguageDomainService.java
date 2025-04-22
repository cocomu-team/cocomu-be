package co.kr.cocomu.study.service.business;

import co.kr.cocomu.language.domain.Language;
import co.kr.cocomu.study.domain.StudyLanguage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyLanguageDomainService {

    public List<Language> extractNewLanguages(final List<Language> languages, final List<StudyLanguage> existing) {
        return languages.stream()
            .filter(lg -> existing.stream().noneMatch(sl -> sl.hasSameLanguage(lg)))
            .toList();
    }

    public void activateSelectedLanguages(final List<StudyLanguage> existing, final List<Language> languages) {
        existing.stream()
            .filter(sl -> languages.stream().anyMatch(sl::hasSameLanguage))
            .forEach(StudyLanguage::useLanguage);
    }

    public void deactivateUnselectedLanguages(final List<StudyLanguage> existing, final List<Language> languages) {
        existing.stream()
            .filter(sl -> languages.stream().noneMatch(sl::hasSameLanguage))
            .forEach(StudyLanguage::unUseLanguage);
    }

}
