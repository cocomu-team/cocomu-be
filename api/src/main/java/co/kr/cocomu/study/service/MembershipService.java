package co.kr.cocomu.study.service;

import co.kr.cocomu.study.domain.Membership;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.vo.MembershipRole;
import co.kr.cocomu.study.repository.MembershipRepository;
import co.kr.cocomu.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public void joinLeader(final Study study, final Long userId) {
        final Membership membership = Membership.createLeader(study, userId);
        membershipRepository.save(membership);
        study.increaseCurrentUserCount();
    }

}
