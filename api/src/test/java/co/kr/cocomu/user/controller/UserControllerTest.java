package co.kr.cocomu.user.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import co.kr.cocomu.codingspace.dto.response.CodingSpaceDto;
import co.kr.cocomu.codingspace.service.CodingSpaceQueryService;
import co.kr.cocomu.common.BaseControllerTest;
import co.kr.cocomu.common.api.Api;
import co.kr.cocomu.common.api.NoContent;
import co.kr.cocomu.common.template.GetRequestTemplate;
import co.kr.cocomu.common.template.PostRequestTemplate;
import co.kr.cocomu.study.dto.response.StudyCardDto;
import co.kr.cocomu.study.service.query.StudyQueryService;
import co.kr.cocomu.user.controller.code.UserApiCode;
import co.kr.cocomu.user.dto.request.ProfileUpdateDto;
import co.kr.cocomu.user.dto.request.UserJoinRequest;
import co.kr.cocomu.user.dto.response.UserInfoDto;
import co.kr.cocomu.user.dto.response.UserResponse;
import co.kr.cocomu.user.service.UserService;
import co.kr.cocomu.user.uploader.ProfileImageUploader;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest {

    private static final String PATH_PREFIX = "/api/v1/users";

    @MockBean private UserService userService;
    @MockBean private ProfileImageUploader profileImageUploader;
    @MockBean private StudyQueryService studyQueryService;
    @MockBean private CodingSpaceQueryService codingSpaceQueryService;

    private UserResponse userResponse;

    @BeforeEach
    void setUser() {
        userResponse = new UserResponse(1L, "코코무", "https://cdn.cocomu.co.kr/images/profile.png");
    }

    @Test
    void 프로필_조회가_성공한다() {
        // given
        UserResponse mockDto = new UserResponse(1L, "", "");
        when(userService.getMyProfile(1L)).thenReturn(mockDto);

        // when
        String path = PATH_PREFIX + "/me";
        ValidatableMockMvcResponse response = GetRequestTemplate.execute(path);

        // then
        Api<UserResponse> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(UserApiCode.GET_USER_INFO_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(UserApiCode.GET_USER_INFO_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockDto);
    }

    @Test
    void 사용자_조회가_성공한다() {
        // given
        UserInfoDto mockDto = new UserInfoDto(1L, "", "", true);
        given(userService.getUserInformation(1L, 1L)).willReturn(mockDto);

        // when
        String path = PATH_PREFIX + "/1";
        ValidatableMockMvcResponse response = GetRequestTemplate.execute(path).status(HttpStatus.OK);

        // then
        Api<UserInfoDto> result = response.extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(UserApiCode.GET_USER_INFO_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(UserApiCode.GET_USER_INFO_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockDto);
    }

    @Test
    void 모든_사용자_조회가_성공한다() {
        // given
        String path = PATH_PREFIX + "/all";
        List<UserResponse> mockResult = List.of(userResponse);
        given(userService.findAll()).willReturn(mockResult);

        // when
        ValidatableMockMvcResponse response = GetRequestTemplate.execute(path).status(HttpStatus.OK);

        // then
        Api<List<UserResponse>> result = response.extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(UserApiCode.ALL_USER_FOUND_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(UserApiCode.ALL_USER_FOUND_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockResult);
    }

    @Test
    void 사용자_추가에_성공한다() {
        // given
        String path = PATH_PREFIX + "/join";
        UserJoinRequest dto = new UserJoinRequest("코코무");
        given(userService.saveUser(dto)).willReturn(userResponse);

        // when
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(path, dto);

        // then
        Api<UserResponse> result = response.extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(UserApiCode.USER_JOIN_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(UserApiCode.USER_JOIN_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(userResponse);
    }

    @Test
    void 프로필_수정_요청이_성공한다() {
        // given
        ProfileUpdateDto dto = new ProfileUpdateDto("", "");
        doNothing().when(userService).updateUser(anyLong(), anyString(), anyString());

        // when
        String path = PATH_PREFIX + "/me";
        ValidatableMockMvcResponse response = PostRequestTemplate.executeWithBody(path, dto);

        // then
        NoContent result = response.extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(UserApiCode.PROFILE_UPDATE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(UserApiCode.PROFILE_UPDATE_SUCCESS.getMessage());
    }

    @Test
    void 프로필_이미지_업로드_요청이_성공한다() throws IOException {
        // given
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "test".getBytes());
        when(profileImageUploader.uploadProfileImage(any(MultipartFile.class), anyLong())).thenReturn("imageUrl");

        // when
        String path = PATH_PREFIX + "/me/profile-image";
        ValidatableMockMvcResponse response = PostRequestTemplate.executeMultipartRequest(path, image);

        // then
        Api<String> result = response.extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(UserApiCode.PROFILE_UPDATE_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(UserApiCode.PROFILE_UPDATE_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo("imageUrl");
    }

    @Test
    void 참여한_스터디_목록_조회가_된다() {
        // given
        List<StudyCardDto> mockResult = List.of();
        when(studyQueryService.getStudyCardsByUser(1L, 1L, 1L)).thenReturn(mockResult);

        // when
        String path = PATH_PREFIX + "/1/studies";
        ValidatableMockMvcResponse response = GetRequestTemplate.execute(path);

        // then
        Api<List<StudyCardDto>> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(UserApiCode.GET_STUDIES_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(UserApiCode.GET_STUDIES_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockResult);
    }
    @Test
    void 참여한_스페이스_목록_조회가_된다() {
        // given
        List<CodingSpaceDto> mockResult = List.of();
        when(codingSpaceQueryService.getSpacesByUser(1L, 1L, 1L)).thenReturn(mockResult);

        // when
        String path = PATH_PREFIX + "/1/coding-spaces";
        ValidatableMockMvcResponse response = GetRequestTemplate.execute(path);

        // then
        Api<List<CodingSpaceDto>> result = response.status(HttpStatus.OK).extract().as(new TypeRef<>() {});
        assertThat(result.code()).isEqualTo(UserApiCode.GET_SPACES_SUCCESS.getCode());
        assertThat(result.message()).isEqualTo(UserApiCode.GET_SPACES_SUCCESS.getMessage());
        assertThat(result.result()).isEqualTo(mockResult);
    }

}