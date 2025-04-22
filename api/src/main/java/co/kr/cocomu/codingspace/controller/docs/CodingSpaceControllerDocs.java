package co.kr.cocomu.codingspace.controller.docs;

import co.kr.cocomu.codingspace.dto.page.FeedbackPage;
import co.kr.cocomu.codingspace.dto.page.FinishPage;
import co.kr.cocomu.codingspace.dto.page.StartingPage;
import co.kr.cocomu.codingspace.dto.page.StudyInformationPage;
import co.kr.cocomu.codingspace.dto.page.WaitingPage;
import co.kr.cocomu.codingspace.dto.request.CreateCodingSpaceDto;
import co.kr.cocomu.codingspace.dto.request.CreateTestCaseDto;
import co.kr.cocomu.codingspace.dto.request.FilterDto;
import co.kr.cocomu.codingspace.dto.request.SaveCodeDto;
import co.kr.cocomu.codingspace.dto.response.CodingSpaceIdDto;
import co.kr.cocomu.codingspace.dto.response.CodingSpacesDto;
import co.kr.cocomu.codingspace.dto.response.SpaceStatusDto;
import co.kr.cocomu.codingspace.dto.response.TestCaseDto;
import co.kr.cocomu.common.api.Api;
import co.kr.cocomu.common.api.NoContent;
import co.kr.cocomu.common.exception.dto.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "004. COCOMU-CODING-SPACE", description = "코코무 코딩 스페이스 관련 API")
public interface CodingSpaceControllerDocs {

