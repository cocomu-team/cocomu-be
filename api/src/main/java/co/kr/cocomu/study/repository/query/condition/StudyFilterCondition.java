package co.kr.cocomu.study.repository.query.condition;

import static co.kr.cocomu.study.domain.QStudy.study;
import static co.kr.cocomu.study.domain.QStudyLanguage.studyLanguage;
import static co.kr.cocomu.study.domain.QStudyUser.studyUser;
import static co.kr.cocomu.study.domain.QStudyWorkbook.studyWorkbook;

import co.kr.cocomu.study.domain.vo.StudyStatus;
import co.kr.cocomu.study.domain.vo.StudyUserStatus;
import co.kr.cocomu.study.dto.request.GetAllStudyFilterDto;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import java.util.List;

public class StudyFilterCondition {

    public static Predicate[] buildStudyFilterCondition(final GetAllStudyFilterDto filter, final Long userId) {
        return new Predicate[] {
            getLanguageCondition(filter.languages()),
            getWorkbookCondition(filter.workbooks()),
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
            .from(studyUser)
            .where(
                studyUser.study.eq(study),
                studyUser.user.id.eq(userId),
                studyUser.status.eq(StudyUserStatus.JOIN)
            )
            .notExists();
    }

    public static BooleanExpression getLastIndexCondition(final Long lastIndex) {
        if (lastIndex == null) {
            return null;
        }

        return study.id.lt(lastIndex);
    }

    // 1. studyLanguage 정보에서 조건에 만족하는 studyId들을 가져오기
    private static BooleanExpression getLanguageCondition(final List<Long> languageIds) {
        if (languageIds == null || languageIds.isEmpty()) {
            return null;
        }

        return JPAExpressions
            .selectOne()
            .from(studyLanguage)
            .where(
                studyLanguage.study.id.eq(study.id),
                studyLanguage.languageTag.id.in(languageIds)
            )
            .exists();
    }

    // 2. studyWorkbook 정보에서 조건에 만족하는 studyId들을 가져오기
    private static BooleanExpression getWorkbookCondition(final List<Long> workbookIds) {
        if (workbookIds == null || workbookIds.isEmpty()) {
            return null;
        }

        return JPAExpressions
            .selectOne()
            .from(studyWorkbook)
            .where(
                studyWorkbook.study.id.eq(study.id),
                studyWorkbook.workbookTag.id.in(workbookIds)
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
            .from(studyUser)
            .where(
                studyUser.study.eq(study),
                studyUser.user.id.eq(userId),
                studyUser.status.eq(StudyUserStatus.JOIN)
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
