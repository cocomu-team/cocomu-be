package co.kr.cocomu.study.factory;

import static org.assertj.core.api.Assertions.assertThat;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.dto.request.CreateStudyDto;
import co.kr.cocomu.study.strategy.PrivateStudyStrategy;
import co.kr.cocomu.study.strategy.PublicStudyStrategy;
import co.kr.cocomu.study.strategy.StudyStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyFactoryTest {

    @Mock private PublicStudyStrategy publicStudyStrategy;
    @Mock private PrivateStudyStrategy privateStudyStrategy;
    @InjectMocks private StudyFactory studyFactory;

    @Test
    void 스터디_객체를_생성한다() {
        // given
        CreateStudyDto dto = new CreateStudyDto("name", true, "", "", 0, null, null);

        // when
        Study study = studyFactory.generateStudy(dto, 1L);

        // then
        assertThat(study.getCurrentUserCount()).isEqualTo(0);
        assertThat(study.getTotalUserCount()).isEqualTo(0);
        assertThat(study.getLeaderId()).isEqualTo(1L);
        assertThat(study.getName()).isEqualTo("name");
        assertThat(study.getDescription()).isEqualTo("");
    }

    @Test
    void 공개_스터디_전략을_반환한다() {
        // when
        StudyStrategy result = studyFactory.resolveStudyStrategy(true);
        // then
        assertThat(result).isInstanceOf(PublicStudyStrategy.class);
    }

    @Test
    void 비공개_스터디_전략을_반환한다() {
        // when
        StudyStrategy result = studyFactory.resolveStudyStrategy(false);
        // then
        assertThat(result).isInstanceOf(PrivateStudyStrategy.class);
    }

}