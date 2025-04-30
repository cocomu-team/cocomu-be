package co.kr.cocomu.study.service;

import co.kr.cocomu.study.domain.LanguageRelation;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.TagRelation;
import co.kr.cocomu.study.domain.WorkbookRelation;
import co.kr.cocomu.study.repository.LanguageRelationRepository;
import co.kr.cocomu.study.repository.WorkbookRelationRepository;
import co.kr.cocomu.study.service.business.RelationBusiness;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RelationService {

    private final RelationBusiness relationBusiness;
    private final LanguageRelationRepository languageRelationRepository;
    private final WorkbookRelationRepository workbookRelationRepository;

    public void addTags(final Study study, final List<Long> workbookTagIds, final List<Long> languageTagIds) {
        addWorkbookTags(study, workbookTagIds);
        addLanguageTags(study, languageTagIds);
    }

    public void changeTags(final Study study, final List<Long> workbookTagIds, final List<Long> languageTagIds) {
        changeWorkbookTags(study, workbookTagIds);
        changeLanguageTags(study, languageTagIds);
    }

    private void addWorkbookTags(final Study study, final List<Long> tagIds) {
        relationBusiness.validateWorkbookTags(tagIds);
        final List<WorkbookRelation> relations = WorkbookRelation.createRelations(study, tagIds);
        workbookRelationRepository.saveAll(relations);
    }

    private void addLanguageTags(final Study study, final List<Long> tagIds) {
        relationBusiness.validateLanguageTags(tagIds);
        final List<LanguageRelation> relations = LanguageRelation.createRelations(study, tagIds);
        languageRelationRepository.saveAll(relations);
    }

    private void changeWorkbookTags(final Study study, final List<Long> tagIds) {
        relationBusiness.validateWorkbookTags(tagIds);
        final List<WorkbookRelation> relations = workbookRelationRepository.findAllByStudyId(study.getId());
        final List<Long> newTagIds = processTagBusiness(tagIds, relations);
        final List<WorkbookRelation> newRelations = WorkbookRelation.createRelations(study, newTagIds);

        workbookRelationRepository.saveAll(newRelations);
    }

    private void changeLanguageTags(final Study study, final List<Long> tagIds) {
        relationBusiness.validateLanguageTags(tagIds);
        final List<LanguageRelation> relations = languageRelationRepository.findAllByStudyId(study.getId());
        final List<Long> newTagIds = processTagBusiness(tagIds, relations);
        final List<LanguageRelation> newRelations = LanguageRelation.createRelations(study, newTagIds);

        languageRelationRepository.saveAll(newRelations);
    }

    private <R extends TagRelation> List<Long> processTagBusiness(final List<Long> tagIds, final List<R> relations) {
        relationBusiness.activateRelations(relations, tagIds);
        relationBusiness.deactivateRelations(relations, tagIds);
        return relationBusiness.extractNewTagIds(tagIds, relations);
    }

}
