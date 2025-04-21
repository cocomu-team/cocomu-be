package co.kr.cocomu.study.dto.response;

import co.kr.cocomu.workbook.service.dto.WorkbookDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "스터디 필터링 옵션 응답")
public record FilterOptionsDto(
    @Schema(description = "스터디 전체 문제집 정보")
    List<WorkbookDto> workbooks,
    @Schema(description = "스터디 전체 언어 정보")
    List<LanguageDto> languages
) {
}
