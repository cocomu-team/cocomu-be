package co.kr.cocomu.tag.service;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.tag.domain.WorkbookTag;
import co.kr.cocomu.tag.exception.WorkbookTagExceptionCode;
import co.kr.cocomu.tag.repository.WorkbookTagRepository;
import co.kr.cocomu.tag.dto.WorkbookDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock private WorkbookTagRepository workbookQuery;
    @InjectMocks private WorkbookTagService workbookTagService;

    @Test
    void 전체_문제집_정보를_조회한다() {
        // given
        when(workbookQuery.findAll()).thenReturn(List.of(mock(WorkbookTag.class)));

        // when
        List<WorkbookDto> result = workbookTagService.getAllWorkbooks();

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void 문제집ID_목록에_포함된_문제집을_가져온다() {
        // given
        List<Long> ids = List.of(1L, 2L);
        List<WorkbookTag> mockWorkbookTags = List.of(mock(WorkbookTag.class), mock(WorkbookTag.class));
        when(workbookQuery.findAllById(ids)).thenReturn(mockWorkbookTags);

        // when
        List<WorkbookTag> result = workbookTagService.getTagsByIdIn(ids);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    void 문제집ID_목록에_포함된_문제집이_없다면_잘못된_요청이다() {
        // given
        List<Long> ids = List.of(1L, 2L);
        List<WorkbookTag> mockWorkbookTags = List.of(mock(WorkbookTag.class));
        when(workbookQuery.findAllById(ids)).thenReturn(mockWorkbookTags);

        // when & then
        assertThatThrownBy(() -> workbookTagService.getTagsByIdIn(ids))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", WorkbookTagExceptionCode.INVALID_REQUEST);
    }


}
