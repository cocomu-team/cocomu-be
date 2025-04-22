package co.kr.cocomu.codingspace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.codingspace.dto.page.FeedbackPage;
import co.kr.cocomu.codingspace.dto.page.FinishPage;
import co.kr.cocomu.codingspace.dto.page.StartingPage;
import co.kr.cocomu.codingspace.dto.page.WaitingPage;
import co.kr.cocomu.codingspace.dto.request.FilterDto;
import co.kr.cocomu.codingspace.dto.response.CodingSpaceDto;
import co.kr.cocomu.codingspace.dto.response.CodingSpacesDto;
import co.kr.cocomu.codingspace.dto.response.LanguageDto;
import co.kr.cocomu.codingspace.exception.CodingSpaceExceptionCode;
import co.kr.cocomu.codingspace.repository.CodingSpaceRepository;
import co.kr.cocomu.codingspace.repository.CodingSpaceTabRepository;
import co.kr.cocomu.codingspace.repository.query.TestCaseQuery;
import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.service.business.StudyDomainService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CodingSpaceQueryServiceTest {

    @Mock private StudyDomainService studyDomainService;
    @Mock private TestCaseQuery testCaseQuery;
    @Mock private CodingSpaceRepository codingSpaceQuery;
    @Mock private CodingSpaceTabRepository codingSpaceTabQuery;

    @InjectMocks private CodingSpaceQueryService codingSpaceQueryService;

    @Test
    void 빈_코딩스페이스_목록을_가져온다() {
        List<CodingSpaceDto> mockSpaces = List.of();
        doNothing().when(studyDomainService).validateStudyMembership(1L, 1L);
        when(codingSpaceQuery.findSpacesWithFilter(anyLong(), anyLong(), any(FilterDto.class))).thenReturn(mockSpaces);
        when(codingSpaceTabQuery.findUsersBySpace(List.of())).thenReturn(Map.of());

        CodingSpacesDto codingSpaces = codingSpaceQueryService.getCodingSpaces(1L, 1L, mock(FilterDto.class));

        assertThat(codingSpaces.codingSpaces()).hasSize(0);
        assertThat(codingSpaces.lastId()).isEqualTo(0L);
    }

    @Test
    void 코딩스페이스_목록을_가져온다() {
        final CodingSpaceDto codingSpaceDto = new CodingSpaceDto();
        codingSpaceDto.setId(1L);
        List<CodingSpaceDto> mockSpaces = List.of(codingSpaceDto);
        doNothing().when(studyDomainService).validateStudyMembership(1L, 1L);
        when(codingSpaceQuery.findSpacesWithFilter(anyLong(), anyLong(), any(FilterDto.class))).thenReturn(mockSpaces);
        when(codingSpaceTabQuery.findUsersBySpace(List.of(1L))).thenReturn(Map.of(1L, List.of()));

        CodingSpacesDto codingSpaces = codingSpaceQueryService.getCodingSpaces(1L, 1L, mock(FilterDto.class));

        assertThat(codingSpaces.codingSpaces()).hasSize(1);
        assertThat(codingSpaces.lastId()).isEqualTo(1L);
    }

    @Test
    void 대기방_페이지를_가져온다() {
        // given
        WaitingPage mockPage = mock(WaitingPage.class);
        when(codingSpaceQuery.findWaitingPage(1L, 1L)).thenReturn(Optional.of(mockPage));
        when(codingSpaceTabQuery.findUsers(1L, 1L)).thenReturn(List.of());
        when(testCaseQuery.findTestCases(1L)).thenReturn(List.of());

        // when
        WaitingPage result = codingSpaceQueryService.extractWaitingPage(1L, 1L);

        // then
        assertThat(result).isEqualTo(mockPage);
    }

    @Test
    void 대기방_페이지를_가져오지_못한다() {
        // given
        when(codingSpaceQuery.findWaitingPage(1L, 1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> codingSpaceQueryService.extractWaitingPage(1L, 1L))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.NOT_ENTER_SPACE);
    }

    @Test
    void 시작_페이지를_가져온다() {
        // given
        StartingPage mockPage = mock(StartingPage.class);
        when(codingSpaceQuery.findStartingPage(1L, 1L)).thenReturn(Optional.of(mockPage));
        when(codingSpaceTabQuery.findUsers(1L, 1L)).thenReturn(List.of());
        when(testCaseQuery.findTestCases(1L)).thenReturn(List.of());

        // when
        StartingPage result = codingSpaceQueryService.extractStaringPage(1L, 1L);

        // then
        assertThat(result).isEqualTo(mockPage);
    }

    @Test
    void 시작_페이지를_가져오지_못한다() {
        // given
        when(codingSpaceQuery.findStartingPage(1L, 1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> codingSpaceQueryService.extractStaringPage(1L, 1L))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.NOT_ENTER_SPACE);
    }

    @Test
    void 피드백_페이지를_가져온다() {
        // given
        FeedbackPage mockPage = mock(FeedbackPage.class);
        when(codingSpaceQuery.findFeedbackPage(1L, 1L)).thenReturn(Optional.of(mockPage));
        when(codingSpaceTabQuery.findAllTabs(1L, 1L)).thenReturn(List.of());
        when(testCaseQuery.findTestCases(1L)).thenReturn(List.of());

        // when
        FeedbackPage result = codingSpaceQueryService.extractFeedbackPage(1L, 1L);

        // then
        assertThat(result).isEqualTo(mockPage);
    }

    @Test
    void 피드백_페이지를_가져오지_못한다() {
        // given
        when(codingSpaceQuery.findFeedbackPage(1L, 1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> codingSpaceQueryService.extractFeedbackPage(1L, 1L))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.NOT_ENTER_SPACE);
    }

    @Test
    void 종료_페이지를_가져온다() {
        // given
        FinishPage mockPage = mock(FinishPage.class);
        when(codingSpaceQuery.findFinishPage(1L, 1L)).thenReturn(Optional.of(mockPage));
        when(codingSpaceTabQuery.findAllFinishedTabs(1L)).thenReturn(List.of());
        when(testCaseQuery.findTestCases(1L)).thenReturn(List.of());

        // when
        FinishPage result = codingSpaceQueryService.extractFinishPage(1L, 1L);

        // then
        assertThat(result).isEqualTo(mockPage);
    }

    @Test
    void 종료_페이지를_가져오지_못한다() {
        // given
        when(codingSpaceQuery.findFinishPage(1L, 1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> codingSpaceQueryService.extractFinishPage(1L, 1L))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.NO_STUDY_MEMBERSHIP);
    }

    @Test
    void 참여한_코딩스페이스_목록을_가져온다() {
        // given
        CodingSpaceDto mockDto = new CodingSpaceDto();
        mockDto.setId(1L);
        List<CodingSpaceDto> mockSpaces = List.of(mockDto);
        when(codingSpaceQuery.findUserSpaces(anyLong(), anyLong(), anyLong())).thenReturn(mockSpaces);
        when(codingSpaceTabQuery.findUsersBySpace(List.of(1L))).thenReturn(Map.of(1L, List.of()));

        // when
        List<CodingSpaceDto> codingSpaces = codingSpaceQueryService.getSpacesByUser(1L, 1L, 1L);

        // then
        assertThat(codingSpaces.getFirst()).isEqualTo(mockDto);
        assertThat(codingSpaces.getFirst().getCurrentUsers()).isEqualTo(List.of());
    }

    @Test
    void 사용자별_참여_스페이스_수를_가져_올_때_query로부터_가져온다() {
        // given
        when(codingSpaceTabQuery.countSpacesByStudyAndUsers(1L, List.of())).thenReturn(Map.of());

        // when
        codingSpaceQueryService.countJoinedSpacesByMembers(1L, List.of());

        // then
        verify(codingSpaceTabQuery).countSpacesByStudyAndUsers(1L, List.of());
    }

}