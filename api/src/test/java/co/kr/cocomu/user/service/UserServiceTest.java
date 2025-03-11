package co.kr.cocomu.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.common.exception.domain.NotFoundException;
import co.kr.cocomu.user.domain.User;
import co.kr.cocomu.user.dto.request.ProfileUpdateDto;
import co.kr.cocomu.user.dto.request.UserJoinRequest;
import co.kr.cocomu.user.dto.response.UserInfoDto;
import co.kr.cocomu.user.dto.response.UserResponse;
import co.kr.cocomu.user.exception.UserExceptionCode;
import co.kr.cocomu.user.repository.UserJpaRepository;
import co.kr.cocomu.user.uploader.ProfileImageUploader;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserJpaRepository userJpaRepository;
    @Mock private ProfileImageUploader profileImageUploader;

    @InjectMocks private UserService userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = User.createUser("코코무");
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "profileImageUrl", "https://cdn.cocomu.co.kr/images/profile.png");

        userResponse = new UserResponse(1L, "코코무", "https://cdn.cocomu.co.kr/images/profile.png");
    }

    @Test
    void 사용자_추가를_추가한다() {
        // given
        UserJoinRequest dto = new UserJoinRequest("코코무");
        given(userJpaRepository.save(any(User.class))).willReturn(user);

        // when
        UserResponse result = userService.saveUser(dto);

        // then
        assertThat(result).isEqualTo(userResponse);
        verify(userJpaRepository).save(any(User.class));
    }

    @Test
    void 전체_사용자_조회를_한다() {
        // given
        List<User> mockUsers = List.of(user);
        given(userJpaRepository.findAll()).willReturn(mockUsers);

        // when
        List<UserResponse> result = userService.findAll();

        // then
        assertThat(result).isEqualTo(List.of(userResponse));
        verify(userJpaRepository).findAll();
    }

    @Test
    void 사용자_정보를_조회한다() {
        // given
        User mockUser = mock(User.class);
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mockUser.getId()).thenReturn(1L);

        // when
        UserInfoDto result = userService.getUserInformation(1L, 1L);

        // then
        assertThat(result.me()).isTrue();
    }

    @Test
    void 사용자_정보가_없을_때_조회하면_예외가_발생한다() {
        // given
        Long userId = 1L;
        when(userJpaRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserInformation(userId, 1L))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", UserExceptionCode.USER_NOT_FOUND);
    }

    @Test
    void 사용자_프로필_수정시_기본_프로필이_아니면_태그_설정을_한다() {
        // given
        ProfileUpdateDto mockDto = mock(ProfileUpdateDto.class);
        User mockUser = mock(User.class);
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotDefaultImage()).thenReturn(true);

        // when
        userService.updateUser(1L, mockDto);

        // then
        verify(profileImageUploader).markAsUnused(mockUser.getProfileImageUrl());
        verify(profileImageUploader).confirmImage(mockDto.profileImageUrl());
        verify(mockUser).updateProfile(mockDto.nickname(), mockDto.profileImageUrl());
    }

    @Test
    void 사용자_프로필_수정시_기본_프로필이라면_태그_설정을_하지_않는다() {
        // given
        ProfileUpdateDto mockDto = mock(ProfileUpdateDto.class);
        User mockUser = mock(User.class);
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotDefaultImage()).thenReturn(false);

        // when
        userService.updateUser(1L, mockDto);

        // then
        verify(profileImageUploader).confirmImage(mockDto.profileImageUrl());
        verify(mockUser).updateProfile(mockDto.nickname(), mockDto.profileImageUrl());
    }

}