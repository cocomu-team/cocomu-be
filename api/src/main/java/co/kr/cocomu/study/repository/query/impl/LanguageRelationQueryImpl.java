package co.kr.cocomu.study.repository.query.impl;

import static co.kr.cocomu.study.domain.QLanguageRelation.languageRelation;
import static co.kr.cocomu.tag.domain.QLanguageTag.languageTag;

import co.kr.cocomu.study.dto.response.LanguageDto;
import co.kr.cocomu.study.repository.query.LanguageRelationQuery;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LanguageRelationQueryImpl implements LanguageRelationQuery {

    private final JPAQueryFactory queryFactory;

    public Map<Long, List<LanguageDto>> findTagsByStudies(final List<Long> studyIds) {
        return queryFactory.select(languageRelation.study.id, languageTag)
            .from(languageRelation)
            .join(languageTag).on(languageRelation.languageTag.id.eq(languageTag.id))
            .where(
                languageRelation.study.id.in(studyIds),
                languageRelation.deleted.isFalse()
            )
            .transform(GroupBy.groupBy(languageRelation.study.id)
                .as(GroupBy.list(
                    Projections.fields(
                        LanguageDto.class,
                        languageTag.id.as("id"),
                        languageTag.name.as("name"),
                        languageTag.imageUrl.as("imageUrl")
                    )
                ))
            );
    }

    public List<LanguageDto> findTagsByStudyId(final Long studyId) {
        return queryFactory.select(
                Projections.fields(
                    LanguageDto.class,
                    languageTag.id.as("id"),
                    languageTag.name.as("name"),
                    languageTag.imageUrl.as("imageUrl")
                ))
            .from(languageRelation)
            .join(languageTag).on(languageRelation.languageTag.id.eq(languageTag.id))
            .where(
                languageRelation.study.id.eq(studyId),
                languageRelation.deleted.isFalse()
            )
            .fetch();
    }

}
