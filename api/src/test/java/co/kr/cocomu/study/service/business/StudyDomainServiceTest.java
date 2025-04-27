package co.kr.cocomu.study.service.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.common.exception.domain.NotFoundException;
import co.kr.cocomu.study.domain.Membership;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.LanguageRelation;
import co.kr.cocomu.study.domain.vo.StudyStatus;
import co.kr.cocomu.study.dto.request.CreatePublicStudyDto;
import co.kr.cocomu.study.exception.MembershipExceptionCode;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.study.exception.LanguageRelationExceptionCode;
import co.kr.cocomu.study.repository.LanguageRelationRepository;
import co.kr.cocomu.study.repository.StudyRepository;
import co.kr.cocomu.study.repository.MembershipRepository;
import co.kr.cocomu.tag.domain.LanguageTag;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyDomainServiceTest {

    @Mock private StudyRepository studyRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private LanguageRelationRepository languageRelationRepository;

    @InjectMocks private StudyDomainService studyDomainService;

    @Test
    void 스터디_ID로_스터디를_찾을_수_있다() {
        // given
        final CreatePublicStudyDto dto = new CreatePublicStudyDto("스터디", null, null, null, 0);
        final Study mockStudy = Study.createPublicStudy(dto, 1L);

        when(studyRepository.findByIdAndStatusNot(1L, StudyStatus.REMOVE)).thenReturn(Optional.of(mockStudy));

        // when
        Study actualStudy = studyDomainService.getStudyWithThrow(1L);

        // then
        assertThat(actualStudy).isEqualTo(mockStudy);
    }

    @Test
    void 존재하지_않는_스터디_조회_시_예외가_발생한다() {
        // given
        when(studyRepository.findByIdAndStatusNot(1L, StudyStatus.REMOVE)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyDomainService.getStudyWithThrow(1L))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.NOT_FOUND_STUDY);
    }

    @Test
    void 스터디에_참여중이지_않으면_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long studyId = 1L;

        when(membershipRepository.isUserJoinedStudy(userId, studyId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> studyDomainService.validateMembership(userId, studyId))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", MembershipExceptionCode.NO_PARTICIPATION);
    }

    @Test
    void 참여중인_스터디라면_정상_통과한다() {
        // given
        Long userId = 1L;
        Long studyId = 1L;

        when(membershipRepository.isUserJoinedStudy(userId, studyId)).thenReturn(true);

        // when & then
        assertThatCode(() -> studyDomainService.validateMembership(userId, studyId))
            .doesNotThrowAnyException();
    }

    @Test
    void 스터디_사용자를_찾을_수_있다() {
        // given
        Membership mockMembership = mock(Membership.class);

        when(membershipRepository.findByUser_IdAndStudy_Id(anyLong(), anyLong())).thenReturn(Optional.of(mockMembership));

        // when
        Membership result = studyDomainService.getStudyUserWithThrow(1L, 1L);

        // then
        assertThat(result).isEqualTo(mockMembership);
    }

    @Test
    void 스터디_사용자가_없으면_예외가_발생한다() {
        // given
        when(membershipRepository.findByUser_IdAndStudy_Id(anyLong(), anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyDomainService.getStudyUserWithThrow(1L, 1L))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.NOT_FOUND_STUDY_USER);
    }

    @Test
    void 스터디에서_사용중인_언어_태그를_가져올_수_있다() {
        // given
        LanguageRelation mockLanguageRelation = mock(LanguageRelation.class);
        LanguageTag mockLanguageTag = mock(LanguageTag.class);
        when(mockLanguageRelation.getLanguageTag()).thenReturn(mockLanguageTag);
        when(languageRelationRepository.findByStudy_idAndLanguageTag_IdAndDeletedIsFalse(1L, 1L))
            .thenReturn(Optional.of(mockLanguageRelation));

        // when
        LanguageTag result = studyDomainService.getLanguageTagInStudy(1L, 1L);

        // then
        assertThat(result).isEqualTo(mockLanguageTag);
    }

    @Test
    void 스터디에서_사용중인_언어_태그가_아니면_예외가_발생한다() {
        // given
        when(languageRelationRepository.findByStudy_idAndLanguageTag_IdAndDeletedIsFalse(1L, 1L))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyDomainService.getLanguageTagInStudy(1L, 1L))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", LanguageRelationExceptionCode.INVALID_RELATION);
    }

}