package co.kr.cocomu.study.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.codingspace.service.CodingSpaceQueryService;
import co.kr.cocomu.common.exception.domain.NotFoundException;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.dto.page.StudyDetailPageDto;
import co.kr.cocomu.study.dto.request.GetAllStudyFilterDto;
import co.kr.cocomu.study.dto.request.StudyUserFilterDto;
import co.kr.cocomu.study.dto.response.AllStudyCardDto;
import co.kr.cocomu.study.dto.response.LanguageDto;
import co.kr.cocomu.study.dto.response.LeaderDto;
import co.kr.cocomu.study.dto.response.StudyCardDto;
import co.kr.cocomu.study.dto.response.StudyMemberDto;
import co.kr.cocomu.study.repository.query.StudyLanguageQueryRepository;
import co.kr.cocomu.study.repository.query.StudyWorkbookQueryRepository;
import co.kr.cocomu.study.service.business.StudyDomainService;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.study.repository.StudyRepository;
import co.kr.cocomu.study.repository.StudyUserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyQueryServiceTest {

    @Mock private StudyRepository studyQuery;
    @Mock private StudyUserRepository studyUserQuery;
    @Mock private StudyWorkbookQueryRepository studyWorkbookQuery;
    @Mock private StudyLanguageQueryRepository studyLanguageQuery;
    @Mock private StudyDomainService studyDomainService;
    @Mock private CodingSpaceQueryService codingSpaceQueryService;

    @InjectMocks private StudyQueryService studyQueryService;

    @Test
    void 전체_스터디_카드_정보를_가져온다() {
        // given
        Long totalCount = 10L;
        GetAllStudyFilterDto dto = new GetAllStudyFilterDto(null, null, null, null, null, null);

        Map<Long, List<LanguageDto>> languageByStudies = new HashMap<>();
        languageByStudies.put(1L, List.of(new LanguageDto(), new LanguageDto()));

        Map<Long, LeaderDto> leaderByStudies = new HashMap<>();
        LeaderDto mockLeader = new LeaderDto();
        leaderByStudies.put(1L, mockLeader);

        StudyCardDto mockStudyCard = new StudyCardDto();
        mockStudyCard.setId(1L);

        when(studyQuery.countStudyCardsWithFilter(dto, 1L)).thenReturn(totalCount);
        when(studyQuery.findTop12StudyCardsWithFilter(dto, 1L)).thenReturn(List.of(mockStudyCard));
        when(studyLanguageQuery.findLanguageByStudies(anyList())).thenReturn(languageByStudies);
        when(studyWorkbookQuery.findWorkbookByStudies(anyList())).thenReturn(new HashMap<>());
        when(studyUserQuery.findLeaderByStudies(anyList())).thenReturn(leaderByStudies);

        // when
        AllStudyCardDto result = studyQueryService.getAllStudyCard(dto, 1L);

        // then
        StudyCardDto studyCard = result.studies().getFirst();
        assertThat(result.totalStudyCount()).isEqualTo(totalCount);
        assertThat(result.studies()).hasSize(1);
        assertThat(studyCard.getId()).isEqualTo(mockStudyCard.getId());
        assertThat(studyCard.getLanguages()).hasSize(2);
        assertThat(studyCard.getWorkbooks()).hasSize(0);
        assertThat(studyCard.getLeader()).isEqualTo(mockLeader);
    }

    @Test
    void 스터디_카드_정보를_조회한다() {
        // given
        Long studyId = 1L;
        Long userId = 1L;

        StudyCardDto mockStudyCard = new StudyCardDto();
        LeaderDto mockLeader = new LeaderDto();
        when(studyQuery.findStudyPagesByStudyId(studyId, 1L)).thenReturn(Optional.of(mockStudyCard));
        when(studyLanguageQuery.findAllLanguagesByStudyId(studyId)).thenReturn(List.of());
        when(studyWorkbookQuery.findWorkbookByStudyId(studyId)).thenReturn(List.of());
        when(studyUserQuery.findLeaderByStudyId(studyId)).thenReturn(mockLeader);

        // when
        StudyCardDto result = studyQueryService.getStudyCard(studyId, userId);

        // then
        assertThat(result.getId()).isEqualTo(mockStudyCard.getId());
        assertThat(result.getLanguages()).isEmpty();
        assertThat(result.getWorkbooks()).isEmpty();
        assertThat(result.getLeader()).isEqualTo(mockLeader);
    }

    @Test
    void 스터디_카드_정보를_조회시_스터디가_없으면_예외가_발생한다() {
        // given
        Long studyId = 1L;
        Long userId = 1L;

        when(studyQuery.findStudyPagesByStudyId(studyId, 1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyQueryService.getStudyCard(studyId, userId))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", StudyExceptionCode.NOT_FOUND_STUDY);
    }

    @Test
    void 스터디_상세_페이지_정보를_조회한다() {
        // given
        Study mockStudy = mock(Study.class);
        List<LanguageDto> mockDto = List.of(mock(LanguageDto.class));
        StudyDetailPageDto expected = new StudyDetailPageDto(mockStudy.getId(), mockStudy.getName(), mockDto);

        when(studyDomainService.getStudyWithThrow(1L)).thenReturn(mockStudy);
        when(studyLanguageQuery.findAllLanguagesByStudyId(1L)).thenReturn(mockDto);

        // when
        StudyDetailPageDto result = studyQueryService.getStudyDetailPage(1L, 1L);

        // then

        verify(studyDomainService).validateStudyMembership(1L, mockStudy.getId());
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void 스터디_목록_조회를_한다() {
        // given
        StudyMemberDto mockDto = new StudyMemberDto();
        mockDto.setUserId(1L);
        when(studyUserQuery.findMembers(anyLong(), any(StudyUserFilterDto.class))).thenReturn(List.of(mockDto));
        when(codingSpaceQueryService.countJoinedSpacesByMembers(1L, List.of(1L))).thenReturn(Map.of(1L, 1L));

        // when
        List<StudyMemberDto> result = studyQueryService.findAllMembers(1L, 1L, mock(StudyUserFilterDto.class));

        // then
        assertThat(result.getFirst().getJoinedSpaceCount()).isEqualTo(1L);
    }

    @Test
    void 참여한_스터디를_조회한다() {
        // given
        when(studyQuery.findTop20UserStudyCards(1L, 1L, null)).thenReturn(List.of());
        when(studyLanguageQuery.findLanguageByStudies(anyList())).thenReturn(new HashMap<>());
        when(studyWorkbookQuery.findWorkbookByStudies(anyList())).thenReturn(new HashMap<>());
        when(studyUserQuery.findLeaderByStudies(anyList())).thenReturn(new HashMap<>());

        // when
        List<StudyCardDto> result = studyQueryService.getStudyCardsByUser(1L, 1L, null);

        // then
        assertThat(result).isEqualTo(List.of());
        verify(studyQuery).findTop20UserStudyCards(1L, 1L, null);
        verify(studyLanguageQuery).findLanguageByStudies(anyList());
        verify(studyWorkbookQuery).findWorkbookByStudies(anyList());
        verify(studyUserQuery).findLeaderByStudies(anyList());
    }

}