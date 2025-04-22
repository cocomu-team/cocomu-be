package co.kr.cocomu.study.repository.query;

import co.kr.cocomu.study.dto.response.LanguageDto;
import java.util.List;
import java.util.Map;

public interface StudyLanguageQueryRepository {

    Map<Long, List<LanguageDto>> findLanguageByStudies(List<Long> studyIds);

    List<LanguageDto> findLanguageByStudyId(Long studyId);

}
