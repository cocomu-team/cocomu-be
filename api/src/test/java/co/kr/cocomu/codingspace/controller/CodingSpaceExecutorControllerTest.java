package co.kr.cocomu.codingspace.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.kr.cocomu.codingspace.controller.code.CodingSpaceApiCode;
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
import co.kr.cocomu.codingspace.dto.response.SpaceStatusDto;
import co.kr.cocomu.codingspace.dto.response.TestCaseDto;
import co.kr.cocomu.codingspace.service.CodingSpaceCommandService;
import co.kr.cocomu.codingspace.service.CodingSpaceQueryService;
import co.kr.cocomu.common.BaseExecutorControllerTest;
import co.kr.cocomu.common.api.Api;
import co.kr.cocomu.common.api.NoContent;
import co.kr.cocomu.common.template.DeleteRequestTemplate;
import co.kr.cocomu.common.template.GetRequestTemplate;
import co.kr.cocomu.common.template.PostRequestTemplate;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

@WebMvcTest(CodingSpaceController.class)
class CodingSpaceExecutorControllerTest extends BaseExecutorControllerTest {

    private static final String PATH_PREFIX = "/api/v1/coding-spaces";

    @MockBean private CodingSpaceCommandService codingSpaceCommandService;
    @MockBean private CodingSpaceQueryService codingSpaceQueryService;

