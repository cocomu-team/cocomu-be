package co.kr.cocomu.study.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import co.kr.cocomu.study.domain.Study;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublicStudyStrategyTest {

    @InjectMocks private PublicStudyStrategy publicStudyStrategy;

    @Test
    void 공개_스터디_상태로_전환한다() {
        // given
        Study mockStudy = mock(Study.class);

        // when
        publicStudyStrategy.updateStatus(mockStudy, null);

        // then
        verify(mockStudy).updatePublicStatus();
    }


    @Test
    void 공개_스터디는_비밀번호가_언제나_일치한다() {
        // when
        boolean result = publicStudyStrategy.matchPassword(null, "ENCODED");

        // then
        assertThat(result).isTrue();
    }

}