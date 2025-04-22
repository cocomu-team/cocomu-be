package co.kr.cocomu.study.service.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.language.domain.Language;
import co.kr.cocomu.study.domain.StudyLanguage;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyLanguageDomainServiceTest {

    @InjectMocks StudyLanguageDomainService studyLanguageDomainService;

    @Test
    void 추가_문제집_중_기존_문제집에_포함되지_않으면_추출한다() {
        // given
        Language existingLanguage = mock(Language.class);
        Language newLanguage = mock(Language.class);

        StudyLanguage existingRelation = mock(StudyLanguage.class);
        when(existingRelation.hasSameLanguage(existingLanguage)).thenReturn(true);
        when(existingRelation.hasSameLanguage(newLanguage)).thenReturn(false);

        List<Language> languages = List.of(existingLanguage, newLanguage);
        List<StudyLanguage> existing = List.of(existingRelation);

        // when
        List<Language> result = studyLanguageDomainService.extractNewLanguages(languages, existing);

        // then
        assertThat(result).hasSize(1);
        assertThat(result).contains(newLanguage);
    }

    @Test
    void 추가_문제집_정보가_기존_스터디_문제집에서_사용을_중지했다면_재사용한다() {
        // given
        Language existingLanguage = mock(Language.class);

        StudyLanguage existingRelation = mock(StudyLanguage.class);
        when(existingRelation.hasSameLanguage(existingLanguage)).thenReturn(true);

        List<Language> Languages = List.of(existingLanguage);
        List<StudyLanguage> existing = List.of(existingRelation);

        // when
        studyLanguageDomainService.activateSelectedLanguages(existing, Languages);

        // then
        verify(existingRelation).useLanguage();
    }


    // workbooks 에 포함되지 않은 studyWorkbook 이 있다면, 미사용 상태로 업데이트
    @Test
    void 추가_문제집_정보에_포함되지_않는_기존_스터디_문제집은_사용을_중지한다() {
        // given
        Language existingLanguage = mock(Language.class);
        StudyLanguage existingRelation = mock(StudyLanguage.class);
        when(existingRelation.hasSameLanguage(existingLanguage)).thenReturn(false);

        List<Language> Languages = List.of(existingLanguage);
        List<StudyLanguage> existing = List.of(existingRelation);

        // when
        studyLanguageDomainService.deactivateUnselectedLanguages(existing, Languages);

        // then
        verify(existingRelation).unUseLanguage();
    }

}