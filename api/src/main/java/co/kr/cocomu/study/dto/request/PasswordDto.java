package co.kr.cocomu.study.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스터디 비밀번호 요청")
public record PasswordDto(
    @Schema(description = "스터디 비밀번호", example = "1234")
    String password
) {
}
