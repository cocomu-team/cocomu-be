package co.kr.cocomu.study.repository.query.impl;
import static co.kr.cocomu.study.domain.QMembership.membership;
import static co.kr.cocomu.study.domain.QStudy.study;
import static co.kr.cocomu.study.repository.query.condition.StudyFilterCondition.getLastIndexCondition;
import static co.kr.cocomu.study.repository.query.condition.StudyFilterCondition.isUserJoined;

import co.kr.cocomu.study.domain.vo.StudyStatus;
import co.kr.cocomu.study.dto.request.GetAllStudyFilterDto;
import co.kr.cocomu.study.dto.response.StudyCardDto;
import co.kr.cocomu.study.repository.query.StudyQueryRepository;
import co.kr.cocomu.study.repository.query.condition.StudyFilterCondition;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

// todO: DB Access 는 IntegrationTest에서 집중 테스트
@Repository
@RequiredArgsConstructor
public class StudyQueryRepositoryImpl implements StudyQueryRepository {

    private static final int PAGE_MIN_OFFSET = 0;
    private static final int STUDY_PAGE_SIZE = 12;
    private static final int STUDY_SCROLL_SIZE = 20;
    private final JPAQueryFactory queryFactory;

    public Long countStudyCardsWithFilter(final GetAllStudyFilterDto filter, final Long userId) {
        return queryFactory.select(study.count())
            .from(study)
            .where(StudyFilterCondition.buildStudyFilterCondition(filter, userId))
            .fetchOne();
    }

    public List<StudyCardDto> findTop12StudyCardsWithFilter(final GetAllStudyFilterDto filter, final Long userId) {
        return buildStudyPageForm(userId)
            .from(study)
            .where(StudyFilterCondition.buildStudyFilterCondition(filter, userId))
            .orderBy(study.id.desc())
            .offset(pageOffset(filter.page()))
            .limit(STUDY_PAGE_SIZE)
            .fetch();
    }

    @Override
    public List<StudyCardDto> findTop20UserStudyCards(final Long userId, final Long viewerId, final Long lastIndex) {
        return buildStudyPageForm(viewerId)
            .from(membership)
            .join(membership.study, study)
            .where(
                getLastIndexCondition(lastIndex),
                membership.userId.eq(userId),
                study.status.ne(StudyStatus.REMOVE)
            )
            .orderBy(study.id.desc())
            .limit(STUDY_SCROLL_SIZE)
            .fetch();
    }

    public Optional<StudyCardDto> findStudyPagesByStudyId(final Long studyId, final Long userId) {
        return Optional.ofNullable(
            buildStudyPageForm(userId)
                .from(study)
                .where(
                    study.id.eq(studyId),
                    study.status.ne(StudyStatus.REMOVE)
                )
                .fetchOne()
        );
    }

    private JPAQuery<StudyCardDto> buildStudyPageForm(final Long userId) {
        return queryFactory.select(
                Projections.fields(
                    StudyCardDto.class,
                    study.id.as("id"),
                    study.name.as("name"),
                    study.status.as("status"),
                    study.description.as("description"),
                    study.currentUserCount.as("currentUserCount"),
                    study.totalUserCount.as("totalUserCount"),
                    study.createdAt.as("createdAt"),
                    isUserJoined(userId).as("joinable")
                )
            );
    }

    private long pageOffset(final Long page) {
        if (page == null || page <= 0) {
            return PAGE_MIN_OFFSET;
        }
        return (page - 1) * STUDY_PAGE_SIZE;
    }

}
