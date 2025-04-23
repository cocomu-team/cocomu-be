package co.kr.cocomu.study.service.business;

import co.kr.cocomu.study.domain.WorkbookRelation;
import co.kr.cocomu.tag.domain.WorkbookTag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkbookRelationDomainService {

    // workbooks 에 studyWorkbooks 내 존재하지 않는 새로운 workbook 만 추출
    public List<WorkbookTag> extractNewTags(final List<WorkbookTag> tags, final List<WorkbookRelation> relations) {
        return tags.stream()
            .filter(tag -> relations.stream().noneMatch(relation -> relation.hasSameTag(tag)))
            .toList();
    }

    // studyWorkbooks 에 존재하는 workbook 이 기존에 사용한 적이 있는 studyWorkbook 이면 다시 사용
    public void activateSelectedRelations(final List<WorkbookRelation> relations, final List<WorkbookTag> tags) {
        relations.stream()
            .filter(relation -> tags.stream().anyMatch(relation::hasSameTag))
            .forEach(WorkbookRelation::useTag);
    }

    // workbooks 에 포함되지 않은 studyWorkbook 이 있다면, 미사용 상태로 업데이트
    public void deactivateUnselectedRelations(final List<WorkbookRelation> relations, final List<WorkbookTag> tags) {
        relations.stream()
            .filter(relation -> tags.stream().noneMatch(relation::hasSameTag))
            .forEach(WorkbookRelation::unUseTag);
    }

}
