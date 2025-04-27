package co.kr.cocomu.study.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import co.kr.cocomu.study.domain.Membership;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.repository.MembershipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    @Mock private MembershipRepository membershipRepository;
    @InjectMocks private MembershipService membershipService;

    @Test
    void 리더로_참여한다() {
        // given
        Study mockStudy = mock(Study.class);
        // when
        membershipService.joinLeader(mockStudy, anyLong());
        // then
        verify(membershipRepository).save(any(Membership.class));
        verify(mockStudy).increaseCurrentUserCount();
    }

}