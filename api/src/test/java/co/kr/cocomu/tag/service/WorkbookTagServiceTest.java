package co.kr.cocomu.tag.service;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.tag.domain.WorkbookTag;
import co.kr.cocomu.study.exception.TagRelationExceptionCode;
import co.kr.cocomu.tag.repository.WorkbookTagRepository;
import co.kr.cocomu.tag.dto.WorkbookDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkbookTagServiceTest {

    @Mock private WorkbookTagRepository workbookTagRepository;
    @InjectMocks private WorkbookTagService workbookTagService;

    @Test
    void 전체_문제집_정보를_조회한다() {
        // given
        when(workbookTagRepository.findAll()).thenReturn(List.of(mock(WorkbookTag.class)));

        // when
        List<WorkbookDto> result = workbookTagService.getAllWorkbooks();

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void 문제집_ID_목록의_정보가_모두_다_DB에_존재한다() {
        // given
        List<Long> ids = List.of(1L, 2L);
        when(workbookTagRepository.countByIdIn(ids)).thenReturn(2);

        // when
        boolean result = workbookTagService.existsAllTagIds(ids);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 문제집_ID_목록의_정보가_모두_다_DB에_존재하지_않는다() {
        // given
        List<Long> ids = List.of(1L, 2L);
        when(workbookTagRepository.countByIdIn(ids)).thenReturn(1);

        // when
        boolean result = workbookTagService.existsAllTagIds(ids);

        // then
        assertThat(result).isFalse();
    }

    @NullAndEmptySource
    @ParameterizedTest
    void 문제집_ID_목록이_null이거나_비어있으면_false를_반환한다(List<Long> ids) {
        // when
        boolean result = workbookTagService.existsAllTagIds(ids);

        // then
        assertThat(result).isFalse();
    }


}
