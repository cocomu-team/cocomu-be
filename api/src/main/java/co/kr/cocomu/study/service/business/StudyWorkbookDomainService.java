package co.kr.cocomu.study.service.business;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.StudyWorkbook;
import co.kr.cocomu.study.repository.jpa.StudyWorkbookJpaRepository;
import co.kr.cocomu.workbook.domain.Workbook;
import co.kr.cocomu.workbook.service.WorkbookQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyWorkbookDomainService {

    // workbooks 에 studyWorkbooks 내 존재하지 않는 새로운 workbook 만 추출
    public List<Workbook> extractNewWorkbooks(final List<Workbook> workbooks, final List<StudyWorkbook> existing) {
        return workbooks.stream()
            .filter(wb -> existing.stream().noneMatch(sw -> sw.hasSameWorkbook(wb)))
            .toList();
    }

    // studyWorkbooks 에 존재하는 workbook 이 기존에 사용한 적이 있는 studyWorkbook 이면 다시 사용
    public void activateSelectedWorkbooks(final List<StudyWorkbook> existing, final List<Workbook> workbooks) {
        existing.stream()
            .filter(sw -> workbooks.stream().anyMatch(sw::hasSameWorkbook))
            .forEach(StudyWorkbook::useWorkbook);
    }

    // workbooks 에 포함되지 않은 studyWorkbook 이 있다면, 미사용 상태로 업데이트
    public void deactivateUnselectedWorkbooks(final List<StudyWorkbook> existing, final List<Workbook> workbooks) {
        existing.stream()
            .filter(sw -> workbooks.stream().noneMatch(sw::hasSameWorkbook))
            .forEach(StudyWorkbook::unUseWorkbook);
    }

}
