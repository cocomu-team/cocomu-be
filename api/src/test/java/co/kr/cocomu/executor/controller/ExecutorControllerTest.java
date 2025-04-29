package co.kr.cocomu.executor.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import co.kr.cocomu.admin.config.AdminSecurityConfig;
import co.kr.cocomu.admin.config.AdminUserConfig;
import co.kr.cocomu.common.BaseControllerTest;
import co.kr.cocomu.common.api.NoContent;
import co.kr.cocomu.common.security.PasswordEncoderConfig;
import co.kr.cocomu.common.template.PostRequestTemplate;
import co.kr.cocomu.executor.controller.code.ExecutorApiCode;
import co.kr.cocomu.executor.dto.request.ExecuteDto;
import co.kr.cocomu.executor.dto.request.SubmitDto;
import co.kr.cocomu.executor.service.ExecutorService;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@WebMvcTest(ExecutorController.class)
@Import({AdminUserConfig.class, PasswordEncoderConfig.class, AdminSecurityConfig.class})
class ExecutorControllerTest extends BaseControllerTest {

    private static final String PATH_PREFIX = "/api/v1/executor";

    @MockBean private ExecutorService executorService;

    @Test
    void 코드_실행_요청이_성공한다() {
        // given
        doNothing().when(executorService).execute(any(ExecuteDto.class));
        ExecuteDto dto = new ExecuteDto(1L, "", "", "");

        // when
        String path = PATH_PREFIX + "/execution";
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(path, dto);

        // then
        NoContent result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(ExecutorApiCode.EXECUTE_CODE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(ExecutorApiCode.EXECUTE_CODE_SUCCESS.getMessage());
    }

    @Test
    void 코드_제출_요청이_성공한다() {
        // given
        doNothing().when(executorService).submit(any(SubmitDto.class));
        SubmitDto dto = new SubmitDto(1L, 1L, "", "");

        // when
        String path = PATH_PREFIX + "/submission";
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(path, dto);

        // then
        NoContent result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(ExecutorApiCode.SUBMIT_CODE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(ExecutorApiCode.SUBMIT_CODE_SUCCESS.getMessage());
    }

}