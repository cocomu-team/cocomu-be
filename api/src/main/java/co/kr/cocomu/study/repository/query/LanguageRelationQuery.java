package co.kr.cocomu.study.repository.query;

import co.kr.cocomu.study.dto.response.LanguageDto;
import java.util.List;
import java.util.Map;

public interface LanguageRelationQuery {

    Map<Long, List<LanguageDto>> findTagsByStudies(List<Long> studyIds);

    List<LanguageDto> findTagsByStudyId(Long studyId);

}
