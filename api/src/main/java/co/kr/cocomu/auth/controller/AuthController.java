package co.kr.cocomu.auth.controller;

import co.kr.cocomu.auth.controller.docs.AuthControllerDocs;
import co.kr.cocomu.auth.domain.OAuth2Provider;
import co.kr.cocomu.auth.controller.code.AuthApiCode;
import co.kr.cocomu.auth.dto.request.OAuthRequest;
import co.kr.cocomu.auth.dto.response.AuthResponse;
import co.kr.cocomu.auth.service.CookieService;
import co.kr.cocomu.auth.service.GithubService;
import co.kr.cocomu.auth.service.GoogleService;
import co.kr.cocomu.auth.service.KakaoService;
import co.kr.cocomu.common.api.Api;
import co.kr.cocomu.common.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController implements AuthControllerDocs {

    private final JwtProvider jwtProvider;
    private final CookieService cookieService;
    private final GithubService githubService;
    private final KakaoService kakaoService;
    private final GoogleService googleService;

    @PostMapping("/oauth-login")
    public Api<AuthResponse> loginWithOAuth2(
        @Valid @RequestBody final OAuthRequest request,
        final HttpServletResponse response
    ) {
        log.info(request.toString());
        final Long userId = oAuthLoginByProvider(request);
        final String accessToken = jwtProvider.issueAccessToken(userId);
        final String refreshToken = jwtProvider.issueRefreshToken(userId);

        cookieService.setRefreshTokenCookie(response, refreshToken);

        return Api.of(AuthApiCode.LOGIN_SUCCESS, new AuthResponse(accessToken));
    }

    private Long oAuthLoginByProvider(final OAuthRequest request) {
        if (request.provider() == OAuth2Provider.GITHUB) {
            return githubService.signupWithLogin(request.oauthCode());
        }
        if (request.provider() == OAuth2Provider.KAKAO) {
            return kakaoService.signupWithLogin(request.oauthCode());
        }
        return googleService.signupWithLogin(request.oauthCode());
    }

    @Profile(value = "!prod")
    @GetMapping("/backdoor")
    public Api<AuthResponse> login(final HttpServletResponse response) {
        final String accessToken = jwtProvider.issueAccessToken(1L);
        final String refreshToken = jwtProvider.issueRefreshToken(1L);

        cookieService.setRefreshTokenCookie(response, refreshToken);

        return Api.of(AuthApiCode.LOGIN_SUCCESS, new AuthResponse(accessToken));
    }

}
