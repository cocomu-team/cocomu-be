package co.kr.cocomu.study.repository.query.impl;

import static co.kr.cocomu.study.domain.QWorkbookRelation.workbookRelation;
import static co.kr.cocomu.tag.domain.QWorkbookTag.workbookTag;

import co.kr.cocomu.study.repository.query.WorkbookRelationQuery;
import co.kr.cocomu.tag.dto.WorkbookDto;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WorkbookRelationQueryImpl implements WorkbookRelationQuery {

    private final JPAQueryFactory queryFactory;

    public Map<Long, List<WorkbookDto>> findTagsByStudies(final List<Long> studyIds) {
        return queryFactory.select(workbookRelation.study.id, workbookTag)
            .from(workbookRelation)
            .join(workbookTag).on(workbookRelation.workbookTagId.eq(workbookTag.id))
            .where(
                workbookRelation.study.id.in(studyIds),
                workbookRelation.deleted.isFalse()
            )
            .transform(GroupBy.groupBy(workbookRelation.study.id)
                .as(GroupBy.list(
                    Projections.fields(
                        WorkbookDto.class,
                        workbookTag.id.as("id"),
                        workbookTag.name.as("name"),
                        workbookTag.imageUrl.as("imageUrl")
                    )
                ))
            );
    }

    public List<WorkbookDto> findTagsByStudyId(final Long studyId) {
        return queryFactory.select(
                Projections.fields(
                    WorkbookDto.class,
                    workbookTag.id.as("id"),
                    workbookTag.name.as("name"),
                    workbookTag.imageUrl.as("imageUrl")
                ))
            .from(workbookRelation)
            .join(workbookTag).on(workbookRelation.workbookTagId.eq(workbookTag.id))
            .where(
                workbookRelation.study.id.eq(studyId),
                workbookRelation.deleted.isFalse()
            )
            .fetch();
    }

}
