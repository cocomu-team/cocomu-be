package co.kr.cocomu.study.repository.query;

import co.kr.cocomu.tag.dto.WorkbookDto;
import java.util.List;
import java.util.Map;

public interface StudyWorkbookQueryRepository {

    Map<Long, List<WorkbookDto>> findWorkbookByStudies(List<Long> studyIds);
    List<WorkbookDto> findWorkbookByStudyId(Long studyId);

}
