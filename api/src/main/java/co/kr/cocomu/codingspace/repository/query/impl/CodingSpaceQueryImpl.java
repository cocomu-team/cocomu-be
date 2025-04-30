package co.kr.cocomu.codingspace.repository.query.impl;

import static co.kr.cocomu.codingspace.domain.QCodingSpace.codingSpace;
import static co.kr.cocomu.codingspace.domain.QCodingSpaceTab.codingSpaceTab;
import static co.kr.cocomu.codingspace.repository.query.condition.CodingSpaceCondition.getCodingSpaceCondition;
import static co.kr.cocomu.codingspace.repository.query.condition.CodingSpaceCondition.getSelectCondition;
import static co.kr.cocomu.codingspace.repository.query.condition.CodingSpaceCondition.isHost;
import static co.kr.cocomu.codingspace.repository.query.condition.CodingSpaceCondition.isUserJoined;
import static co.kr.cocomu.study.domain.QMembership.membership;
import static co.kr.cocomu.tag.domain.QLanguageTag.languageTag;

import co.kr.cocomu.codingspace.domain.vo.CodingSpaceStatus;
import co.kr.cocomu.codingspace.domain.vo.TabStatus;
import co.kr.cocomu.codingspace.dto.page.FeedbackPage;
import co.kr.cocomu.codingspace.dto.page.FinishPage;
import co.kr.cocomu.codingspace.dto.page.StartingPage;
import co.kr.cocomu.codingspace.dto.page.WaitingPage;
import co.kr.cocomu.codingspace.dto.request.FilterDto;
import co.kr.cocomu.codingspace.dto.response.CodingSpaceDto;
import co.kr.cocomu.codingspace.dto.response.LanguageDto;
import co.kr.cocomu.codingspace.repository.query.CodingSpaceQuery;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CodingSpaceQueryImpl implements CodingSpaceQuery {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CodingSpaceDto> findSpacesWithFilter(
        final Long userId,
        final Long studyId,
        final FilterDto filter
    ) {
        return buildCodingSpaceQuery(userId)
            .from(codingSpace)
            .join(languageTag).on(codingSpace.languageTagId.eq(languageTag.id))
            .where(getCodingSpaceCondition(filter, userId, studyId))
            .orderBy(codingSpace.id.desc())
            .limit(20)
            .fetch();
    }

    @Override
    public List<CodingSpaceDto> findUserSpaces(final Long userId, final Long viewerId, final Long lastIndex) {
        return buildCodingSpaceQuery(viewerId)
            .from(codingSpaceTab)
            .join(codingSpaceTab.codingSpace, codingSpace)
            .join(languageTag).on(codingSpace.languageTagId.eq(languageTag.id))
            .where(getSelectCondition(lastIndex), codingSpaceTab.user.id.eq(userId))
            .orderBy(codingSpace.id.desc())
            .limit(20)
            .fetch();
    }

    private JPAQuery<CodingSpaceDto> buildCodingSpaceQuery(final Long viewerId) {
        return queryFactory
            .select(
                Projections.fields(
                    CodingSpaceDto.class,
                    codingSpace.id.as("id"),
                    isUserJoined(viewerId).as("joinedMe"),
                    codingSpace.name.as("name"),
                    Projections.fields(
                        LanguageDto.class,
                        languageTag.id.as("languageId"),
                        languageTag.name.as("languageName"),
                        languageTag.imageUrl.as("languageImageUrl")
                    ).as("language"),
                    codingSpace.currentUserCount.as("currentUserCount"),
                    codingSpace.totalUserCount.as("totalUserCount"),
                    codingSpace.createdAt.as("createdAt"),
                    codingSpace.status.as("status")
                )
            );
    }

    public Optional<WaitingPage> findWaitingPage(final Long codingSpaceId, final Long userId) {
        return Optional.ofNullable(queryFactory
            .select(Projections.fields(
                WaitingPage.class,
                codingSpace.id.as("id"),
                codingSpace.name.as("name"),
                isHost(userId).as("hostMe"),
                codingSpace.description.as("description"),
                codingSpace.workbookUrl.as("workbookUrl"),
                codingSpace.codingMinutes.as("codingMinutes"),
                codingSpace.totalUserCount.as("totalUserCount"),
                codingSpace.status.as("status"),
                Projections.fields(
                    LanguageDto.class,
                    languageTag.id.as("languageId"),
                    languageTag.name.as("languageName"),
                    languageTag.imageUrl.as("languageImageUrl")
                ).as("language")
            ))
            .from(codingSpaceTab)
            .join(codingSpaceTab.codingSpace, codingSpace)
            .join(languageTag).on(codingSpace.languageTagId.eq(languageTag.id))
            .where(
                codingSpace.status.eq(CodingSpaceStatus.WAITING),
                codingSpace.id.eq(codingSpaceId),
                codingSpaceTab.status.eq(TabStatus.ACTIVE),
                codingSpaceTab.user.id.eq(userId)
            )
            .fetchOne()
        );
    }

    @Override
    public Optional<StartingPage> findStartingPage(final Long codingSpaceId, final Long userId) {
        return Optional.ofNullable(queryFactory
            .select(Projections.fields(
                StartingPage.class,
                codingSpace.id.as("id"),
                codingSpace.name.as("name"),
                codingSpace.description.as("description"),
                codingSpace.workbookUrl.as("workbookUrl"),
                isHost(userId).as("hostMe"),
                codingSpace.codingMinutes.as("codingMinutes"),
                codingSpace.startTime.as("startTime"),
                codingSpaceTab.id.as("tabId"),
                codingSpaceTab.documentKey.as("documentKey"),
                Projections.fields(
                    LanguageDto.class,
                    languageTag.id.as("languageId"),
                    languageTag.name.as("languageName"),
                    languageTag.imageUrl.as("languageImageUrl")
                ).as("language")
            ))
            .from(codingSpaceTab)
            .join(codingSpaceTab.codingSpace, codingSpace)
            .join(languageTag).on(codingSpace.languageTagId.eq(languageTag.id))
            .where(
                codingSpace.status.eq(CodingSpaceStatus.RUNNING),
                codingSpace.id.eq(codingSpaceId),
                codingSpaceTab.status.eq(TabStatus.ACTIVE),
                codingSpaceTab.user.id.eq(userId)
            )
            .fetchOne()
        );
    }

    @Override
    public Optional<FeedbackPage> findFeedbackPage(final Long codingSpaceId, final Long userId) {
        return Optional.ofNullable(queryFactory
            .select(Projections.fields(
                FeedbackPage.class,
                codingSpace.id.as("id"),
                codingSpace.name.as("name"),
                codingSpace.description.as("description"),
                codingSpace.workbookUrl.as("workbookUrl"),
                isHost(userId).as("hostMe"),
                codingSpace.codingMinutes.as("codingMinutes"),
                codingSpace.startTime.as("startTime"),
                codingSpace.finishTime.as("finishTime"),
                Projections.fields(
                    LanguageDto.class,
                    languageTag.id.as("languageId"),
                    languageTag.name.as("languageName"),
                    languageTag.imageUrl.as("languageImageUrl")
                ).as("language")
            ))
            .from(codingSpaceTab)
            .join(codingSpaceTab.codingSpace, codingSpace)
            .join(languageTag).on(codingSpace.languageTagId.eq(languageTag.id))
            .where(
                codingSpace.status.eq(CodingSpaceStatus.FEEDBACK),
                codingSpace.id.eq(codingSpaceId),
                codingSpaceTab.status.eq(TabStatus.ACTIVE),
                codingSpaceTab.user.id.eq(userId)
            )
            .fetchOne()
        );
    }

    @Override
    public Optional<FinishPage> findFinishPage(final Long codingSpaceId, final Long userId) {
        return Optional.ofNullable(queryFactory
            .select(Projections.fields(
                FinishPage.class,
                codingSpace.study.id.as("studyId"),
                codingSpace.id.as("id"),
                codingSpace.name.as("name"),
                codingSpace.description.as("description"),
                codingSpace.workbookUrl.as("workbookUrl"),
                codingSpace.codingMinutes.as("codingMinutes"),
                codingSpace.startTime.as("startTime"),
                codingSpace.finishTime.as("finishTime"),
                Projections.fields(
                    LanguageDto.class,
                    languageTag.id.as("languageId"),
                    languageTag.name.as("languageName"),
                    languageTag.imageUrl.as("languageImageUrl")
                ).as("language")
            ))
            .from(codingSpace)
            .where(
                codingSpace.status.eq(CodingSpaceStatus.FINISHED),
                codingSpace.id.eq(codingSpaceId),
                JPAExpressions
                    .selectOne()
                    .from(membership)
                    .where(
                        membership.study.id.eq(codingSpace.study.id),
                        membership.userId.eq(userId)
                    )
                    .exists()
            )
            .fetchOne()
        );
    }

}
