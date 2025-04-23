package co.kr.cocomu.study.repository.query.impl;

import co.kr.cocomu.study.repository.query.StudyWorkbookQueryRepository;
import co.kr.cocomu.tag.dto.WorkbookDto;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static co.kr.cocomu.study.domain.QStudyWorkbook.studyWorkbook;
import static co.kr.cocomu.tag.domain.QWorkbookTag.workbookTag;

@Repository
@RequiredArgsConstructor
public class StudyWorkbookRepositoryImpl implements StudyWorkbookQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Map<Long, List<WorkbookDto>> findWorkbookByStudies(final List<Long> studyIds) {
        return queryFactory.select(studyWorkbook.study.id, workbookTag)
            .from(studyWorkbook)
            .join(workbookTag).on(studyWorkbook.workbookTag.id.eq(workbookTag.id))
            .where(
                studyWorkbook.study.id.in(studyIds),
                studyWorkbook.deleted.isFalse()
            )
            .transform(GroupBy.groupBy(studyWorkbook.study.id)
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

    public List<WorkbookDto> findWorkbookByStudyId(final Long studyId) {
        return queryFactory.select(
                Projections.fields(
                    WorkbookDto.class,
                    workbookTag.id.as("id"),
                    workbookTag.name.as("name"),
                    workbookTag.imageUrl.as("imageUrl")
                ))
            .from(studyWorkbook)
            .join(workbookTag).on(studyWorkbook.workbookTag.id.eq(workbookTag.id))
            .where(
                studyWorkbook.study.id.eq(studyId),
                studyWorkbook.deleted.isFalse()
            )
            .fetch();
    }

}
