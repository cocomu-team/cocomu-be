package co.kr.cocomu.study.controller;

import co.kr.cocomu.common.BaseExecutorControllerTest;
import co.kr.cocomu.common.api.Api;
import co.kr.cocomu.common.api.NoContent;
import co.kr.cocomu.common.template.GetRequestTemplate;
import co.kr.cocomu.common.template.PostRequestTemplate;
import co.kr.cocomu.tag.service.LanguageTagService;
import co.kr.cocomu.study.controller.code.StudyApiCode;
import co.kr.cocomu.study.dto.page.StudyDetailPageDto;
import co.kr.cocomu.study.dto.page.StudyPageDto;
import co.kr.cocomu.study.dto.request.*;
import co.kr.cocomu.study.dto.response.*;
import co.kr.cocomu.study.service.command.StudyCommandService;
import co.kr.cocomu.study.service.query.StudyQueryService;
import co.kr.cocomu.tag.service.WorkbookTagService;
import co.kr.cocomu.tag.dto.WorkbookDto;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@WebMvcTest(StudyController.class)
class StudyExecutorControllerTest extends BaseExecutorControllerTest {

    private static final String PATH_PREFIX = "/api/v1/studies";

    @MockBean private StudyCommandService studyCommandService;
    @MockBean private StudyQueryService studyQueryService;
    @MockBean private WorkbookTagService workbookTagService;
    @MockBean private LanguageTagService languageTagService;

    @Test
    void 공개방_생성_요청이_성공한다() {
        // given
        CreatePublicStudyDto dto = new CreatePublicStudyDto("코코무", List.of(), List.of(), "", 10);
        when(studyCommandService.createPublicStudy(1L, dto)).thenReturn(1L);

        // when
        String path = PATH_PREFIX + "/public";
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(path, dto);

        // then
        Api<Long> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.CREATE_STUDY_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.CREATE_STUDY_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(1L);
    }

    @Test
    void 공개방_참여_요청이_성공한다() {
        // given
        when(studyCommandService.joinPublicStudy(1L, 1L)).thenReturn(1L);

        // when
        String path = PATH_PREFIX + "/public/1/join";
        ValidatableMockMvcResponse response = PostRequestTemplate.execute(path);

        // then
        Api<Long> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.JOIN_STUDY_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.JOIN_STUDY_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(1L);
    }

    @Test
    void 전체_스터디_조회_요청이_성공한다() {
        // given
        AllStudyCardDto mockResult = 전체스터디조회설정(1L);

        // when
        ValidatableMockMvcResponse response = GetRequestTemplate.execute(PATH_PREFIX);

        // then
        전체스터디조회결과(response, mockResult);
    }

    @Test
    void 인증_정보_없이_전체_스터디_조회_요청이_성공한다() {
        // given
        AllStudyCardDto mockResult = 전체스터디조회설정(null);

        // when
        ValidatableMockMvcResponse response = GetRequestTemplate.executeNoAuth(PATH_PREFIX);

        // then
        전체스터디조회결과(response, mockResult);
    }

