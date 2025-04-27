package co.kr.cocomu.study.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.Membership;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.exception.MembershipExceptionCode;
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
    void 리더로_참여하면_스터디_인원수가_증가한다() {
        // given
        Study mockStudy = mock(Study.class);
        // when
        membershipService.joinLeader(mockStudy, 1L);
        // then
        verify(membershipRepository).save(any(Membership.class));
        verify(mockStudy).increaseCurrentUserCount();
    }

    @Test
    void 기존_멤버_정보가_있을_경우_재참여되고_스터디_인원수가_증가한다() {
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
    void 처음_참여하는_멤버일_경우_DB에_저장하고_스터디_인원수가_증가한다() {
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

    @Test
    void 스터디에서_나가면_스터디_인원수가_감소한다() {
        // given
        Study mockStudy = mock(Study.class);
        Membership mockMembership = mock(Membership.class);
        when(membershipRepository.findByUser_IdAndStudy_Id(anyLong(), anyLong()))
            .thenReturn(Optional.of(mockMembership));

        // when
        membershipService.leave(mockStudy, 1L);

        // then
        verify(mockMembership).leave();
        verify(mockStudy).decreaseCurrentUserCount();
    }

    @Test
    void 미참여_상태에서_나가려고_할_경우_예외가_발생한다() {
        // given
        Study mockStudy = mock(Study.class);
        when(membershipRepository.findByUser_IdAndStudy_Id(anyLong(), anyLong()))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> membershipService.leave(mockStudy, 1L))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", MembershipExceptionCode.NO_PARTICIPATION);
    }

}