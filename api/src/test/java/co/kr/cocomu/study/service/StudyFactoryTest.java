package co.kr.cocomu.study.service;

import static org.junit.jupiter.api.Assertions.*;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.dto.request.CreateStudyDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyFactoryTest {

    @InjectMocks
    private StudyFactory studyFactory;

    @Test
    void 스터디_객체를_생성한다() {
        CreateStudyDto dto = new CreateStudyDto("name", true, "", "", 0, null, null);
        Study study = studyFactory.generateStudy(dto, 0L);
        assertNotNull(study);
    }

}