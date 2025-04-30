package co.kr.cocomu.study.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.business.RelationBusiness;
import co.kr.cocomu.study.domain.TagRelation;
import co.kr.cocomu.study.exception.TagRelationExceptionCode;
import co.kr.cocomu.tag.service.LanguageTagService;
import co.kr.cocomu.tag.service.WorkbookTagService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RelationBusinessTest {

    @Mock private WorkbookTagService workbookTagService;
    @Mock private LanguageTagService languageTagService;
    @InjectMocks
    RelationBusiness relationBusiness;

    @Test
    void 문제집_태그_도메인에서_태그_정보가_모두_존재한다() {
        when(workbookTagService.existsAllTagIds(List.of())). thenReturn(true);

        assertThatCode(() -> relationBusiness.validateWorkbookTags(List.of()))
            .doesNotThrowAnyException();
    }

    @Test
    void 문제집_태그_도메인에서_태그_정보가_모두_존재하지_않을_경우_예외가_발생한다() {
        when(workbookTagService.existsAllTagIds(List.of())). thenReturn(false);

        assertThatThrownBy(() -> relationBusiness.validateWorkbookTags(List.of()))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", TagRelationExceptionCode.INVALID_TAG_INFORMATION);
    }

    @Test
    void 언어_태그_도메인에서_태그_정보가_모두_존재한다() {
        when(languageTagService.existsAllTagIds(List.of())). thenReturn(true);

        assertThatCode(() -> relationBusiness.validateLanguageTags(List.of()))
            .doesNotThrowAnyException();
    }

    @Test
    void 언어_태그_도메인에서_태그_정보가_모두_존재하지_않을_경우_예외가_발생한다() {
        when(languageTagService.existsAllTagIds(List.of())). thenReturn(false);

        assertThatThrownBy(() -> relationBusiness.validateLanguageTags(List.of()))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", TagRelationExceptionCode.INVALID_TAG_INFORMATION);
    }

    @Test
    void 추가되는_스터디_관계_중_기존_관계에_포함되지_않으면_추출한다() {
        // given
        TagRelation existingRelation = mock(TagRelation.class);
        when(existingRelation.hasSameTagId(1L)).thenReturn(true);
        when(existingRelation.hasSameTagId(2L)).thenReturn(false);

        List<Long> tagIds = List.of(1L, 2L);
        List<TagRelation> existing = List.of(existingRelation);

        // when
        List<Long> result = relationBusiness.extractNewTagIds(tagIds, existing);

        // then
        assertThat(result).hasSize(1);
        assertThat(result).contains(2L);
    }

    @Test
    void 추가되는_스터디_관계_정보가_기존_관계에서_포함되었다면_재사용한다() {
        // given
        TagRelation existingRelation = mock(TagRelation.class);
        when(existingRelation.hasSameTagId(1L)).thenReturn(true);

        List<Long> languageTags = List.of(1L);
        List<TagRelation> existing = List.of(existingRelation);

        // when
        relationBusiness.activateRelations(existing, languageTags);

        // then
        verify(existingRelation).useTag();
    }

    @Test
    void 추가되는_스터디_관계에_포함되지_않는_기존_스터디_관계는_사용을_중지한다() {
        // given
        TagRelation existingRelation = mock(TagRelation.class);
        when(existingRelation.hasSameTagId(1L)).thenReturn(false);

        List<Long> languageTags = List.of(1L);
        List<TagRelation> existing = List.of(existingRelation);

        // when
        relationBusiness.deactivateRelations(existing, languageTags);

        // then
        verify(existingRelation).unUseTag();
    }

}