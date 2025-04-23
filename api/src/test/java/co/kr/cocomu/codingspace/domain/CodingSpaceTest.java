package co.kr.cocomu.codingspace.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.kr.cocomu.codingspace.domain.vo.CodingSpaceStatus;
import co.kr.cocomu.codingspace.domain.vo.TestCaseType;
import co.kr.cocomu.codingspace.dto.request.CreateCodingSpaceDto;
import co.kr.cocomu.codingspace.dto.request.CreateTestCaseDto;
import co.kr.cocomu.codingspace.exception.CodingSpaceExceptionCode;
import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.tag.domain.LanguageTag;
import co.kr.cocomu.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CodingSpaceTest {

    Study mockStudy;
    User mockUser;
    User otherUser;
    LanguageTag mockLanguageTag;

    @BeforeEach
    void setUp() {
        mockStudy = mock(Study.class);
        mockUser = mock(User.class);
        otherUser = mock(User.class);
        mockLanguageTag = mock(LanguageTag.class);
    }


    @Test
    void 코딩_스페이스가_정상적으로_생성된다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        LanguageTag mockLanguageTag = mock(LanguageTag.class);
        when(dto.totalUserCount()).thenReturn(2);
        when(dto.testcases()).thenReturn(List.of(testCase));

        // when
        CodingSpace codingSpace = CodingSpace.createCodingSpace(dto, mockStudy, mockUser, mockLanguageTag);

        // then
        assertThat(codingSpace.getStatus()).isEqualTo(CodingSpaceStatus.WAITING);
        assertThat(codingSpace.getStudy()).isEqualTo(mockStudy);
        assertThat(codingSpace.getTestCases()).hasSize(1);
    }

    @Test
    void 코딩_스페이스가_생성시_방장이_추가된다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        when(dto.totalUserCount()).thenReturn(2);
        when(dto.testcases()).thenReturn(List.of(testCase));

        // when
        CodingSpace codingSpace = CodingSpace.createCodingSpace(dto, mockStudy, mockUser, mockLanguageTag);

        // then
        assertThat(codingSpace.getCurrentUserCount()).isEqualTo(1);
        assertThat(codingSpace.getTabs()).hasSize(1);
    }

    @Test
    void 코딩_스페이스_최대_인원_초과시_예외가_발생한다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        when(dto.totalUserCount()).thenReturn(5);
        when(dto.testcases()).thenReturn(List.of(testCase));

        // when & then
        assertThatThrownBy(() -> CodingSpace.createCodingSpace(dto, null, null, mockLanguageTag))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.MAX_USER_COUNT_IS_FOUR);
    }

    @Test
    void 코딩_스페이스_최소_인원_미만일_시_예외가_발생한다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        when(dto.totalUserCount()).thenReturn(1);
        when(dto.testcases()).thenReturn(List.of(testCase));

        // when & then
        assertThatThrownBy(() -> CodingSpace.createCodingSpace(dto, null, null, mockLanguageTag))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.MIN_USER_COUNT_IS_TWO);
    }

    @Test
    void 코딩_스페이스_테스트_케이스가_없으면_예외가_발생한다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        when(dto.totalUserCount()).thenReturn(2);
        when(dto.testcases()).thenReturn(List.of());

        // when & then
        assertThatThrownBy(() -> CodingSpace.createCodingSpace(dto, null, null, mockLanguageTag))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.EMPTY_TEST_CASE);
    }

    @Test
    void 코딩_테스트에_참여가_된다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        when(dto.totalUserCount()).thenReturn(2);
        when(dto.testcases()).thenReturn(List.of(testCase));

        CodingSpace codingSpace = CodingSpace.createCodingSpace(dto, mockStudy, mockUser, mockLanguageTag);
        int currentCount = codingSpace.getCurrentUserCount();

        // when
        codingSpace.joinUser(otherUser);

        // then
        assertThat(codingSpace.getCurrentUserCount()).isEqualTo(currentCount + 1);
        assertThat(codingSpace.getTabs()).hasSize(2);
    }

    @Test
    void 코딩_테스트에_이미_참여한_경우_예외가_발생한다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        when(dto.totalUserCount()).thenReturn(2);
        when(dto.testcases()).thenReturn(List.of(testCase));

        CodingSpace codingSpace = CodingSpace.createCodingSpace(dto, mockStudy, mockUser, mockLanguageTag);
        codingSpace.joinUser(otherUser);

        // when & then
        assertThatThrownBy(() -> codingSpace.joinUser(otherUser))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.ALREADY_PARTICIPATION_SPACE);
    }

    @Test
    void 코딩_테스트가_대기중이_아닐_경우_참여하면_예외가_발생한다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        when(dto.totalUserCount()).thenReturn(2);
        when(dto.testcases()).thenReturn(List.of(testCase));

        CodingSpace codingSpace = CodingSpace.createCodingSpace(dto, mockStudy, mockUser, mockLanguageTag);

        codingSpace.joinUser(otherUser);
        codingSpace.getTabs().forEach(CodingSpaceTab::enterTab);
        codingSpace.start();

        // when & then
        assertThatThrownBy(() -> codingSpace.joinUser(otherUser))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.NOT_WAITING_STUDY);
    }

    @Test
    void 코딩_테스트가_참여_시_최대_인원을_초과하는_경우_예외가_발생한다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        when(dto.totalUserCount()).thenReturn(2);
        when(dto.testcases()).thenReturn(List.of(testCase));

        CodingSpace codingSpace = CodingSpace.createCodingSpace(dto, mockStudy, mockUser, mockLanguageTag);
        codingSpace.joinUser(otherUser);
        User otherUser2 = mock(User.class);

        // when & then
        assertThatThrownBy(() -> codingSpace.joinUser(otherUser2))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.OVER_USER_COUNT);
    }

    @Test
    void 코딩_스페이스_시작이_된다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        when(dto.totalUserCount()).thenReturn(2);
        when(dto.testcases()).thenReturn(List.of(testCase));

        CodingSpace codingSpace = CodingSpace.createCodingSpace(dto, mockStudy, mockUser, mockLanguageTag);
        codingSpace.joinUser(mock(User.class));
        codingSpace.getTabs().forEach(CodingSpaceTab::enterTab);

        // when
        codingSpace.start();

        // then
        assertThat(codingSpace.getStatus()).isEqualTo(CodingSpaceStatus.RUNNING);
        assertThat(codingSpace.getStartTime()).isBefore(LocalDateTime.now());
    }

    @Test
    void 코딩_스페이스를_재시작하면_예외가_발생한다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        when(dto.totalUserCount()).thenReturn(2);
        when(dto.testcases()).thenReturn(List.of(testCase));

        CodingSpace codingSpace = CodingSpace.createCodingSpace(dto, mockStudy, mockUser, mockLanguageTag);
        codingSpace.joinUser(mock(User.class));
        codingSpace.getTabs().forEach(CodingSpaceTab::enterTab);
        codingSpace.start();

        // when & then
        assertThatThrownBy(codingSpace::start)
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.ALREADY_STARTING_SPACE);
    }

    @Test
    void 입장한_인원이_2명_미만이라면_코딩스페이스_시작이_안된다() {
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        when(dto.totalUserCount()).thenReturn(2);
        when(dto.testcases()).thenReturn(List.of(testCase));

        CodingSpace codingSpace = CodingSpace.createCodingSpace(dto, mockStudy, mockUser, mockLanguageTag);

        // when & then
        assertThatThrownBy(codingSpace::start)
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.START_MINIMUM_USER_COUNT);
    }

    @Test
    void 코딩_스페이스_피드백_시작이_된다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        when(dto.totalUserCount()).thenReturn(2);
        when(dto.testcases()).thenReturn(List.of(testCase));

        CodingSpace codingSpace = CodingSpace.createCodingSpace(dto, mockStudy, mockUser, mockLanguageTag);
        codingSpace.joinUser(mock(User.class));
        codingSpace.getTabs().forEach(CodingSpaceTab::enterTab);
        codingSpace.start();

        // when
        codingSpace.startFeedBack();

        // then
        assertThat(codingSpace.getStatus()).isEqualTo(CodingSpaceStatus.FEEDBACK);
        assertThat(codingSpace.getFinishTime()).isBefore(LocalDateTime.now());
    }

    @Test
    void 코딩_스페이스가_시작_상태가_아니라면_피드백을_진행할_수_없다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        when(dto.totalUserCount()).thenReturn(2);
        when(dto.testcases()).thenReturn(List.of(testCase));

        CodingSpace codingSpace = CodingSpace.createCodingSpace(dto, mockStudy, mockUser, mockLanguageTag);

        // when & then
        assertThatThrownBy(codingSpace::startFeedBack)
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.CAN_NOT_FEEDBACK);
    }

    @Test
    void 코딩_스페이스_종료가_된다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        when(dto.totalUserCount()).thenReturn(2);
        when(dto.testcases()).thenReturn(List.of(testCase));

        CodingSpace codingSpace = CodingSpace.createCodingSpace(dto, mockStudy, mockUser, mockLanguageTag);
        codingSpace.joinUser(mock(User.class));
        codingSpace.getTabs().forEach(CodingSpaceTab::enterTab);
        codingSpace.start();
        codingSpace.startFeedBack();

        // when
        codingSpace.finishSpace();

        // then
        assertThat(codingSpace.getStatus()).isEqualTo(CodingSpaceStatus.FINISHED);
    }

    @Test
    void 코딩_스페이스가_피드백_상태가_아니라면_종료_할_수_없다() {
        // given
        CreateCodingSpaceDto dto = mock(CreateCodingSpaceDto.class);
        CreateTestCaseDto testCase = mock(CreateTestCaseDto.class);
        when(dto.totalUserCount()).thenReturn(2);
        when(dto.testcases()).thenReturn(List.of(testCase));

        CodingSpace codingSpace = CodingSpace.createCodingSpace(dto, mockStudy, mockUser, mockLanguageTag);
        codingSpace.joinUser(mock(User.class));
        codingSpace.getTabs().forEach(CodingSpaceTab::enterTab);
        codingSpace.start();

        // when & then
        assertThatThrownBy(codingSpace::finishSpace)
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.CAN_NOT_FINISH);
    }

    @Test
    void 테스트케이스_제거가_된다() {
        // given
        CodingSpace codingSpace = new CodingSpace();
        TestCase mockCase = mock(TestCase.class);
        when(mockCase.getId()).thenReturn(1L);
        when(mockCase.getType()).thenReturn(TestCaseType.CUSTOM);
        codingSpace.addTestCase(mockCase);

        // when
        codingSpace.deleteTestCase(1L);

        // then
        assertThat(codingSpace.getTestCases()).isEmpty();
    }

    @Test
    void 기본_테스트_케이스_제거시_예외가_발생한다() {
        // given
        CodingSpace codingSpace = new CodingSpace();
        TestCase mockCase = mock(TestCase.class);
        when(mockCase.getId()).thenReturn(1L);
        when(mockCase.getType()).thenReturn(TestCaseType.DEFAULT);
        codingSpace.addTestCase(mockCase);

        // when & then
        assertThatThrownBy(() -> codingSpace.deleteTestCase(1L))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.CAN_NOT_REMOVE_DEFAULT_CASE);
    }

    @Test
    void 제거하려는_테스트_케이스가_없는_경우_예외가_발생한다() {
        // given
        CodingSpace codingSpace = new CodingSpace();

        // when & then
        assertThatThrownBy(() -> codingSpace.deleteTestCase(1L))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CodingSpaceExceptionCode.NON_EXISTENT_CASE);
    }

}