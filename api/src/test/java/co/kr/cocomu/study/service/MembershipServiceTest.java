package co.kr.cocomu.study.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.study.domain.Membership;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.repository.MembershipRepository;
import java.util.Optional;
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
        membershipService.joinLeader(mockStudy, 1L);
        // then
        verify(membershipRepository).save(any(Membership.class));
        verify(mockStudy).increaseCurrentUserCount();
    }

    @Test
    void 기존_멤버_정보가_있을_경우_재참여한다() {
        // given
        Study mockStudy = mock(Study.class);
        Membership mockMembership = mock(Membership.class);
        when(membershipRepository.findByUser_IdAndStudy_Id(anyLong(), anyLong()))
            .thenReturn(Optional.of(mockMembership));

        // when
        membershipService.joinMember(mockStudy, 1L);

        // then
        verify(mockMembership).reJoin();
        verify(mockStudy).increaseCurrentUserCount();
    }

    @Test
    void 처음_참여하는_멤버일_경우_DB에_저장한다() {
        // given
        Study mockStudy = mock(Study.class);
        when(membershipRepository.findByUser_IdAndStudy_Id(anyLong(), anyLong()))
            .thenReturn(Optional.empty());

        // when
        membershipService.joinMember(mockStudy, 1L);

        // then
        verify(membershipRepository).save(any(Membership.class));
        verify(mockStudy).increaseCurrentUserCount();
    }

}