package co.kr.cocomu.study.service;

import co.kr.cocomu.study.domain.LanguageRelation;
import co.kr.cocomu.tag.domain.LanguageTag;
import co.kr.cocomu.tag.service.LanguageTagService;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.repository.LanguageRelationRepository;
import co.kr.cocomu.study.service.business.LanguageRelationDomainService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LanguageRelationService {

    private final LanguageRelationRepository languageRelationRepository;
    private final LanguageTagService languageTagService;
    private final LanguageRelationDomainService languageRelationDomainService;

    public void addRelationToStudy(final Study study, final List<Long> tagIds) {
        final List<LanguageTag> tags = languageTagService.getTagsByIdIn(tagIds);
        final List<LanguageRelation> relations = LanguageRelation.createRelations(study, tags);
        languageRelationRepository.saveAll(relations);
    }

    public void changeRelationToStudy(final Study study, final List<Long> tagIds) {
        final List<LanguageTag> tags = languageTagService.getTagsByIdIn(tagIds);
        final List<LanguageRelation> relations = languageRelationRepository.findAllByStudyId(study.getId());
        languageRelationDomainService.activateSelectedRelations(relations, tags);
        languageRelationDomainService.deactivateUnselectedRelations(relations, tags);

        final List<LanguageTag> newTags = languageRelationDomainService.extractNewTags(tags, relations);
        final List<LanguageRelation> newRelations = LanguageRelation.createRelations(study, newTags);
        languageRelationRepository.saveAll(newRelations);
    }

}
