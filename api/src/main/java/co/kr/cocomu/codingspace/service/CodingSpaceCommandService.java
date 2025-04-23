package co.kr.cocomu.codingspace.service;

import co.kr.cocomu.codingspace.domain.CodingSpace;
import co.kr.cocomu.codingspace.domain.CodingSpaceTab;
import co.kr.cocomu.codingspace.domain.TestCase;
import co.kr.cocomu.codingspace.domain.vo.CodingSpaceStatus;
import co.kr.cocomu.codingspace.dto.request.CreateCodingSpaceDto;
import co.kr.cocomu.codingspace.dto.request.CreateTestCaseDto;
import co.kr.cocomu.codingspace.dto.response.TestCaseDto;
import co.kr.cocomu.codingspace.repository.CodingSpaceRepository;
import co.kr.cocomu.codingspace.repository.TestCaseRepository;
import co.kr.cocomu.codingspace.stomp.StompSSEProducer;
import co.kr.cocomu.tag.domain.LanguageTag;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.service.business.StudyDomainService;
import co.kr.cocomu.user.domain.User;
import co.kr.cocomu.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CodingSpaceCommandService {

    private final CodingSpaceDomainService codingSpaceDomainService;
    private final StudyDomainService studyDomainService;
    private final UserService userService;
    private final CodingSpaceRepository codingSpaceRepository;
    private final TestCaseRepository testCaseRepository;
    private final StompSSEProducer stompSSEProducer;

    public Long createCodingSpace(final CreateCodingSpaceDto dto, final Long userId) {
        studyDomainService.validateStudyMembership(userId, dto.studyId());
        final Study study = studyDomainService.getStudyWithThrow(dto.studyId());
        final User user = userService.getUserWithThrow(userId);
        final LanguageTag languageTag = studyDomainService.getLanguageTagInStudy(dto.studyId(), dto.languageId());

        final CodingSpace codingSpace = CodingSpace.createCodingSpace(dto, study, user, languageTag);
        final CodingSpace savedCodingSpace = codingSpaceRepository.save(codingSpace);

        return savedCodingSpace.getId();
    }

    public Long joinCodingSpace(final Long codingSpaceId, final Long userId) {
        final CodingSpace codingSpace = codingSpaceDomainService.getCodingSpaceWithThrow(codingSpaceId);
        final User user = userService.getUserWithThrow(userId);
        studyDomainService.validateStudyMembership(user.getId(), codingSpace.getStudy().getId());
        codingSpace.joinUser(user);

        return codingSpace.getId();
    }

    public CodingSpaceStatus enterSpace(final Long codingSpaceId, final Long userId) {
        final CodingSpaceTab tab = codingSpaceDomainService.getCodingSpaceTabWithThrow(codingSpaceId, userId);
        tab.enterTab();

        final User user = tab.getUser();
        stompSSEProducer.publishUserEnter(user, codingSpaceId);

        return tab.getCodingSpace().getStatus();
    }

    public void leaveSpace(final Long userId, final Long codingSpaceId) {
        final CodingSpaceTab tab = codingSpaceDomainService.getCodingSpaceTabWithThrow(codingSpaceId, userId);
        tab.leaveTab();

        final User user = tab.getUser();
        stompSSEProducer.publishUserLeave(user, codingSpaceId);
    }

    public void startSpace(final Long codingSpaceId, final Long userId) {
        final CodingSpaceTab tab = codingSpaceDomainService.getCodingSpaceTabWithThrow(codingSpaceId, userId);
        tab.start();

        stompSSEProducer.publishStartSpace(codingSpaceId);
    }

    public void startFeedback(final Long codingSpaceId, final Long userId) {
        final CodingSpaceTab tab = codingSpaceDomainService.getCodingSpaceTabWithThrow(codingSpaceId, userId);
        tab.startFeedback();

        stompSSEProducer.publishFeedback(codingSpaceId);
    }

    public void finishSpace(final Long codingSpaceId, final Long userId) {
        final CodingSpaceTab tab = codingSpaceDomainService.getCodingSpaceTabWithThrow(codingSpaceId, userId);
        tab.finishSpace();

        stompSSEProducer.publishFinish(codingSpaceId);
    }

    public void saveFinalCode(final Long codingSpaceId, final Long userId, final String code) {
        final CodingSpaceTab tab = codingSpaceDomainService.getCodingSpaceTabWithThrow(codingSpaceId, userId);
        tab.saveCode(code);
    }

    public TestCaseDto addTestCase(final Long codingSpaceId, final Long userId, final CreateTestCaseDto dto) {
        final CodingSpaceTab tab = codingSpaceDomainService.getCodingSpaceTabWithThrow(codingSpaceId, userId);
        codingSpaceDomainService.validateActiveTab(tab.getId());

        final TestCase customTestCase = TestCase.createCustomCase(dto);
        customTestCase.setCodingSpace(tab.getCodingSpace());

        final TestCase savedCase = testCaseRepository.save(customTestCase);
        final TestCaseDto testCaseDto = TestCaseDto.from(savedCase);
        stompSSEProducer.publishAddTestCase(testCaseDto, codingSpaceId);

        return testCaseDto;
    }

    public Long deleteTestCase(final Long codingSpaceId, final Long userId, final Long testCaseId) {
        final CodingSpaceTab tab = codingSpaceDomainService.getCodingSpaceTabWithThrow(codingSpaceId, userId);
        codingSpaceDomainService.validateActiveTab(tab.getId());

        final CodingSpace codingSpace = tab.getCodingSpace();
        codingSpace.deleteTestCase(testCaseId);

        stompSSEProducer.publishDeleteTestCase(codingSpaceId, testCaseId);

        return testCaseId;
    }

    public void deleteSpace(final Long codingSpaceId, final Long userId) {
        final CodingSpaceTab tab = codingSpaceDomainService.getCodingSpaceTabWithThrow(codingSpaceId, userId);
        tab.validateHostRole();

        codingSpaceRepository.deleteById(codingSpaceId);
    }

}
