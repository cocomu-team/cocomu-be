package co.kr.cocomu.study.dto.page;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.dto.response.LanguageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "스터디 상세 페이지 조회 응답")
@AllArgsConstructor
@NoArgsConstructor
public class StudyDetailPageDto {

    @Schema(description = "스터디 식별자", example = "1")
    private Long id;
    @Schema(description = "스터디명", example = "우리 스터디")
    private String name;
    @Schema(description = "스터디 사용 언어 정보")
    private List<LanguageDto> languages;

}
