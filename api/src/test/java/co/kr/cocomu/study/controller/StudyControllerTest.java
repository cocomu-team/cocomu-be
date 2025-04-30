package co.kr.cocomu.study.controller;

import co.kr.cocomu.common.BaseControllerTest;
import co.kr.cocomu.common.api.Api;
import co.kr.cocomu.common.api.NoContent;
import co.kr.cocomu.common.template.DeleteRequestTemplate;
import co.kr.cocomu.common.template.GetRequestTemplate;
import co.kr.cocomu.common.template.PatchRequestTemplate;
import co.kr.cocomu.common.template.PostRequestTemplate;
import co.kr.cocomu.common.template.PutRequestTemplate;
import co.kr.cocomu.tag.service.LanguageTagService;
import co.kr.cocomu.study.controller.code.StudyApiCode;
import co.kr.cocomu.study.dto.page.StudyDetailPageDto;
import co.kr.cocomu.study.dto.request.*;
import co.kr.cocomu.study.dto.response.*;
import co.kr.cocomu.study.service.StudyService;
import co.kr.cocomu.study.service.StudyQueryService;
import co.kr.cocomu.tag.service.WorkbookTagService;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(StudyController.class)
class StudyControllerTest extends BaseControllerTest {

    private static final String PATH_PREFIX = "/api/v1/studies";

    @MockBean private StudyService studyService;
    @MockBean private StudyQueryService studyQueryService;
    @MockBean private WorkbookTagService workbookTagService;
    @MockBean private LanguageTagService languageTagService;

    @Test
    void 스터디_생성_요청이_성공한다() {
        // given
        CreateStudyDto dto = new CreateStudyDto("코코무", true, "password", "",10, List.of(1L), List.of(1L));
        when(studyService.create(1L, dto)).thenReturn(1L);

        // when
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(PATH_PREFIX, dto);

        // then
        Api<Long> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.CREATE_STUDY_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.CREATE_STUDY_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(1L);
    }

    @Test
    void 스터디_참여_요청이_성공한다() {
        // given
        PasswordDto dto = new PasswordDto(null);
        when(studyService.join(1L, 1L, dto)).thenReturn(1L);

        // when
        String path = PATH_PREFIX + "/1";
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(path, dto);

        // then
        Api<Long> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.JOIN_STUDY_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.JOIN_STUDY_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(1L);
    }

    @Test
    void 스터디_수정_요청이_성공한다() {
        // given
        EditStudyDto dto = new EditStudyDto("", "", true, null, 0, List.of(), List.of());
        when(studyService.editStudy(1L, 1L, dto)).thenReturn(1L);

        // when
        String path = PATH_PREFIX + "/1";
        ValidatableMockMvcResponse response = PutRequestTemplate.executeWithBody(path, dto);

        // then
        Api<Long> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.EDIT_STUDY_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.EDIT_STUDY_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(1L);
    }

    @Test
    void 스터디_나가기_요청이_성공한다() {
        // when
        String path = PATH_PREFIX + "/1";
        ValidatableMockMvcResponse response = PatchRequestTemplate.execute(path);

        // then
        verify(studyService).leaveMember(1L, 1L);
        NoContent result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.LEAVE_STUDY_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.LEAVE_STUDY_SUCCESS.getMessage());
    }

    @Test
    void 스터디_삭제_요청이_성공한다() {
        // when
        String path = PATH_PREFIX + "/1";
        ValidatableMockMvcResponse response = DeleteRequestTemplate.execute(path);

        // then
        verify(studyService).removeStudy(1L, 1L);
        NoContent result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(StudyApiCode.REMOVE_STUDY_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(StudyApiCode.REMOVE_STUDY_SUCCESS.getMessage());
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