package co.kr.cocomu.admin.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import co.kr.cocomu.admin.service.AdminService;
import co.kr.cocomu.admin.controller.code.AdminApiCode;
import co.kr.cocomu.admin.dto.request.CreateJudgeRequest;
import co.kr.cocomu.admin.dto.request.CreateLanguageRequest;
import co.kr.cocomu.admin.dto.response.JudgeResponse;
import co.kr.cocomu.admin.dto.response.LanguageResponse;
import co.kr.cocomu.common.BaseControllerTest;
import co.kr.cocomu.common.api.Api;
import co.kr.cocomu.common.api.NoContent;
import co.kr.cocomu.common.template.DeleteRequestTemplate;
import co.kr.cocomu.common.template.PostRequestTemplate;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(AdminController.class)
@WithMockUser(roles = "ADMIN")
class AdminControllerTest extends BaseControllerTest {

    private static final String PATH_PREFIX = "/api/v1/admin";
    @MockBean private AdminService adminService;

    @Test
    void 스터디_문제집_추가_요청이_성공한다() {
        CreateJudgeRequest dto = new CreateJudgeRequest("백준", "이미지URL");
        JudgeResponse mockResponse = new JudgeResponse(1L, "백준", "이미지URL");
        given(adminService.addJudge(dto)).willReturn(mockResponse);

        // when
        String path = PATH_PREFIX + "/studies/judges";
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(path, dto);

        // then
        Api<JudgeResponse> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(AdminApiCode.ADD_JUDGE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(AdminApiCode.ADD_JUDGE_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockResponse);
    }

    @Test
    void 스터디_언어_추가_요청이_성공한다() {
        CreateLanguageRequest dto = new CreateLanguageRequest("자바", "이미지URL");
        LanguageResponse mockResponse = new LanguageResponse(1L, "자바", "이미지URL");
        given(adminService.addLanguage(dto)).willReturn(mockResponse);

        // when
        String path = PATH_PREFIX + "/studies/languages";
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(path, dto);

        // then
        Api<LanguageResponse> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(AdminApiCode.ADD_LANGUAGE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(AdminApiCode.ADD_LANGUAGE_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockResponse);
    }

    @Test
    void 스터디_문제집_삭제_요청이_성공한다() {
        // when
        String path = PATH_PREFIX + "/studies/judges/1";
        ValidatableMockMvcResponse response = DeleteRequestTemplate.execute(path);

        // then
        NoContent result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(AdminApiCode.DELETE_JUDGE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(AdminApiCode.DELETE_JUDGE_SUCCESS.getMessage());
        verify(adminService).deleteJudge(1L);
    }

    @Test
    void 스터디_언어_삭제_요청이_성공한다() {
        // when
        String path = PATH_PREFIX + "/studies/languages/1";
        ValidatableMockMvcResponse response = DeleteRequestTemplate.execute(path);

        // then
        NoContent result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(AdminApiCode.DELETE_LANGUAGE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(AdminApiCode.DELETE_LANGUAGE_SUCCESS.getMessage());
        verify(adminService).deleteLanguage(1L);
    }

}