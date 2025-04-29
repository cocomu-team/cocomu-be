package co.kr.cocomu.study.service.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.study.domain.LanguageRelation;
import co.kr.cocomu.tag.domain.LanguageTag;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LanguageRelationTagDomainServiceTest {

    @InjectMocks
    LanguageRelationBusiness languageRelationBusiness;

    @Test
    void 추가_문제집_중_기존_문제집에_포함되지_않으면_추출한다() {
        // given
        LanguageTag existingLanguageTag = mock(LanguageTag.class);
        LanguageTag newLanguageTag = mock(LanguageTag.class);

        LanguageRelation existingRelation = mock(LanguageRelation.class);
        when(existingRelation.hasSameLTag(existingLanguageTag)).thenReturn(true);
        when(existingRelation.hasSameLTag(newLanguageTag)).thenReturn(false);

        List<LanguageTag> languageTags = List.of(existingLanguageTag, newLanguageTag);
        List<LanguageRelation> existing = List.of(existingRelation);

        // when
        List<LanguageTag> result = languageRelationBusiness.extractNewTags(languageTags, existing);

        // then
        assertThat(result).hasSize(1);
        assertThat(result).contains(newLanguageTag);
    }

    @Test
    void 추가_문제집_정보가_기존_스터디_문제집에서_사용을_중지했다면_재사용한다() {
        // given
        LanguageTag existingLanguageTag = mock(LanguageTag.class);

        LanguageRelation existingRelation = mock(LanguageRelation.class);
        when(existingRelation.hasSameLTag(existingLanguageTag)).thenReturn(true);

        List<LanguageTag> languageTags = List.of(existingLanguageTag);
        List<LanguageRelation> existing = List.of(existingRelation);

        // when
        languageRelationBusiness.activateSelectedRelations(existing, languageTags);

        // then
        verify(existingRelation).useTag();
    }


    // workbooks 에 포함되지 않은 studyWorkbook 이 있다면, 미사용 상태로 업데이트
    @Test
    void 추가_문제집_정보에_포함되지_않는_기존_스터디_문제집은_사용을_중지한다() {
        // given
        LanguageTag existingLanguageTag = mock(LanguageTag.class);
        LanguageRelation existingRelation = mock(LanguageRelation.class);
        when(existingRelation.hasSameLTag(existingLanguageTag)).thenReturn(false);

        List<LanguageTag> languageTags = List.of(existingLanguageTag);
        List<LanguageRelation> existing = List.of(existingRelation);

        // when
        languageRelationBusiness.deactivateUnselectedRelations(existing, languageTags);

        // then
        verify(existingRelation).unUseTag();
    }

}