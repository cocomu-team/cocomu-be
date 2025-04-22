package co.kr.cocomu.admin.controller.docs;

import co.kr.cocomu.admin.dto.request.CreateLanguageRequest;
import co.kr.cocomu.admin.dto.request.CreateWorkbookRequest;
import co.kr.cocomu.common.api.Api;
import co.kr.cocomu.common.api.NoContent;
import co.kr.cocomu.common.exception.dto.ExceptionResponse;
import co.kr.cocomu.study.dto.response.LanguageDto;
import co.kr.cocomu.workbook.dto.WorkbookDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "000. COCOMU-ADMIN", description = "코코무 관리자 관련 API")
public interface AdminControllerDocs {

    @Operation(summary = "문제집 정보 추가", description = "스터디에 문제집 정보를 추가하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "스터디 문제집 정보가 추가되었습니다."
    )
    Api<WorkbookDto> addWorkbook(CreateWorkbookRequest dto);

    @Operation(summary = "언어 정보 추가", description = "스터디에 언어 정보를 추가하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "스터디 언어 정보가 추가되었습니다."
    )
    Api<LanguageDto> addLanguage(CreateLanguageRequest dto);

    @Operation(summary = "문제집 정보 삭제", description = "스터디에 문제집 정보를 삭제하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "스터디 문제집 정보가 삭제되었습니다."
    )
    @ApiResponse(
        responseCode = "404",
        description = "스터디 문제집 정보에 포함되어 있지 않은 문제집입니다.",
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    NoContent deleteWorkbook(Long workbookId);

    @Operation(summary = "언어 정보 삭제", description = "스터디에 언어 정보를 삭제하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "스터디 언어 정보가 삭제되었습니다."
    )
    @ApiResponse(
        responseCode = "404",
        description = "스터디 언어 정보에 포함되어 있지 않은 언어입니다.",
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    NoContent deleteLanguage(Long languageId);

    @Operation(summary = "공용 이미지 업로드", description = "공용으로 사용할 이미지를 업로드하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "공용 이미지 업로드가 성공했습니다."
    )
    Api<String> uploadCommonImage(@RequestBody final MultipartFile image);

}
