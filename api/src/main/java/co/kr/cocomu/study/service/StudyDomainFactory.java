package co.kr.cocomu.study.service;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.dto.request.CreateStudyDto;
import org.springframework.stereotype.Component;

@Component
public class StudyDomainFactory {

    public Study generateStudy(final CreateStudyDto dto, final Long leaderId) {
        return Study.create(dto.name(), dto.description(), dto.totalUserCount(), leaderId);
    }

}
