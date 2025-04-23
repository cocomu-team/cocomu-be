package co.kr.cocomu.tag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.tag.domain.LanguageTag;
import co.kr.cocomu.tag.exception.LanguageTagExceptionCode;
import co.kr.cocomu.tag.repository.LanguageTagRepository;
import co.kr.cocomu.study.dto.response.LanguageDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LanguageTagServiceTest {

    @Mock private LanguageTagRepository languageTagRepository;

    @InjectMocks private LanguageTagService languageTagService;

    @Test
    void 스터디_전체_언어_정보를_조회한다() {
        // given
        when(languageTagRepository.findAll()).thenReturn(List.of());

        // when
        List<LanguageDto> result = languageTagService.getAllTags();

        // then
        assertThat(result).hasSize(0);
    }

    @Test
    void 언어ID_목록에_포함된_언어_태그를_가져온다() {
        // given
        List<Long> ids = List.of(1L, 2L);
        List<LanguageTag> mockLanguageTag = List.of(mock(LanguageTag.class), mock(LanguageTag.class));
        when(languageTagRepository.findAllById(ids)).thenReturn(mockLanguageTag);

        // when
        List<LanguageTag> result = languageTagService.getTagsByIdIn(ids);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    void 사용하려는_언어_태그_수와_실제_조회한_언어_태그_수가_다르다면_잘못된_요청이다() {
        // given
        List<Long> ids = List.of(1L, 2L);
        List<LanguageTag> mockLanguageTags = List.of(mock(LanguageTag.class));
        when(languageTagRepository.findAllById(ids)).thenReturn(mockLanguageTags);

        // when & then
        assertThatThrownBy(() -> languageTagService.getTagsByIdIn(ids))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", LanguageTagExceptionCode.INVALID_REQUEST);
    }


}
