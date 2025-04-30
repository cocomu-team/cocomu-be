package co.kr.cocomu.user.controller;

import co.kr.cocomu.codingspace.dto.response.CodingSpaceDto;
import co.kr.cocomu.codingspace.service.CodingSpaceQueryService;
import co.kr.cocomu.common.api.Api;
import co.kr.cocomu.common.api.NoContent;
import co.kr.cocomu.study.dto.response.StudyCardDto;
import co.kr.cocomu.study.service.StudyQueryService;
import co.kr.cocomu.user.controller.code.UserApiCode;
import co.kr.cocomu.user.controller.docs.UserControllerDocs;
import co.kr.cocomu.user.dto.response.UserInfoDto;
import co.kr.cocomu.user.dto.request.ProfileUpdateDto;
import co.kr.cocomu.user.dto.request.UserJoinRequest;
import co.kr.cocomu.user.dto.response.UserResponse;
import co.kr.cocomu.user.service.UserService;
import co.kr.cocomu.user.uploader.ProfileImageUploader;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;
    private final ProfileImageUploader profileImageUploader;
    private final StudyQueryService studyQueryService;
    private final CodingSpaceQueryService codingSpaceQueryService;

    @GetMapping("/me")
    public Api<UserResponse> getProfile(@AuthenticationPrincipal Long userId) {
        final UserResponse result = userService.getMyProfile(userId);
        return Api.of(UserApiCode.GET_USER_INFO_SUCCESS, result);
    }

    @GetMapping("/{userId}")
    public Api<UserInfoDto> getUserInformation(
        @AuthenticationPrincipal final Long authUserId,
        @PathVariable final Long userId
    ) {
        final UserInfoDto result = userService.getUserInformation(userId, authUserId);
        return Api.of(UserApiCode.GET_USER_INFO_SUCCESS, result);
    }

    @GetMapping("/all") @Deprecated @Profile("!prod")
    public Api<List<UserResponse>> getUsers() {
        final List<UserResponse> users = userService.findAll();
        return Api.of(UserApiCode.ALL_USER_FOUND_SUCCESS, users);
    }

    @PostMapping("/join") @Deprecated @Profile("!prod")
    public Api<UserResponse> createUser(@Valid @RequestBody final UserJoinRequest dto) {
        final UserResponse result = userService.saveUser(dto);
        return Api.of(UserApiCode.USER_JOIN_SUCCESS, result);
    }

    @PostMapping("/me")
    public NoContent updateUser(
        @AuthenticationPrincipal final Long userId,
        @Valid @RequestBody final ProfileUpdateDto dto
    ) {
        userService.updateUser(userId, dto.nickname(), dto.profileImageUrl());
        return NoContent.from(UserApiCode.PROFILE_UPDATE_SUCCESS);
    }

    @PostMapping(value = "/me/profile-image", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Api<String> uploadProfileImage(
        @RequestBody final MultipartFile image,
        @AuthenticationPrincipal final Long userId
    ) {
        final String uploadedUrl = profileImageUploader.uploadProfileImage(image, userId);
        return Api.of(UserApiCode.PROFILE_UPDATE_SUCCESS, uploadedUrl);
    }

    @GetMapping("/{userId}/studies")
    public Api<List<StudyCardDto>> getStudyCards(
        @PathVariable final Long userId,
        @AuthenticationPrincipal final Long viewerId,
        @RequestParam(required = false) final Long lastIndex
    ) {
        final List<StudyCardDto> result = studyQueryService.getStudyCardsByUser(userId, viewerId, lastIndex);
        return Api.of(UserApiCode.GET_STUDIES_SUCCESS, result);
    }

    @GetMapping("/{userId}/coding-spaces")
    public Api<List<CodingSpaceDto>> getCodingSpaces(
        @PathVariable final Long userId,
        @AuthenticationPrincipal final Long viewerId,
        @RequestParam(required = false) final Long lastIndex
    ) {
        final List<CodingSpaceDto> result = codingSpaceQueryService.getSpacesByUser(userId, viewerId, lastIndex);
        return Api.of(UserApiCode.GET_SPACES_SUCCESS, result);
    }

}
