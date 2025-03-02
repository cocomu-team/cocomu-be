package co.kr.cocomu.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import co.kr.cocomu.common.exception.domain.NotFoundException;
import co.kr.cocomu.user.domain.User;
import co.kr.cocomu.user.dto.request.UserJoinRequest;
import co.kr.cocomu.user.dto.response.UserResponse;
import co.kr.cocomu.user.exception.UserExceptionCode;
import co.kr.cocomu.user.repository.UserJpaRepository;
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

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private UserService userService;

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
    void 사용자를_조회한다() {
        // given
        Long userId = 1L;
        given(userJpaRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        UserResponse result = userService.findUser(userId);

        // then
        assertThat(result).isEqualTo(userResponse);
        verify(userJpaRepository).findById(1L);
    }

    @Test
    void 사용자_정보가_없을_때_조회하면_예외가_발생한다() {
        // given
        Long userId = 1L;
        given(userJpaRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findUser(userId))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", UserExceptionCode.USER_NOT_FOUND);
    }

}