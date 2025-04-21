package co.kr.cocomu.workbook.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "스터디 문제집 응답")
public class WorkbookDto {

    @Schema(description = "스터디 문제집 식별자", example = "1")
    private Long id;
    @Schema(description = "스터디 문제집명", example = "백준")
    private String name;
    @Schema(description = "스터디 문제집 이미지 정보", example = "https://cdn.cocomu.co.kr/..")
    private String imageUrl;

}
