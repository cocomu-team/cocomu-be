package co.kr.cocomu.study.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import co.kr.cocomu.language.domain.Language;
import org.junit.jupiter.api.Test;

class StudyLanguageTest {

    @Test
    void 스터디_언어_태그_정보가_같다() {
        // given
        Language mockLanguage = mock(Language.class);
        StudyLanguage studyLanguage = new StudyLanguage(mock(Study.class), mockLanguage);

        // when
        boolean result = studyLanguage.hasSameLanguage(mockLanguage);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 스터디_언어_태그_정보가_다르다() {
        // given
        Language mockLanguage = mock(Language.class);
        StudyLanguage studyLanguage = new StudyLanguage(mock(Study.class), mock(Language.class));

        // when
        boolean result = studyLanguage.hasSameLanguage(mockLanguage);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 스터디_언어_태그를_사용하지_않는다() {
        // given
        StudyLanguage studyLanguage = new StudyLanguage(mock(Study.class), mock(Language.class));

        // when
        studyLanguage.unUseLanguage();

        // then
        assertThat(studyLanguage.isDeleted()).isTrue();
    }

    @Test
    void 스터디_언어_태그를_재사용한다() {
        // given
        StudyLanguage studyLanguage = new StudyLanguage(mock(Study.class), mock(Language.class));
        studyLanguage.unUseLanguage();

        // when
        studyLanguage.useLanguage();

        // then
        assertThat(studyLanguage.isDeleted()).isFalse();
    }

}