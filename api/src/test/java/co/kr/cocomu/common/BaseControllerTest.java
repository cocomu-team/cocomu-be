package co.kr.cocomu.common;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import co.kr.cocomu.common.jwt.JwtProvider;
import co.kr.cocomu.common.security.SecurityConfig;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.WebApplicationContext;

@Import(SecurityConfig.class)
public class BaseControllerTest {

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        doNothing().when(jwtProvider).validationTokenWithThrow(anyString());
        given(jwtProvider.getUserId(anyString())).willReturn(1L);
    }

}
