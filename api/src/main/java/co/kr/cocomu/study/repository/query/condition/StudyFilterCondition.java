package co.kr.cocomu.study.repository.query.condition;

import static co.kr.cocomu.study.domain.QLanguageRelation.languageRelation;
import static co.kr.cocomu.study.domain.QMembership.membership;
import static co.kr.cocomu.study.domain.QStudy.study;
import static co.kr.cocomu.study.domain.QWorkbookRelation.workbookRelation;

import co.kr.cocomu.study.domain.vo.MembershipStatus;
import co.kr.cocomu.study.domain.vo.StudyStatus;
import co.kr.cocomu.study.dto.request.GetAllStudyFilterDto;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import java.util.List;

public class StudyFilterCondition {

    public static Predicate[] buildStudyFilterCondition(final GetAllStudyFilterDto filter, final Long userId) {
        return new Predicate[] {
            getLanguageRelationCondition(filter.languages()),
            getWorkbookRelationCondition(filter.workbooks()),
            getStatusCondition(filter.status()),
            getJoinableCondition(filter.joinable(), userId),
            getSearchCondition(filter.keyword())
        };
    }

    // 스터디에 참여할 수 있는지 확인하는 조건
    public static BooleanExpression isUserJoined(final Long userId) {
        if (userId == null) {
            return Expressions.asBoolean(Expressions.constant(true)).isTrue();
        }

        return JPAExpressions.selectOne()
            .from(membership)
            .where(
                membership.study.eq(study),
                membership.userId.eq(userId),
                membership.status.eq(MembershipStatus.JOIN)
            )
            .notExists();
    }

    public static BooleanExpression getLastIndexCondition(final Long lastIndex) {
        if (lastIndex == null) {
            return null;
        }

        return study.id.lt(lastIndex);
    }

    // 1. languageRelation 정보에서 조건에 만족하는 studyId들을 가져오기
    private static BooleanExpression getLanguageRelationCondition(final List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return null;
        }

        return JPAExpressions
            .selectOne()
            .from(languageRelation)
            .where(
                languageRelation.study.id.eq(study.id),
                languageRelation.languageTag.id.in(tagIds)
            )
            .exists();
    }

    // 2. workbookRelation 정보에서 조건에 만족하는 studyId들을 가져오기
    private static BooleanExpression getWorkbookRelationCondition(final List<Long> workbookIds) {
        if (workbookIds == null || workbookIds.isEmpty()) {
            return null;
        }

        return JPAExpressions
            .selectOne()
            .from(workbookRelation)
            .where(
                workbookRelation.study.id.eq(study.id),
                workbookRelation.workbookTag.id.in(workbookIds)
            )
            .exists();
    }

    // 3. study 정보에서 status를 만족하는 조건 가져오기
    private static BooleanExpression getStatusCondition(final StudyStatus studyStatus) {
        if (studyStatus == null) {
            return study.status.ne(StudyStatus.REMOVE);
        }
        return study.status.eq(studyStatus);
    }

    // 4. 스터디에 참여할 수 있는지 조건 가져오기
    private static BooleanExpression getJoinableCondition(final Boolean joinable, final Long userId) {
        if (joinable == null || !joinable || userId == null) {
            return null;
        }
        return JPAExpressions.selectOne()
            .from(membership)
            .where(
                membership.study.eq(study),
                membership.userId.eq(userId),
                membership.status.eq(MembershipStatus.JOIN)
            )
            .notExists()
            .and(study.currentUserCount.lt(study.totalUserCount));
    }

    // 5. 스터디 검색 필터링 조건 가져오기
    private static BooleanExpression getSearchCondition(final String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return study.name.containsIgnoreCase(keyword);
    }

}
