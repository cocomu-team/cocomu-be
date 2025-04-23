package co.kr.cocomu.study.service.business;

import co.kr.cocomu.tag.domain.LanguageTag;
import co.kr.cocomu.study.domain.LanguageRelation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LanguageRelationDomainService {

    public List<LanguageTag> extractNewTags(final List<LanguageTag> tags, final List<LanguageRelation> relations) {
        return tags.stream()
            .filter(tag -> relations.stream().noneMatch(relation -> relation.hasSameLTag(tag)))
            .toList();
    }

    public void activateSelectedRelations(final List<LanguageRelation> relations, final List<LanguageTag> tags) {
        relations.stream()
            .filter(relation -> tags.stream().anyMatch(relation::hasSameLTag))
            .forEach(LanguageRelation::useTag);
    }

    public void deactivateUnselectedRelations(final List<LanguageRelation> relations, final List<LanguageTag> tags) {
        relations.stream()
            .filter(relation -> tags.stream().noneMatch(relation::hasSameLTag))
            .forEach(LanguageRelation::unUseTag);
    }

}
