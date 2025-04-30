package co.kr.cocomu.study.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "스터디 생성 요청")
public record CreateStudyDto(
    @NotBlank(message = "스터디명은 필수입니다.")
    @Schema(description = "스터디명", example = "매일 열심히하는 스터디")
    String name,
    @NotNull
    @Schema(description = "공개 스터디 여부")
    boolean publicStudy,
    @Schema(description = "스터디 암호", example = "1234")
    String password,
    @NotNull
    @Schema(description = "스터디 설명", example = "우리 스터디는 ~ ")
    String description,
    @NotNull
    @Schema(description = "스터디 최대 인원", example = "80")
    int totalUserCount,
    @NotEmpty(message = "스터디 언어태그 정보는 필수입니다.")
    @Schema(description = "스터디에서 사용할 언어 태그 식별자", example = "[1, 2]")
    List<Long> languageTagIds,
    @NotEmpty
    @Schema(description = "스터디에서 사용할 문제집 태그 식별자", example = "[1, 2]")
    List<Long> workbookTagIds
) {
}