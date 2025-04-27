package co.kr.cocomu.study.repository.query.impl;

import static co.kr.cocomu.study.domain.QMembership.membership;
import static co.kr.cocomu.user.domain.QUser.user;

import co.kr.cocomu.study.domain.vo.MembershipRole;
import co.kr.cocomu.study.domain.vo.MembershipStatus;
import co.kr.cocomu.study.dto.request.StudyUserFilterDto;
import co.kr.cocomu.study.dto.response.LeaderDto;
import co.kr.cocomu.study.dto.response.StudyMemberDto;
import co.kr.cocomu.study.repository.query.MembershipQuery;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MembershipQueryImpl implements MembershipQuery {

    private final JPAQueryFactory queryFactory;

    public Map<Long, LeaderDto> findLeaderByStudies(final List<Long> studyIds) {
        return queryFactory.select(membership.study.id, user)
            .from(membership)
            .join(user).on(membership.userId.eq(user.id))
            .where(
                membership.study.id.in(studyIds),
                membership.role.eq(MembershipRole.LEADER),
                membership.status.eq(MembershipStatus.JOIN)
            )
            .transform(GroupBy.groupBy(membership.study.id).as(
                Projections.fields(
                    LeaderDto.class,
                    user.id.as("id"),
                    user.nickname.as("nickname"),
                    user.profileImageUrl.as("profileImageUrl")
                )
            ));
    }

    public LeaderDto findLeaderByStudyId(final Long studyId) {
        return queryFactory.select(Projections.fields(
                LeaderDto.class,
                user.id.as("id"),
                user.nickname.as("nickname"),
                user.profileImageUrl.as("profileImageUrl")
            ))
            .from(membership)
            .join(user).on(membership.userId.eq(user.id))
            .where(
                membership.study.id.eq(studyId),
                membership.role.eq(MembershipRole.LEADER),
                membership.status.eq(MembershipStatus.JOIN)
            )
            .fetchOne();
    }

    @Override
    public List<StudyMemberDto> findMembers(final Long studyId, final StudyUserFilterDto filter) {
        return queryFactory.select(Projections.fields(
                StudyMemberDto.class,
                membership.id.as("studyUserId"),
                user.id.as("userId"),
                user.nickname.as("nickname"),
                user.profileImageUrl.as("profileImageUrl"),
                membership.role.as("role"),
                membership.createdAt.as("joinedDate")
            ))
            .from(membership)
            .join(user).on(membership.userId.eq(user.id))
            .where(
                membership.study.id.eq(studyId),
                membership.status.eq(MembershipStatus.JOIN),
                getSelectCondition(filter.lastNickname(), filter.lastIndex())
            )
            .orderBy(getOrderSpecifiers(filter.lastNickname()))
            .limit(20)
            .fetch();
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(final String lastNickname) {
        if (lastNickname == null || lastNickname.isEmpty()) {
            return new OrderSpecifier[] {
                membership.role.when(MembershipRole.LEADER).then(0).otherwise(1).asc(),
                user.nickname.asc(),
                membership.id.asc()
            };
        }

        return new OrderSpecifier[]{ user.nickname.asc(), membership.id.asc() };
    }

    private BooleanExpression getSelectCondition(final String lastNickname, final Long lastIndex) {
        if ((lastNickname == null || lastNickname.isEmpty()) && lastIndex == null) {
            return null;
        }

        if (lastIndex == null) {
            return user.nickname.gt(lastNickname).and(membership.role.eq(MembershipRole.MEMBER));
        }

        if (lastNickname == null) {
            return membership.id.gt(lastIndex).and(membership.role.eq(MembershipRole.MEMBER));
        }

        return (user.nickname.gt(lastNickname)
            .or(user.nickname.eq(lastNickname).and(membership.id.gt(lastIndex))))
            .and(membership.role.eq(MembershipRole.MEMBER));
    }

}
