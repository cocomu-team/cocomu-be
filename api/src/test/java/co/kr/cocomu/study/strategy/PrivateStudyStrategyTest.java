package co.kr.cocomu.study.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class PrivateStudyStrategyTest {

    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private PrivateStudyStrategy privateStudyStrategy;

    @ParameterizedTest
    @NullAndEmptySource
    void 비공개_스터디_전환시_비밀번호가_없으면_안된다(String password) {
        // when then
        assertThatThrownBy(() -> privateStudyStrategy.updateStatus(mock(Study.class), password))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.REQUIRED_STUDY_PASSWORD);
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "1234567"})
    void 비공개_스터디_전환시_비밀번호가_4자_미만이거나_6자를_초과하면_안된다(String password) {
        // when then
        assertThatThrownBy(() -> privateStudyStrategy.updateStatus(mock(Study.class), password))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.WRONG_PASSWORD_RULES);
    }

    @Test
    void 비공개_스터디_전환시_비밀번호가_4자_미만이거나_6자를_초과하면_안된다() {
        // given
        Study mockStudy = mock(Study.class);
        when(passwordEncoder.encode("1234")).thenReturn("encode");
        // when
        privateStudyStrategy.updateStatus(mockStudy, "1234");
        // then
        verify(mockStudy).updatePrivateStatus("encode");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 비밀번호_정보가_비어있으면_검증이_실패한다(String password) {
        boolean result = privateStudyStrategy.matchPassword(password, "encode");
        assertFalse(result);
    }

    @Test
    void 비밀번호_정보가_맞다면_검증이_성공한다() {
        // given
        when(passwordEncoder.matches("password", "encode")).thenReturn(true);
        // when
        boolean result = privateStudyStrategy.matchPassword("password", "encode");
        // then
        assertTrue(result);
    }

}