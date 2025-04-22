package co.kr.cocomu.workbooks.service;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.workbook.domain.Workbook;
import co.kr.cocomu.workbook.exception.WorkbookExceptionCode;
import co.kr.cocomu.workbook.repository.WorkbookRepository;
import co.kr.cocomu.workbook.service.WorkbookQueryService;
import co.kr.cocomu.workbook.dto.WorkbookDto;
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
public class WorkbookQueryServiceTest {

    @Mock private WorkbookRepository workbookQuery;
    @InjectMocks private WorkbookQueryService workbookQueryService;

    @Test
    void 전체_문제집_정보를_조회한다() {
        // given
        when(workbookQuery.findAll()).thenReturn(List.of(mock(Workbook.class)));

        // when
        List<WorkbookDto> result = workbookQueryService.getAllWorkbooks();

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void 문제집ID_목록에_포함된_문제집을_가져온다() {
        // given
        List<Long> ids = List.of(1L, 2L);
        List<Workbook> mockWorkbooks = List.of(mock(Workbook.class), mock(Workbook.class));
        when(workbookQuery.findAllById(ids)).thenReturn(mockWorkbooks);

        // when
        List<Workbook> result = workbookQueryService.getWorkbooksByIdIn(ids);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    void 문제집ID_목록에_포함된_문제집이_없다면_잘못된_요청이다() {
        // given
        List<Long> ids = List.of(1L, 2L);
        List<Workbook> mockWorkbooks = List.of(mock(Workbook.class));
        when(workbookQuery.findAllById(ids)).thenReturn(mockWorkbooks);

        // when & then
        assertThatThrownBy(() -> workbookQueryService.getWorkbooksByIdIn(ids))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", WorkbookExceptionCode.INVALID_REQUEST);
    }


}
