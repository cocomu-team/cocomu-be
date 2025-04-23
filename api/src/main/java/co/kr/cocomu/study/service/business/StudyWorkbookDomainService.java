package co.kr.cocomu.study.service.business;

import co.kr.cocomu.study.domain.StudyWorkbook;
import co.kr.cocomu.tag.domain.WorkbookTag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyWorkbookDomainService {

    // workbooks 에 studyWorkbooks 내 존재하지 않는 새로운 workbook 만 추출
    public List<WorkbookTag> extractNewWorkbooks(final List<WorkbookTag> workbookTags, final List<StudyWorkbook> existing) {
        return workbookTags.stream()
            .filter(wb -> existing.stream().noneMatch(sw -> sw.hasSameWorkbook(wb)))
            .toList();
    }

    // studyWorkbooks 에 존재하는 workbook 이 기존에 사용한 적이 있는 studyWorkbook 이면 다시 사용
    public void activateSelectedWorkbooks(final List<StudyWorkbook> existing, final List<WorkbookTag> workbookTags) {
        existing.stream()
            .filter(sw -> workbookTags.stream().anyMatch(sw::hasSameWorkbook))
            .forEach(StudyWorkbook::useWorkbook);
    }

    // workbooks 에 포함되지 않은 studyWorkbook 이 있다면, 미사용 상태로 업데이트
    public void deactivateUnselectedWorkbooks(final List<StudyWorkbook> existing, final List<WorkbookTag> workbookTags) {
        existing.stream()
            .filter(sw -> workbookTags.stream().noneMatch(sw::hasSameWorkbook))
            .forEach(StudyWorkbook::unUseWorkbook);
    }

}