    @Test
    void 코딩_스페이스_생성_요청이_성공한다() {
        // given
        CreateTestCaseDto testCaseDto = new CreateTestCaseDto("input", "output");
        CreateCodingSpaceDto dto = new CreateCodingSpaceDto(1L, 10, 30, 1L, "", "", "", List.of(testCaseDto));
        when(codingSpaceCommandService.createCodingSpace(dto, 1L)).thenReturn(1L);

        // when
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(PATH_PREFIX, dto);

        // then
        Api<CodingSpaceIdDto> result =  response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.CREATE_CODING_SPACE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.CREATE_CODING_SPACE_SUCCESS.getMessage());
        assertThat(result.result().codingSpaceId()).isEqualTo(1L);
    }

    @Test
    void 코딩_스페이스_참여_요청이_성공한다() {
        // given
        when(codingSpaceCommandService.joinCodingSpace(1L, 1L)).thenReturn(1L);

        // when
        String path = PATH_PREFIX + "/1";
        ValidatableMockMvcResponse response = PostRequestTemplate.execute(path);

        // then
        Api<CodingSpaceIdDto> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.JOIN_CODING_SPACE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.JOIN_CODING_SPACE_SUCCESS.getMessage());
        assertThat(result.result().codingSpaceId()).isEqualTo(1L);
    }

    @Test
    void 코딩_스페이스_목록_조회하기() {
        // given
        CodingSpacesDto mockResult = CodingSpacesDto.of(List.of(), Map.of());
        when(codingSpaceQueryService.getCodingSpaces(eq(1L), eq(1L), any(FilterDto.class))).thenReturn(mockResult);

        // when
        String path = PATH_PREFIX + "/studies/1";
        ValidatableMockMvcResponse response = GetRequestTemplate.execute(path);

        // then
        Api<CodingSpacesDto> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.GET_CODING_SPACES_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.GET_CODING_SPACES_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockResult);
    }

    @Test
    void 코딩_스페이스_입장이_성공한다() {
        // given
        when(codingSpaceCommandService.enterSpace(1L, 1L)).thenReturn(CodingSpaceStatus.WAITING);

        // when
        String path = PATH_PREFIX + "/1/enter";
        ValidatableMockMvcResponse response = PostRequestTemplate.execute(path);

        // then
        Api<SpaceStatusDto> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.ENTER_SPACE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.ENTER_SPACE_SUCCESS.getMessage());
        assertThat(result.result().status()).isEqualTo(CodingSpaceStatus.WAITING);
    }

    @Test
    void 코딩_스페이스_대기방_조회_요청이_성공한다() {
        // given
        WaitingPage mockPage = new WaitingPage();
        when(codingSpaceQueryService.extractWaitingPage(1L, 1L)).thenReturn(mockPage);

        // when
        String path = PATH_PREFIX + "/1/waiting-page";
        ValidatableMockMvcResponse response = GetRequestTemplate.execute(path);

        // then
        Api<WaitingPage> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.GET_WAITING_PAGE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.GET_WAITING_PAGE_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockPage);
    }

    @Test
    void 코딩_스페이스_시작_요청이_성공한다() {
        // given
        doNothing().when(codingSpaceCommandService).startSpace(1L, 1L);

        // when
        String path = PATH_PREFIX + "/1/start";
        ValidatableMockMvcResponse response = PostRequestTemplate.execute(path);

        // then
        NoContent result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.START_CODING_SPACE.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.START_CODING_SPACE.getMessage());
    }

    @Test
    void 코딩_스페이스_시작페이지_조회_요청이_성공한다() {
        // given
        StartingPage mockPage = new StartingPage();
        when(codingSpaceQueryService.extractStaringPage(1L, 1L)).thenReturn(mockPage);

        // when
        String path = PATH_PREFIX + "/1/starting-page";
        ValidatableMockMvcResponse response = GetRequestTemplate.execute(path);

        // then
        Api<StartingPage> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.GET_STARTING_PAGE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.GET_STARTING_PAGE_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockPage);
    }

    @Test
    void 코딩_스페이스_피드백_모드_시작_요청이_성공한다() {
        // given
        doNothing().when(codingSpaceCommandService).startFeedback(1L, 1L);

        // when
        String path = PATH_PREFIX + "/1/feedback";
        ValidatableMockMvcResponse response = PostRequestTemplate.execute(path);

        // then
        NoContent result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {
        });
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.START_FEEDBACK_MODE.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.START_FEEDBACK_MODE.getMessage());
    }

    @Test
    void 코딩_스페이스_피드백페이지_조회_요청이_성공한다() {
        // given
        FeedbackPage mockPage = new FeedbackPage();
        when(codingSpaceQueryService.extractFeedbackPage(1L, 1L)).thenReturn(mockPage);

        // when
        String path = PATH_PREFIX + "/1/feedback-page";
        ValidatableMockMvcResponse response = GetRequestTemplate.execute(path);

        // then
        Api<FeedbackPage> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.GET_FEEDBACK_PAGE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.GET_FEEDBACK_PAGE_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockPage);
    }

    @Test
    void 코딩_스페이스_종료_요청이_성공한다() {
        // given
        doNothing().when(codingSpaceCommandService).finishSpace(1L, 1L);

        // when
        String path = PATH_PREFIX + "/1/finish";
        ValidatableMockMvcResponse response = PostRequestTemplate.execute(path);

        // then
        NoContent result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.FINISH_SPACE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.FINISH_SPACE_SUCCESS.getMessage());
    }

    @Test
    void 코딩_스페이스_최종_코드_저장_요청이_성공한다() {
        // given
        SaveCodeDto dto = new SaveCodeDto("code");
        doNothing().when(codingSpaceCommandService).saveFinalCode(1L, 1L, "code");

        // when
        String path = PATH_PREFIX + "/1/save";
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(path, dto);

        // then
        NoContent result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.SAVE_FINAL_CODE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.SAVE_FINAL_CODE_SUCCESS.getMessage());
    }

    @Test
    void 코딩_스페이스_종료_페이지_조회_요청이_성공한다() {
        // given
        FinishPage mockPage = new FinishPage();
        when(codingSpaceQueryService.extractFinishPage(1L, 1L)).thenReturn(mockPage);

        // when
        String path = PATH_PREFIX + "/1/finish-page";
        ValidatableMockMvcResponse response = GetRequestTemplate.execute(path);

        // then
        Api<FinishPage> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.GET_FINISH_PAGE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.GET_FINISH_PAGE_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockPage);
    }

    @Test
    void 코딩_스페이스_테스트_케이스_추가_요청이_성공한다() {
        // given
        CreateTestCaseDto dto = new CreateTestCaseDto("", "");
        TestCaseDto mockResult = new TestCaseDto();
        when(codingSpaceCommandService.addTestCase(1L, 1L, dto)).thenReturn(mockResult);

        // when
        String path = PATH_PREFIX + "/1/test-case";
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(path, dto);

        // then
        Api<TestCaseDto> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.ADD_TEST_CASE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.ADD_TEST_CASE_SUCCESS.getMessage());
        assertThat(result.result()).usingRecursiveComparison().isEqualTo(mockResult);
    }

    @Test
    void 코딩_스페이스_테스트_케이스_삭제_요청이_성공한다() {
        // given
        when(codingSpaceCommandService.deleteTestCase(1L, 1L, 1L)).thenReturn(1L);

        // when
        String path = PATH_PREFIX + "/1/test-cases/1";
        ValidatableMockMvcResponse response = DeleteRequestTemplate.execute(path);

        // then
        Api<Long> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.DELETE_TEST_CASE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.DELETE_TEST_CASE_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(1L);
    }

    @Test
    void 코딩_스페이스_삭제_요청이_성공한다() {
        // given
        doNothing().when(codingSpaceCommandService).deleteSpace(1L, 1L);

        // when
        String path = PATH_PREFIX + "/1";
        ValidatableMockMvcResponse response = DeleteRequestTemplate.execute(path);

        // then
        NoContent result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(CodingSpaceApiCode.DELETE_SPACE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(CodingSpaceApiCode.DELETE_SPACE_SUCCESS.getMessage());
    }

}