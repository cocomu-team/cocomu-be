package co.kr.cocomu.study.business;

import co.kr.cocomu.common.annotation.Business;
import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.TagRelation;
import co.kr.cocomu.study.exception.TagRelationExceptionCode;
import co.kr.cocomu.tag.service.LanguageTagService;
import co.kr.cocomu.tag.service.WorkbookTagService;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Business
@RequiredArgsConstructor
public class RelationBusiness {

    private final WorkbookTagService workbookTagService;
    private final LanguageTagService languageTagService;

    public void validateWorkbookTags(final List<Long> tagIds) {
        if (!workbookTagService.existsAllTagIds(tagIds)) {
            throw new BadRequestException(TagRelationExceptionCode.INVALID_TAG_INFORMATION);
        }
    }

    public void validateLanguageTags(final List<Long> tagIds) {
        if (!languageTagService.existsAllTagIds(tagIds)) {
            throw new BadRequestException(TagRelationExceptionCode.INVALID_TAG_INFORMATION);
        }
    }

    // 추가한 태그 중에 study tag relation 에 포함되지 않는 새로운 태그 만 추출
    public <R extends TagRelation> List<Long> extractNewTagIds(final List<Long> tagIds, final List<R> relations) {
        return tagIds.stream()
            .filter(tagId -> relations.stream().noneMatch(relation -> relation.hasSameTagId(tagId)))
            .toList();
    }

    // 기존 relation 에 존재하는 사용중 or 기존에 사용중이다가 현재 사용하지 않는 태그라면 사용
    public <R extends TagRelation> void activateRelations(final List<R> relations, final List<Long> tagIds) {
        relations.stream()
            .filter(relation -> tagIds.stream().anyMatch(relation::hasSameTagId))
            .forEach(TagRelation::useTag);
    }

    // 기존 relation 에 존재하는 tag 중 새로 사용할 태그에 포함되지 않는 태그 정보라면 사용하지 않음
    public <R extends TagRelation> void deactivateRelations(final List<R> relations, final List<Long> tagIds) {
        relations.stream()
            .filter(relation -> tagIds.stream().noneMatch(relation::hasSameTagId))
            .forEach(TagRelation::unUseTag);
    }

}
