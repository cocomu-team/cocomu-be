package co.kr.cocomu.codingspace.repository.query.condition;

import static co.kr.cocomu.codingspace.domain.QCodingSpace.codingSpace;
import static co.kr.cocomu.codingspace.domain.QCodingSpaceTab.codingSpaceTab;

import co.kr.cocomu.codingspace.domain.vo.CodingSpaceRole;
import co.kr.cocomu.codingspace.domain.vo.CodingSpaceStatus;
import co.kr.cocomu.codingspace.dto.request.FilterDto;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CodingSpaceCondition {

    public static Predicate[] getCodingSpaceCondition(final FilterDto filter, final Long userId, final Long studyId) {
        if (filter == null) {
            return new Predicate[] { codingSpace.study.id.eq(studyId) };
        }

        return new Predicate[] {
            codingSpace.study.id.eq(studyId),
            getSelectCondition(filter.lastId()),
            getStatusCondition(filter.status()),
            getJoinableCondition(filter.joinable(), userId),
            getSearchCondition(filter.keyword()),
            getLanguageCondition(filter.languageIds())
        };
    }

    public static BooleanExpression isUserJoined(final Long userId) {
        return JPAExpressions.selectOne()
            .from(codingSpaceTab)
            .where(
                codingSpaceTab.codingSpace.eq(codingSpace),
                codingSpaceTab.user.id.eq(userId)
            )
            .exists();
    }

    public static BooleanExpression isHost(final Long userId) {
        return JPAExpressions.selectOne()
            .from(codingSpaceTab)
            .where(
                codingSpaceTab.codingSpace.eq(codingSpace),
                codingSpaceTab.user.id.eq(userId),
                codingSpaceTab.role.eq(CodingSpaceRole.HOST)
            )
            .exists();
    }

    private static BooleanExpression getStatusCondition(final CodingSpaceStatus status) {
        if (status == null) {
            return null;
        }
        return codingSpace.status.eq(status);
    }

    public static BooleanExpression getSelectCondition(final Long lastId) {
        if (lastId == null) {
            return null;
        }
        return codingSpace.id.lt(lastId);
    }

    private static BooleanExpression getJoinableCondition(final Boolean joinable, final Long userId) {
        if (joinable == null || !joinable) {
            return null;
        }

        return codingSpace.currentUserCount.lt(codingSpace.totalUserCount)
            .and(codingSpace.status.eq(CodingSpaceStatus.WAITING))
            .and(userNotJoinedCondition(userId));
    }

    private static BooleanExpression userNotJoinedCondition(final Long userId) {
        return JPAExpressions.selectOne()
            .from(codingSpaceTab)
            .where(
                codingSpaceTab.codingSpace.eq(codingSpace),
                codingSpaceTab.user.id.eq(userId)
            )
            .notExists();
    }

    private static BooleanExpression getSearchCondition(final String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        return codingSpace.name.containsIgnoreCase(keyword);
    }

    private static BooleanExpression getLanguageCondition(final List<Long> languageIds) {
        if (languageIds == null) {
            return null;
        }

        return codingSpace.languageTagId.in(languageIds);
    }

}
