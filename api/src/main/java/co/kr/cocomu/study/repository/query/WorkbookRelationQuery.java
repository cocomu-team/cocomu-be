package co.kr.cocomu.study.repository.query;

import co.kr.cocomu.tag.dto.WorkbookDto;
import java.util.List;
import java.util.Map;

public interface WorkbookRelationQuery {

    Map<Long, List<WorkbookDto>> findTagsByStudies(List<Long> studyIds);
    List<WorkbookDto> findTagsByStudyId(Long studyId);

}
