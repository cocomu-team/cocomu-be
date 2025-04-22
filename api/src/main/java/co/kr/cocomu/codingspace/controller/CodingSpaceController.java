package co.kr.cocomu.codingspace.controller;

import co.kr.cocomu.codingspace.controller.code.CodingSpaceApiCode;
import co.kr.cocomu.codingspace.controller.docs.CodingSpaceControllerDocs;
import co.kr.cocomu.codingspace.domain.vo.CodingSpaceStatus;
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
import co.kr.cocomu.codingspace.dto.response.LanguageDto;
import co.kr.cocomu.codingspace.dto.response.SpaceStatusDto;
import co.kr.cocomu.codingspace.dto.response.TestCaseDto;
import co.kr.cocomu.codingspace.service.CodingSpaceCommandService;
import co.kr.cocomu.codingspace.service.CodingSpaceQueryService;
import co.kr.cocomu.common.api.Api;
import co.kr.cocomu.common.api.NoContent;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coding-spaces")
public class CodingSpaceController implements CodingSpaceControllerDocs {

    private final CodingSpaceCommandService codingSpaceCommandService;
    private final CodingSpaceQueryService codingSpaceQueryService;

    @PostMapping
    public Api<CodingSpaceIdDto> createCodingSpace(
        @RequestBody @Valid final CreateCodingSpaceDto dto,
        @AuthenticationPrincipal final Long userId
    ) {
        final Long codingSpaceId = codingSpaceCommandService.createCodingSpace(dto, userId);
        return Api.of(CodingSpaceApiCode.CREATE_CODING_SPACE_SUCCESS, new CodingSpaceIdDto(codingSpaceId));
    }

    @PostMapping("/{codingSpaceId}")
    public Api<CodingSpaceIdDto> joinCodingSpace(
        @PathVariable final Long codingSpaceId,
        @AuthenticationPrincipal final Long userId
    ) {
        final Long tabId = codingSpaceCommandService.joinCodingSpace(codingSpaceId, userId);
        return Api.of(CodingSpaceApiCode.JOIN_CODING_SPACE_SUCCESS, new CodingSpaceIdDto(tabId));
    }

    @GetMapping("/studies/{studyId}")
    public Api<CodingSpacesDto> getCodingSpaces(
        @PathVariable final Long studyId,
        @AuthenticationPrincipal final Long userId,
        @ModelAttribute final FilterDto dto
    ) {
        final CodingSpacesDto result = codingSpaceQueryService.getCodingSpaces(studyId, userId, dto);
        return Api.of(CodingSpaceApiCode.GET_CODING_SPACES_SUCCESS, result);
    }

    @PostMapping("/{codingSpaceId}/enter")
    public Api<SpaceStatusDto> enterSpace(
        @PathVariable final Long codingSpaceId,
        @AuthenticationPrincipal final Long userId
    ) {
        final CodingSpaceStatus status = codingSpaceCommandService.enterSpace(codingSpaceId, userId);
        return Api.of(CodingSpaceApiCode.ENTER_SPACE_SUCCESS, new SpaceStatusDto(status));
    }

    @GetMapping("/{codingSpaceId}/waiting-page")
    public Api<WaitingPage> getWaitingPage(
        @PathVariable final Long codingSpaceId,
        @AuthenticationPrincipal final Long userId
    ) {
        final WaitingPage result = codingSpaceQueryService.extractWaitingPage(codingSpaceId, userId);
        return Api.of(CodingSpaceApiCode.GET_WAITING_PAGE_SUCCESS, result);
    }

    @PostMapping("/{codingSpaceId}/start")
    public NoContent startCodingSpace(
        @PathVariable final Long codingSpaceId,
        @AuthenticationPrincipal final Long userId
    ) {
        codingSpaceCommandService.startSpace(codingSpaceId, userId);
        return NoContent.from(CodingSpaceApiCode.START_CODING_SPACE);
    }

