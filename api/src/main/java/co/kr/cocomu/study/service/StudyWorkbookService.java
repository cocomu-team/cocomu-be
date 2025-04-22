package co.kr.cocomu.study.service;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.StudyWorkbook;
import co.kr.cocomu.study.repository.jpa.StudyWorkbookJpaRepository;
import co.kr.cocomu.study.service.business.StudyWorkbookDomainService;
import co.kr.cocomu.workbook.domain.Workbook;
import co.kr.cocomu.workbook.service.WorkbookQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyWorkbookService {

    private final StudyWorkbookJpaRepository studyWorkbookJpaRepository;
    private final WorkbookQueryService workbookQueryService;
    private final StudyWorkbookDomainService studyWorkbookDomainService;

    public void addWorkbooksToStudy(final Study study, final List<Long> workbookIds) {
        final List<Workbook> workbooks = workbookQueryService.getWorkbooksByIdIn(workbookIds);
        final List<StudyWorkbook> studyWorkbooks = StudyWorkbook.createStudyWorkbooks(study, workbooks);
        studyWorkbookJpaRepository.saveAll(studyWorkbooks);
    }

    public void changeWorkbooksToStudy(final Study study, final List<Long> workbookIds) {
        final List<Workbook> allWb = workbookQueryService.getWorkbooksByIdIn(workbookIds);
        final List<StudyWorkbook> existing = studyWorkbookJpaRepository.findAllByStudyId(study.getId());
        studyWorkbookDomainService.activateSelectedWorkbooks(existing, allWb);
        studyWorkbookDomainService.deactivateUnselectedWorkbooks(existing, allWb);

        final List<Workbook> newWorkbooks = studyWorkbookDomainService.extractNewWorkbooks(allWb, existing);
        final List<StudyWorkbook> newStudyWorkbooks = StudyWorkbook.createStudyWorkbooks(study, newWorkbooks);
        studyWorkbookJpaRepository.saveAll(newStudyWorkbooks);
    }

}
