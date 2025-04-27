package co.kr.cocomu.study.service;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public void validatePassword(final String password, final String encodedPassword) {
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new BadRequestException(StudyExceptionCode.STUDY_PASSWORD_WRONG);
        }
    }

    public String encodeStudyPassword(final String password) {
        return passwordEncoder.encode(password);
    }

}