    @GetMapping("/{codingSpaceId}/starting-page")
    public Api<StartingPage> getStartingPage(
        @PathVariable final Long codingSpaceId,
        @AuthenticationPrincipal final Long userId
    ) {
        final StartingPage result = codingSpaceQueryService.extractStaringPage(codingSpaceId, userId);
        return Api.of(CodingSpaceApiCode.GET_STARTING_PAGE_SUCCESS, result);
    }

    @PostMapping("/{codingSpaceId}/feedback")
    public NoContent startFeedback(
        @PathVariable final Long codingSpaceId,
        @AuthenticationPrincipal final Long userId
    ) {
        codingSpaceCommandService.startFeedback(codingSpaceId, userId);
        return NoContent.from(CodingSpaceApiCode.START_FEEDBACK_MODE);
    }

    @GetMapping("/{codingSpaceId}/feedback-page")
    public Api<FeedbackPage> getFeedbackPage(
        @PathVariable final Long codingSpaceId,
        @AuthenticationPrincipal final Long userId
    ) {
        final FeedbackPage result = codingSpaceQueryService.extractFeedbackPage(codingSpaceId, userId);
        return Api.of(CodingSpaceApiCode.GET_FEEDBACK_PAGE_SUCCESS, result);
    }

    @PostMapping("/{codingSpaceId}/finish")
    public NoContent finishSpace(
        @PathVariable final Long codingSpaceId,
        @AuthenticationPrincipal final Long userId
    ) {
        codingSpaceCommandService.finishSpace(codingSpaceId, userId);
        return NoContent.from(CodingSpaceApiCode.FINISH_SPACE_SUCCESS);
    }

    @PostMapping("/{codingSpaceId}/save")
    public NoContent saveFinalCode(
        @PathVariable final Long codingSpaceId,
        @RequestBody @Valid final SaveCodeDto dto,
        @AuthenticationPrincipal final Long userId
    ) {
        codingSpaceCommandService.saveFinalCode(codingSpaceId, userId, dto.code());
        return NoContent.from(CodingSpaceApiCode.SAVE_FINAL_CODE_SUCCESS);
    }

    @GetMapping("/{codingSpaceId}/finish-page")
    public Api<FinishPage> getFinishPage(
        @PathVariable final Long codingSpaceId,
        @AuthenticationPrincipal final Long userId
    ) {
        final FinishPage result = codingSpaceQueryService.extractFinishPage(codingSpaceId, userId);
        return Api.of(CodingSpaceApiCode.GET_FINISH_PAGE_SUCCESS, result);
    }

    @PostMapping("/{codingSpaceId}/test-case")
    public Api<TestCaseDto> addTestCase(
        @PathVariable final Long codingSpaceId,
        @AuthenticationPrincipal final Long userId,
        @RequestBody final CreateTestCaseDto dto
    ) {
        final TestCaseDto result = codingSpaceCommandService.addTestCase(codingSpaceId, userId, dto);
        return Api.of(CodingSpaceApiCode.ADD_TEST_CASE_SUCCESS, result);
    }

    @DeleteMapping("/{codingSpaceId}/test-cases/{testCaseId}")
    public Api<Long> deleteTestCase(
        @PathVariable final Long codingSpaceId,
        @PathVariable final Long testCaseId,
        @AuthenticationPrincipal final Long userId
    ) {
        final Long result = codingSpaceCommandService.deleteTestCase(codingSpaceId, userId, testCaseId);
        return Api.of(CodingSpaceApiCode.DELETE_TEST_CASE_SUCCESS, result);
    }

    @DeleteMapping("/{codingSpaceId}")
    public NoContent deleteCodingSpace(
        @PathVariable final Long codingSpaceId,
        @AuthenticationPrincipal final Long userId
    ) {
        codingSpaceCommandService.deleteSpace(codingSpaceId, userId);
        return NoContent.from(CodingSpaceApiCode.DELETE_SPACE_SUCCESS);
    }

}
