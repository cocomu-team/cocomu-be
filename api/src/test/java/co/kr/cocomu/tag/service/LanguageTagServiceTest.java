package co.kr.cocomu.tag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.kr.cocomu.tag.repository.LanguageTagRepository;
import co.kr.cocomu.study.dto.response.LanguageDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
    void 언어태그_ID_목록의_정보가_모두_다_DB에_존재한다() {
        // given
        List<Long> ids = List.of(1L, 2L);
        when(languageTagRepository.countByIdIn(ids)).thenReturn(2);

        // when
        boolean result = languageTagService.existsAllTagIds(ids);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 언어태그_ID_목록의_정보가_모두_다_DB에_존재하지_않는다() {
        // given
        List<Long> ids = List.of(1L, 2L);
        when(languageTagRepository.countByIdIn(ids)).thenReturn(1);

        // when
        boolean result = languageTagService.existsAllTagIds(ids);

        // then
        assertThat(result).isFalse();
    }

    @NullAndEmptySource
    @ParameterizedTest
    void 언어태그_ID_목록이_null이거나_비어있으면_false를_반환한다(List<Long> ids) {
        // when
        boolean result = languageTagService.existsAllTagIds(ids);

        // then
        assertThat(result).isFalse();
    }

}
