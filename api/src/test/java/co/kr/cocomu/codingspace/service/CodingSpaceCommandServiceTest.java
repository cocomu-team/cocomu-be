package co.kr.cocomu.codingspace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CodingSpaceCommandServiceTest {

    @Mock private StudyDomainService studyDomainService;
    @Mock private CodingSpaceRepository codingSpaceRepository;
    @Mock private TestCaseRepository testCaseRepository;
    @Mock private CodingSpaceDomainService codingSpaceDomainService;
    @Mock private StompSSEProducer stompSSEProducer;
    @Mock private UserService userService;

    @InjectMocks private CodingSpaceCommandService codingSpaceCommandService;

    Study mockStudy;
    User mockUser;
    CodingSpace mockCodingSpace;

    @BeforeEach
    void setUp() {
        mockStudy = mock(Study.class);
        mockUser = mock(User.class);
        mockCodingSpace = mock(CodingSpace.class);
    }

    @Test
    void 코딩_스페이스를_생성한다() {
        // given
        CreateCodingSpaceDto mockDto = mock(CreateCodingSpaceDto.class);
        List<CreateTestCaseDto> mockTestCases = List.of(mock(CreateTestCaseDto.class));
        코딩_스페이스_생성_스텁(mockDto, mockTestCases);

        // when
        Long result = codingSpaceCommandService.createCodingSpace(mockDto, 1L);

        // then
        assertThat(result).isEqualTo(mockCodingSpace.getId());
    }

    @Test
    void 코딩_스페이스에_참여한다() {
        // given
        코딩_스페이스_참여_스텁();

        // when
        Long result = codingSpaceCommandService.joinCodingSpace(1L, 1L);

        // then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void 대기방_입장을_한다() {
        // given
        CodingSpaceTab mockTab = mock(CodingSpaceTab.class);
        when(mockTab.getUser()).thenReturn(mockUser);
        when(mockTab.getCodingSpace()).thenReturn(mockCodingSpace);
        when(mockCodingSpace.getStatus()).thenReturn(CodingSpaceStatus.WAITING);
        when(codingSpaceDomainService.getCodingSpaceTabWithThrow(1L, 1L)).thenReturn(mockTab);
        doNothing().when(mockTab).enterTab();
        doNothing().when(stompSSEProducer).publishUserEnter(mockUser, 1L);

        // when
        CodingSpaceStatus status = codingSpaceCommandService.enterSpace(1L, 1L);

        // then
        assertThat(status).isEqualTo(CodingSpaceStatus.WAITING);
    }

    @Test
    void 코딩스페이스_퇴장을_한다() {
        // given
        CodingSpaceTab mockTab = mock(CodingSpaceTab.class);
        when(mockTab.getUser()).thenReturn(mockUser);
        when(codingSpaceDomainService.getCodingSpaceTabWithThrow(1L, 1L)).thenReturn(mockTab);
        doNothing().when(mockTab).leaveTab();
        doNothing().when(stompSSEProducer).publishUserLeave(mockUser, 1L);

        // when
        codingSpaceCommandService.leaveSpace(1L, 1L);

        // then
        verify(stompSSEProducer).publishUserLeave(mockUser, 1L);
    }

    @Test
    void 코딩_스페이스_시작을_한다() {
        // given
        CodingSpaceTab mockTab = mock(CodingSpaceTab.class);
        when(codingSpaceDomainService.getCodingSpaceTabWithThrow(1L, 1L)).thenReturn(mockTab);
        doNothing().when(mockTab).start();
        doNothing().when(stompSSEProducer).publishStartSpace(1L);

        // when
        codingSpaceCommandService.startSpace(1L, 1L);

        // then
        verify(stompSSEProducer).publishStartSpace(1L);
    }

    @Test
    void 코딩_스페이스_피드맥_모드를_시작_한다() {
        // given
        CodingSpaceTab mockTab = mock(CodingSpaceTab.class);
        when(codingSpaceDomainService.getCodingSpaceTabWithThrow(1L, 1L)).thenReturn(mockTab);
        doNothing().when(mockTab).startFeedback();
        doNothing().when(stompSSEProducer).publishFeedback(1L);

        // when
        codingSpaceCommandService.startFeedback(1L, 1L);

        // then
        verify(stompSSEProducer).publishFeedback(1L);
    }

    @Test
    void 코딩_스페이스_종료를_한다() {
        // given
        CodingSpaceTab mockTab = mock(CodingSpaceTab.class);
        when(codingSpaceDomainService.getCodingSpaceTabWithThrow(1L, 1L)).thenReturn(mockTab);
        doNothing().when(mockTab).finishSpace();
        doNothing().when(stompSSEProducer).publishFinish(1L);

        // when
        codingSpaceCommandService.finishSpace(1L, 1L);

        // then
        verify(stompSSEProducer).publishFinish(1L);
    }

    @Test
    void 최종_코드_저장을_한다() {
        // given
        CodingSpaceTab mockTab = mock(CodingSpaceTab.class);
        when(codingSpaceDomainService.getCodingSpaceTabWithThrow(1L, 1L)).thenReturn(mockTab);
        doNothing().when(mockTab).saveCode("code");

        // when
        codingSpaceCommandService.saveFinalCode(1L, 1L, "code");

        // then
        verify(mockTab).saveCode("code");
    }

    @Test
    void 테스트_케이스를_추가한다() {
        // given
        CodingSpaceTab mockSpaceTab = mock(CodingSpaceTab.class);
        TestCase mockTestCase = mock(TestCase.class);
        when(codingSpaceDomainService.getCodingSpaceTabWithThrow(1L, 1L)).thenReturn(mockSpaceTab);
        when(testCaseRepository.save(any(TestCase.class))).thenReturn(mockTestCase);

        // when
        codingSpaceCommandService.addTestCase(1L, 1L, mock(CreateTestCaseDto.class));

        // then
        verify(stompSSEProducer).publishAddTestCase(any(TestCaseDto.class), anyLong());
    }

    @Test
    void 테스트케이스_제거를_한다() {
        // given
        CodingSpaceTab mockTab = mock(CodingSpaceTab.class);
        CodingSpace mockCodingSpace = mock(CodingSpace.class);
        when(codingSpaceDomainService.getCodingSpaceTabWithThrow(1L, 1L)).thenReturn(mockTab);
        when(mockTab.getCodingSpace()).thenReturn(mockCodingSpace);

        // when
        codingSpaceCommandService.deleteTestCase(1L, 1L, 1L);

        // then
        verify(mockCodingSpace).deleteTestCase(1L);
        verify(stompSSEProducer).publishDeleteTestCase(1L, 1L);
    }

    @Test
    void 코딩_스페이스_제거를_한다() {
        // given
        CodingSpaceTab mockTab = mock(CodingSpaceTab.class);
        when(codingSpaceDomainService.getCodingSpaceTabWithThrow(1L, 1L)).thenReturn(mockTab);

        // when
        codingSpaceCommandService.deleteSpace(1L, 1L);

        // then
        verify(mockTab).validateHostRole();
        verify(codingSpaceRepository).deleteById(1L);
    }

    /*
    * ========================== SET STUB ==========================
    * */

    private void 코딩_스페이스_생성_스텁(CreateCodingSpaceDto mockDto, List<CreateTestCaseDto> mockTestCases) {
        when(mockDto.totalUserCount()).thenReturn(2);
        when(mockDto.testcases()).thenReturn(mockTestCases);
        when(studyDomainService.getStudyWithThrow(mockDto.studyId())).thenReturn(mockStudy);
        when(userService.getUserWithThrow(1L)).thenReturn(mockUser);
        when(studyDomainService.getLanguageTagInStudy(mockDto.studyId(), mockDto.languageId()))
            .thenReturn(mock(LanguageTag.class));
        when(codingSpaceRepository.save(any(CodingSpace.class))).thenReturn(mockCodingSpace);
    }

    private void 코딩_스페이스_참여_스텁() {
        when(mockStudy.getId()).thenReturn(1L);
        when(mockUser.getId()).thenReturn(1L);
        when(mockCodingSpace.getStudy()).thenReturn(mockStudy);
        doNothing().when(mockCodingSpace).joinUser(mockUser);
        when(mockCodingSpace.getId()).thenReturn(1L);

        when(codingSpaceDomainService.getCodingSpaceWithThrow(1L)).thenReturn(mockCodingSpace);
        when(userService.getUserWithThrow(1L)).thenReturn(mockUser);
        doNothing().when(studyDomainService).validateStudyMembership(1L, 1L);
    }

}