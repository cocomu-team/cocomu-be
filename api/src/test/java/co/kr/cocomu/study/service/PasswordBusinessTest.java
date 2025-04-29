package co.kr.cocomu.study.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.study.service.business.PasswordBusiness;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class PasswordBusinessTest {

    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private PasswordBusiness passwordBusiness;

    @Test
    void 비밀번호_검증_성공() {
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // when & then
        assertThatCode(() -> passwordBusiness.validatePassword("password", "encodedPassword"))
            .doesNotThrowAnyException();
    }

    @Test
    void 비밀번호_검증_실패() {
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> passwordBusiness.validatePassword("password", "encodedPassword"))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.STUDY_PASSWORD_WRONG);
    }

    @Test
    void 비밀번호_암호화() {
        // given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // when
        String result = passwordBusiness.encodePassword("password");

        // then
        assertThat(result).isEqualTo("encodedPassword");
    }

}