package co.kr.cocomu.study.service;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.StudyWorkbook;
import co.kr.cocomu.study.repository.StudyWorkbookJpaRepository;
import co.kr.cocomu.study.service.business.StudyWorkbookDomainService;
import co.kr.cocomu.tag.domain.WorkbookTag;
import co.kr.cocomu.tag.service.WorkbookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyWorkbookService {

    private final StudyWorkbookJpaRepository studyWorkbookJpaRepository;
    private final WorkbookService workbookService;
    private final StudyWorkbookDomainService studyWorkbookDomainService;

    public void addWorkbooksToStudy(final Study study, final List<Long> workbookIds) {
        final List<WorkbookTag> workbookTags = workbookService.getWorkbooksByIdIn(workbookIds);
        final List<StudyWorkbook> studyWorkbooks = StudyWorkbook.createStudyWorkbooks(study, workbookTags);
        studyWorkbookJpaRepository.saveAll(studyWorkbooks);
    }

    public void changeWorkbooksToStudy(final Study study, final List<Long> workbookIds) {
        final List<WorkbookTag> allWb = workbookService.getWorkbooksByIdIn(workbookIds);
        final List<StudyWorkbook> existing = studyWorkbookJpaRepository.findAllByStudyId(study.getId());
        studyWorkbookDomainService.activateSelectedWorkbooks(existing, allWb);
        studyWorkbookDomainService.deactivateUnselectedWorkbooks(existing, allWb);

        final List<WorkbookTag> newWorkbookTags = studyWorkbookDomainService.extractNewWorkbooks(allWb, existing);
        final List<StudyWorkbook> newStudyWorkbooks = StudyWorkbook.createStudyWorkbooks(study, newWorkbookTags);
        studyWorkbookJpaRepository.saveAll(newStudyWorkbooks);
    }

}
