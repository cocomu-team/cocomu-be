package co.kr.cocomu.study.service;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.WorkbookRelation;
import co.kr.cocomu.study.repository.WorkbookRelationRepository;
import co.kr.cocomu.study.service.business.WorkbookRelationBusiness;
import co.kr.cocomu.tag.domain.WorkbookTag;
import co.kr.cocomu.tag.service.WorkbookTagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkbookRelationService {

    private final WorkbookRelationRepository workbookRelationRepository;
    private final WorkbookTagService workbookTagService;
    private final WorkbookRelationBusiness workbookRelationBusiness;

    public void addWorkbooksToStudy(final Study study, final List<Long> tagIds) {
        final List<WorkbookTag> tags = workbookTagService.getTagsByIdIn(tagIds);
        final List<WorkbookRelation> relations = WorkbookRelation.createRelations(study, tags);
        workbookRelationRepository.saveAll(relations);
    }

    public void changeWorkbooksToStudy(final Study study, final List<Long> tagIds) {
        final List<WorkbookTag> tags = workbookTagService.getTagsByIdIn(tagIds);
        final List<WorkbookRelation> relations = workbookRelationRepository.findAllByStudyId(study.getId());
        workbookRelationBusiness.activateSelectedRelations(relations, tags);
        workbookRelationBusiness.deactivateUnselectedRelations(relations, tags);

        final List<WorkbookTag> newTags = workbookRelationBusiness.extractNewTags(tags, relations);
        final List<WorkbookRelation> newRelations = WorkbookRelation.createRelations(study, newTags);
        workbookRelationRepository.saveAll(newRelations);
    }

}