    @Operation(summary = "코딩 스페이스 생성", description = "코딩 스페이스를 생성하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "코딩 스페이스 생성에 성공했습니다."
    )
    @ApiResponse(
        responseCode = "400",
        description = """
                코딩 스페이스의 최대 인원은 4명입니다.
                코딩 스페이스의 최소 인원은 2명입니다.
                해당 스터디에서 사용하지 않는 언어입니다.
                해당 스터디의 스터디원이 아닙니다.
                테스트 케이스는 비어 있을 수 없습니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = """
            존재하지 않는 스터디입니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    Api<CodingSpaceIdDto> createCodingSpace(CreateCodingSpaceDto dto, Long userId);

    @Operation(summary = "코딩 스페이스 참여", description = "코딩 스페이스에 참여하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "코딩 스페이스 참여에 성공했습니다."
    )
    @ApiResponse(
        responseCode = "400",
        description = """
                해당 스터디의 스터디원이 아닙니다.
                이미 코딩 스페이스에 참여되었습니다.
                대기중인 코딩 스페이가 아닙니다.
                코딩 스페이스 최대 인원을 초과헀습니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = """
            존재하지 않는 스터디입니다.
            사용자를 찾을 수 없습니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    Api<CodingSpaceIdDto> joinCodingSpace(Long codingSpaceId, Long userId);

    @Operation(summary = "코딩 스페이스 목록 조회", description = "코딩 스페이스 목록을 조회하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "코딩 스페이스 목록 조회에 성공했습니다."
    )
    @ApiResponse(
        responseCode = "400",
        description = """
            해당 스터디의 스터디원이 아닙니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    Api<CodingSpacesDto> getCodingSpaces(Long studyId, Long userId, FilterDto dto);

    @Operation(summary = "코딩 스페이스 입장", description = "코딩 스페이스에 입장하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "코딩 스페이스 입장에 성공했습니다."
    )
    @ApiResponse(
        responseCode = "400",
        description = """
                코딩 스페이스에 참여중이지 않습니다.
                종료된 코딩 스페이스입니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    Api<SpaceStatusDto> enterSpace(Long codingSpaceId, Long userId);

    @Operation(summary = "코딩 스페이스 대기방 페이지 조회", description = "코딩 스페이스 대기방을 조회하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "코딩 테스트 대기방 조회에 성공했습니다."
    )
    @ApiResponse(
        responseCode = "400",
        description = """
            코딩 스페이스에 입장하지 않았습니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    Api<WaitingPage> getWaitingPage(Long codingSpaceId, Long userId);

    @Operation(summary = "코딩 스페이스 시작", description = "코딩 스페이스 시작을 하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "코딩 스페이스 시작이 성공했습니다."
    )
    @ApiResponse(
        responseCode = "400",
        description = """
                이미 시작된 코딩 스페이스입니다.
                코딩 스페이스에 참여중이지 않습니다.
                유효하지 않은 권한이 없습니다.
                코딩 스페이스에 입장하지 않았습니다.
                스터디 시작 최소 인원은 2명입니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    NoContent startCodingSpace(Long codingSpaceId, Long userId);

    @Operation(summary = "코딩 스페이스 시작 페이지 조회", description = "코딩 스페이스 시작 페에지를 조회하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "코딩 스페이스 시작 페이지 조회에 성공했습니다."
    )
    @ApiResponse(
        responseCode = "400",
        description = """
            코딩 스페이스에 입장하지 않았습니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    Api<StartingPage> getStartingPage(Long codingSpaceId, Long userId);

    @Operation(summary = "코딩 스페이스 피드백 시작", description = "코딩 스페이스 피드백을 시작 하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "피드백 모드가 시작되었습니다."
    )
    @ApiResponse(
        responseCode = "400",
        description = """
                코딩 스페이스에 참여중이지 않습니다.
                유효하지 않은 권한이 없습니다.
                코딩 스페이스에 입장하지 않았습니다.
                스터디 시작 상태일 때만 피드백 모드를 진행할 수 있습니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    NoContent startFeedback(Long codingSpaceId, Long userId);

    @Operation(summary = "코딩 스페이스 피드백 페이지 조회", description = "코딩 스페이스 피드백 페에지를 조회하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "코딩 테스트 피드백 페이지 조회에 성공했습니다."
    )
    @ApiResponse(
        responseCode = "400",
        description = """
            코딩 스페이스에 입장하지 않았습니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    Api<FeedbackPage> getFeedbackPage(Long codingSpaceId, Long userId);

    @Operation(summary = "코딩 스페이스 종료", description = "코딩 스페이스를 종료하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "코딩 스페이스 종료가 성공했습니다."
    )
    @ApiResponse(
        responseCode = "400",
        description = """
                코딩 스페이스에 참여중이지 않습니다.
                코딩 스페이스에 입장하지 않았습니다.
                유효하지 않은 권한이 없습니다.
                스터디 피드백 상태일 때만 종료할 수 있습니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    NoContent finishSpace(Long codingSpaceId, Long userId);

    @Operation(summary = "코딩 스페이스 최종 코드 저장", description = "피드백 후 최종 코드를 저장하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "최종 코드 저장이 성공했습니다."
    )
    NoContent saveFinalCode(Long codingSpaceId, SaveCodeDto dto, Long userId);

    @Operation(summary = "코딩 스페이스 종료 페이지 조회", description = "스터디 종료 페이지를 조회하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "코딩 스페이스 종료 페이지 조회에 성공했습니다."
    )
    @ApiResponse(
        responseCode = "400",
        description = """
            스터디에 참여한 회원이 아닙니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    Api<FinishPage> getFinishPage(Long codingSpaceId, Long userId);

    @Operation(summary = "코딩 스페이스 테스트 케이스 추가", description = "테스트 케이스를 추가하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "코딩 스페이스 테스트 케이스 추가에 성공했습니다."
    )
    @ApiResponse(
        responseCode = "400",
        description = """
                코딩 스페이스에 참여중이지 않습니다.
                활성화 되지 않은 탭 입니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    Api<TestCaseDto> addTestCase(Long codingSpaceId, Long userId, CreateTestCaseDto dto);

    @Operation(summary = "코딩 스페이스 테스트 케이스 삭제", description = "테스트 케이스를 삭제하는 기능")
    @ApiResponse(
        responseCode = "200",
        description = "테스트 케이스 삭제에 성공했습니다."
    )
    @ApiResponse(
        responseCode = "400",
        description = """
                코딩 스페이스에 참여중이지 않습니다.
                활성화 되지 않은 탭 입니다.
                코딩 스페이스에 존재하지 않는 테스트케이스입니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    Api<Long> deleteTestCase(Long codingSpaceId, Long testCaseId, Long userId);

}