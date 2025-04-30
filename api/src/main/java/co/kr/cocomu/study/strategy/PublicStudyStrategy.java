package co.kr.cocomu.study.strategy;

import co.kr.cocomu.study.domain.Study;
import org.springframework.stereotype.Component;

@Component
public class PublicStudyStrategy implements StudyStrategy {

    @Override
    public void updateStatus(final Study study, final String password) {
        study.updatePublicStatus();
    }

    @Override
    public boolean matchPassword(final String password, final String encodedPassword) {
        return true;
    }

}
