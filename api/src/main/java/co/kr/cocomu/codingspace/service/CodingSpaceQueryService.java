package co.kr.cocomu.codingspace.service;

import co.kr.cocomu.codingspace.dto.page.FeedbackPage;
import co.kr.cocomu.codingspace.dto.page.FinishPage;
import co.kr.cocomu.codingspace.dto.page.StartingPage;
import co.kr.cocomu.codingspace.dto.page.WaitingPage;
import co.kr.cocomu.codingspace.dto.request.FilterDto;
import co.kr.cocomu.codingspace.dto.response.CodingSpaceDto;
import co.kr.cocomu.codingspace.dto.response.CodingSpacesDto;
import co.kr.cocomu.codingspace.dto.response.LanguageDto;
import co.kr.cocomu.codingspace.dto.response.UserDto;
import co.kr.cocomu.codingspace.exception.CodingSpaceExceptionCode;
import co.kr.cocomu.codingspace.repository.CodingSpaceRepository;
import co.kr.cocomu.codingspace.repository.CodingSpaceTabRepository;
import co.kr.cocomu.codingspace.repository.query.TestCaseQuery;
import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.service.business.StudyDomainService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CodingSpaceQueryService {

    private final StudyDomainService studyDomainService;
    private final CodingSpaceRepository codingSpaceQuery;
    private final CodingSpaceTabRepository codingSpaceTabQuery;
    private final TestCaseQuery testCaseQuery;

    public CodingSpacesDto getCodingSpaces(final Long studyId, final Long userId, final FilterDto dto) {
        studyDomainService.validateStudyMembership(userId, studyId);
        final List<CodingSpaceDto> codingSpaces = codingSpaceQuery.findSpacesWithFilter(userId, studyId, dto);
        final List<Long> codingSpaceIds = codingSpaces.stream().map(CodingSpaceDto::getId).toList();
        final Map<Long, List<UserDto>> usersBySpace = codingSpaceTabQuery.findUsersBySpace(codingSpaceIds);

        return CodingSpacesDto.of(codingSpaces, usersBySpace);
    }

    public WaitingPage extractWaitingPage(final Long codingSpaceId, final Long userId) {
        return codingSpaceQuery.findWaitingPage(codingSpaceId, userId)
            .map(waitingPage -> {
                waitingPage.setTestCases(testCaseQuery.findTestCases(codingSpaceId));
                waitingPage.setActiveUsers(codingSpaceTabQuery.findUsers(codingSpaceId, userId));
                return waitingPage;
            })
            .orElseThrow(() -> new BadRequestException(CodingSpaceExceptionCode.NOT_ENTER_SPACE));
    }

    public StartingPage extractStaringPage(final Long codingSpaceId, final Long userId) {
        return codingSpaceQuery.findStartingPage(codingSpaceId, userId)
            .map(startingPage -> {
                startingPage.setTestCases(testCaseQuery.findTestCases(codingSpaceId));
                startingPage.setActiveUsers(codingSpaceTabQuery.findUsers(codingSpaceId, userId));
                return startingPage;
            })
            .orElseThrow(() -> new BadRequestException(CodingSpaceExceptionCode.NOT_ENTER_SPACE));
    }

    public FeedbackPage extractFeedbackPage(final Long codingSpaceId, final Long userId) {
        return codingSpaceQuery.findFeedbackPage(codingSpaceId, userId)
            .map(feedbackPage -> {
                feedbackPage.setTestCases(testCaseQuery.findTestCases(codingSpaceId));
                feedbackPage.setActiveTabs(codingSpaceTabQuery.findAllTabs(codingSpaceId, userId));
                return feedbackPage;
            })
            .orElseThrow(() -> new BadRequestException(CodingSpaceExceptionCode.NOT_ENTER_SPACE));
    }

    public FinishPage extractFinishPage(final Long codingSpaceId, final Long userId) {
        return codingSpaceQuery.findFinishPage(codingSpaceId, userId)
            .map(finishPage -> {
                finishPage.setTestCases(testCaseQuery.findTestCases(codingSpaceId));
                finishPage.setAllFinishedTabs(codingSpaceTabQuery.findAllFinishedTabs(codingSpaceId));
                return finishPage;
            })
            .orElseThrow(() -> new BadRequestException(CodingSpaceExceptionCode.NO_STUDY_MEMBERSHIP));
    }

    public List<CodingSpaceDto> getSpacesByUser(final Long userId, final Long viewerId, final Long lastIndex) {
        final List<CodingSpaceDto> codingSpaces = codingSpaceQuery.findUserSpaces(userId, viewerId, lastIndex);
        final List<Long> codingSpaceIds = codingSpaces.stream().map(CodingSpaceDto::getId).toList();
        final Map<Long, List<UserDto>> usersBySpace = codingSpaceTabQuery.findUsersBySpace(codingSpaceIds);

        for (CodingSpaceDto codingSpace : codingSpaces) {
            final List<UserDto> users = usersBySpace.getOrDefault(codingSpace.getId(), List.of());
            codingSpace.setCurrentUsers(users);
        }

        return codingSpaces;
    }

    public Map<Long, Long> countJoinedSpacesByMembers(final Long studyId, final List<Long> userIds) {
        return codingSpaceTabQuery.countSpacesByStudyAndUsers(studyId, userIds);
    }

}
