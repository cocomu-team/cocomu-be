package co.kr.cocomu.study.repository.query.impl;

import static co.kr.cocomu.language.domain.QLanguage.language;
import static co.kr.cocomu.study.domain.QStudyLanguage.studyLanguage;

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
        return queryFactory.select(studyLanguage.study.id, language)
            .from(studyLanguage)
            .join(language).on(studyLanguage.language.id.eq(language.id))
            .where(
                studyLanguage.study.id.in(studyIds),
                studyLanguage.deleted.isFalse()
            )
            .transform(GroupBy.groupBy(studyLanguage.study.id)
                .as(GroupBy.list(
                    Projections.fields(
                        LanguageDto.class,
                        language.id.as("id"),
                        language.name.as("name"),
                        language.imageUrl.as("imageUrl")
                    )
                ))
            );
    }

    public List<LanguageDto> findAllLanguagesByStudyId(final Long studyId) {
        return queryFactory.select(
                Projections.fields(
                    LanguageDto.class,
                    language.id.as("id"),
                    language.name.as("name"),
                    language.imageUrl.as("imageUrl")
                ))
            .from(studyLanguage)
            .join(language).on(studyLanguage.language.id.eq(language.id))
            .where(
                studyLanguage.study.id.eq(studyId),
                studyLanguage.deleted.isFalse()
            )
            .fetch();
    }

}
