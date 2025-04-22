package co.kr.cocomu.study.dto.page;

import co.kr.cocomu.study.dto.response.AllStudyCardDto;
import co.kr.cocomu.study.dto.response.LanguageDto;
import co.kr.cocomu.workbook.dto.WorkbookDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "스터디 리스트 페이지 조회 응답")
public class StudyPageDto {

    @Schema(description = "스터디 전체 문제집 정보")
    private List<WorkbookDto> workbooks;
    @Schema(description = "스터디 전체 언어 정보")
    private List<LanguageDto> languages;
    @Schema(description = "스터디 리스트 정보")
    private AllStudyCardDto studies;

}