    @Test
    void 스터디_정보페이지_조회가_된다() {
        // given
        StudyCardDto mockResult = new StudyCardDto();
        when(studyQueryService.getStudyCard(1L, null)).thenReturn(mockResult);

        // when
        String path = PATH_PREFIX + "/1/study-information";
        ValidatableMockMvcResponse response = GetRequestTemplate.executeNoAuth(path);

        // then
        Api<StudyCardDto> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.GET_STUDY_INFO_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.GET_STUDY_INFO_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockResult);
    }

    @Test
    void 스터디_리스트_페이지_조회가_된다() {
        // given
        GetAllStudyFilterDto noFilter = new GetAllStudyFilterDto(null, null, null, null, null, null);
        List<WorkbookDto> mockWorkbooks = List.of(new WorkbookDto(1L, "백준", "이미지URL"));
        List<LanguageDto> mockLanguages = List.of(new LanguageDto(1L, "Java", "이미지URL"));
        AllStudyCardDto mockStudyCards = new AllStudyCardDto(10L, List.of());
        StudyPageDto mockResult = new StudyPageDto(mockWorkbooks, mockLanguages, mockStudyCards);

        when(workbookTagService.getAllWorkbooks()).thenReturn(mockWorkbooks);
        when(languageTagService.getAllTags()).thenReturn(mockLanguages);
        when(studyQueryService.getAllStudyCard(noFilter, null)).thenReturn(mockStudyCards);

        // when
        String path = PATH_PREFIX + "/page";
        ValidatableMockMvcResponse response = GetRequestTemplate.executeNoAuth(path);

        // then
        Api<StudyPageDto> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.GET_STUDY_PAGE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.GET_STUDY_PAGE_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockResult);
    }

    @Test
    void 스터디_상세_정보를_조회_요청이_성공한다() {
        // given
        StudyDetailPageDto mockResult = new StudyDetailPageDto();
        when(studyQueryService.getStudyDetailPage(1L, 1L)).thenReturn(mockResult);

        // when
        String path = PATH_PREFIX + "/1";
        ValidatableMockMvcResponse response = GetRequestTemplate.execute(path);

        // then
        Api<StudyDetailPageDto> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.GET_STUDY_DETAIL_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.GET_STUDY_DETAIL_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockResult);
    }

    @Test
    void 스터디_작성_페이지_조회_요청이_성공한다() {
        // given
        when(languageTagService.getAllTags()).thenReturn(List.of());
        when(workbookTagService.getAllWorkbooks()).thenReturn(List.of());

        // when
        String path = PATH_PREFIX + "/filter-options";
        ValidatableMockMvcResponse response = GetRequestTemplate.executeNoAuth(path);

        // then
        Api<FilterOptionsDto> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.GET_FILTER_OPTIONS_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.GET_FILTER_OPTIONS_SUCCESS.getMessage());
        assertThat(result.result().languages()).isEmpty();
        assertThat(result.result().workbooks()).isEmpty();
    }

    @Test
    void 비공개_스터디_생성_요청이_성공한다() {
        // given
        CreatePrivateStudyDto dto = new CreatePrivateStudyDto("123", "1234", List.of(), List.of(), "", 50);
        when(studyCommandService.createPrivateStudy(dto, 1L)).thenReturn(1L);

        // when
        String path = PATH_PREFIX + "/private";
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(path, dto);

        // then
        Api<Long> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.CREATE_STUDY_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.CREATE_STUDY_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(1L);
    }

    @Test
    void 비공개_스터디_참여_요청이_성공한다() {
        // given
        JoinPrivateStudyDto dto = new JoinPrivateStudyDto("1234");
        when(studyCommandService.joinPrivateStudy(1L, 1L, "1234")).thenReturn(1L);

        // when
        String path = PATH_PREFIX + "/private/1/join";
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(path, dto);

        // then
        Api<Long> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.JOIN_STUDY_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.JOIN_STUDY_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(1L);
    }

    @Test
    void 스터디_나가기_요청이_성공한다() {
        // given
        doNothing().when(studyCommandService).leaveMember(1L, 1L);

        // when
        String path = PATH_PREFIX + "/1/leave";
        ValidatableMockMvcResponse response = PostRequestTemplate.execute(path);

        // then
        NoContent result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.LEAVE_STUDY_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.LEAVE_STUDY_SUCCESS.getMessage());
    }

    @Test
    void 스터디_삭제_요청이_성공한다() {
        // given
        doNothing().when(studyCommandService).removeStudy(1L, 1L);

        // when
        String path = PATH_PREFIX + "/1/remove";
        ValidatableMockMvcResponse response = PostRequestTemplate.execute(path);

        // then
        NoContent result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.REMOVE_STUDY_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.REMOVE_STUDY_SUCCESS.getMessage());
    }

    @Test
    void 스터디원_조회_요청이_성공한다() {
        // given
        StudyUserFilterDto dto = new StudyUserFilterDto("", 1L);
        when(studyQueryService.findAllMembers(1L, 1L, dto)).thenReturn(List.of());

        // when
        String path = PATH_PREFIX + "/1/members";
        ValidatableMockMvcResponse response = GetRequestTemplate.execute(path);

        // then
        Api<List<StudyMemberDto>> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.GET_ALL_MEMBERS_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.GET_ALL_MEMBERS_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(List.of());
    }

    @Test
    void 공개_스터디로_변경하는_요청이_성공한다() {
        // given
        EditStudyDto dto = new EditStudyDto("", "", true, null, 0, List.of(), List.of());
        when(studyCommandService.editStudy(1L, 1L, dto)).thenReturn(1L);

        // when
        String path = PATH_PREFIX + "/1/edit";
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(path, dto);

        // then
        Api<Long> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.EDIT_STUDY_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.EDIT_STUDY_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(1L);
    }

    @Test
    void 비공개_스터디로_변경하는_요청이_성공한다() {
        // given
        EditStudyDto dto = new EditStudyDto("", "", false, "1234", 0, List.of(), List.of());
        when(studyCommandService.editStudy(1L, 1L, dto)).thenReturn(1L);

        // when
        String path = PATH_PREFIX + "/1/edit";
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(path, dto);

        // then
        Api<Long> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.EDIT_STUDY_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.EDIT_STUDY_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(1L);
    }

    /*
    ===================   아래로 공통 테스트 설정입니다.  ===================
     */
    private @NotNull AllStudyCardDto 전체스터디조회설정(Long uesrId) {
        GetAllStudyFilterDto dto = new GetAllStudyFilterDto(null, null, null, null, null, null);
        AllStudyCardDto mockResult = new AllStudyCardDto(10L, List.of());
        when(studyQueryService.getAllStudyCard(dto, uesrId)).thenReturn(mockResult);
        return mockResult;
    }

    private static void 전체스터디조회결과(final ValidatableMockMvcResponse response, final AllStudyCardDto mockResult) {
        Api<AllStudyCardDto> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.GET_ALL_STUDIES_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.GET_ALL_STUDIES_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockResult);
    }

}