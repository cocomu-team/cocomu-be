package co.kr.cocomu.study.factory;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.dto.request.CreateStudyDto;
import co.kr.cocomu.study.strategy.PrivateStudyStrategy;
import co.kr.cocomu.study.strategy.PublicStudyStrategy;
import co.kr.cocomu.study.strategy.StudyStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyFactory {

    private final PublicStudyStrategy publicStudyStrategy;
    private final PrivateStudyStrategy privateStudyStrategy;

    public Study generateStudy(final CreateStudyDto dto, final Long leaderId) {
        return Study.create(dto.name(), dto.description(), dto.totalUserCount(), leaderId);
    }

    public StudyStrategy resolveStudyStrategy(final boolean isPublicStatus) {
        if (isPublicStatus) {
            return publicStudyStrategy;
        }
        return privateStudyStrategy;
    }

}
