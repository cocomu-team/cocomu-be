package co.kr.cocomu.study.strategy;

import co.kr.cocomu.common.annotation.Business;
import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Business
@RequiredArgsConstructor
public class PrivateStudyStrategy implements StudyStrategy {

    private final PasswordEncoder passwordEncoder;

    @Override
    public void updateStatus(final Study study, final String password) {
        if (password == null || password.isEmpty()) {
            throw new BadRequestException(StudyExceptionCode.REQUIRED_STUDY_PASSWORD);
        }
        if (password.length() < 4 || password.length() > 6) {
            throw new BadRequestException(StudyExceptionCode.WRONG_PASSWORD_RULES);
        }

        final String encodedPassword = passwordEncoder.encode(password);
        study.updatePrivateStatus(encodedPassword);
    }

    @Override
    public boolean matchPassword(final String password, final String encodedPassword) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return passwordEncoder.matches(password, encodedPassword);
    }

}
