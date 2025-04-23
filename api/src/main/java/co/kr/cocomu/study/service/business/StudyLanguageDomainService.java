package co.kr.cocomu.study.service.business;

import co.kr.cocomu.tag.domain.LanguageTag;
import co.kr.cocomu.study.domain.StudyLanguage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyLanguageDomainService {

    public List<LanguageTag> extractNewLanguages(final List<LanguageTag> languageTags, final List<StudyLanguage> existing) {
        return languageTags.stream()
            .filter(lg -> existing.stream().noneMatch(sl -> sl.hasSameLanguage(lg)))
            .toList();
    }

    public void activateSelectedLanguages(final List<StudyLanguage> existing, final List<LanguageTag> languageTags) {
        existing.stream()
            .filter(sl -> languageTags.stream().anyMatch(sl::hasSameLanguage))
            .forEach(StudyLanguage::useLanguage);
    }

    public void deactivateUnselectedLanguages(final List<StudyLanguage> existing, final List<LanguageTag> languageTags) {
        existing.stream()
            .filter(sl -> languageTags.stream().noneMatch(sl::hasSameLanguage))
            .forEach(StudyLanguage::unUseLanguage);
    }

}
