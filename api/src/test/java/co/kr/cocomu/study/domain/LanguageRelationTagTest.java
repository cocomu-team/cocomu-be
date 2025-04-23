package co.kr.cocomu.study.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import co.kr.cocomu.tag.domain.LanguageTag;
import org.junit.jupiter.api.Test;

class LanguageRelationTagTest {

    @Test
    void 스터디_언어_태그_정보가_같다() {
        // given
        LanguageTag mockLanguageTag = mock(LanguageTag.class);
        LanguageRelation languageRelation = new LanguageRelation(mock(Study.class), mockLanguageTag);

        // when
        boolean result = languageRelation.hasSameLTag(mockLanguageTag);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 스터디_언어_태그_정보가_다르다() {
        // given
        LanguageTag mockLanguageTag = mock(LanguageTag.class);
        LanguageRelation languageRelation = new LanguageRelation(mock(Study.class), mock(LanguageTag.class));

        // when
        boolean result = languageRelation.hasSameLTag(mockLanguageTag);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 스터디_언어_태그를_사용하지_않는다() {
        // given
        LanguageRelation languageRelation = new LanguageRelation(mock(Study.class), mock(LanguageTag.class));

        // when
        languageRelation.unUseTag();

        // then
        assertThat(languageRelation.isDeleted()).isTrue();
    }

    @Test
    void 스터디_언어_태그를_재사용한다() {
        // given
        LanguageRelation languageRelation = new LanguageRelation(mock(Study.class), mock(LanguageTag.class));
        languageRelation.unUseTag();

        // when
        languageRelation.useTag();

        // then
        assertThat(languageRelation.isDeleted()).isFalse();
    }

}