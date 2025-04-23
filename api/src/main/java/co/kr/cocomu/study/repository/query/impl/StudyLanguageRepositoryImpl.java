package co.kr.cocomu.study.repository.query.impl;

import static co.kr.cocomu.study.domain.QStudyLanguage.studyLanguage;
import static co.kr.cocomu.tag.domain.QLanguageTag.languageTag;

import co.kr.cocomu.study.dto.response.LanguageDto;
import co.kr.cocomu.study.repository.query.StudyLanguageQueryRepository;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudyLanguageRepositoryImpl implements StudyLanguageQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Map<Long, List<LanguageDto>> findLanguageByStudies(final List<Long> studyIds) {
        return queryFactory.select(studyLanguage.study.id, languageTag)
            .from(studyLanguage)
            .join(languageTag).on(studyLanguage.languageTag.id.eq(languageTag.id))
            .where(
                studyLanguage.study.id.in(studyIds),
                studyLanguage.deleted.isFalse()
            )
            .transform(GroupBy.groupBy(studyLanguage.study.id)
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

    public List<LanguageDto> findAllLanguagesByStudyId(final Long studyId) {
        return queryFactory.select(
                Projections.fields(
                    LanguageDto.class,
                    languageTag.id.as("id"),
                    languageTag.name.as("name"),
                    languageTag.imageUrl.as("imageUrl")
                ))
            .from(studyLanguage)
            .join(languageTag).on(studyLanguage.languageTag.id.eq(languageTag.id))
            .where(
                studyLanguage.study.id.eq(studyId),
                studyLanguage.deleted.isFalse()
            )
            .fetch();
    }

}
