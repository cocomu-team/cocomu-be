package co.kr.cocomu.study.strategy;

import co.kr.cocomu.study.domain.Study;

public interface StudyStrategy {

    void updateStatus(Study study, String password);

    boolean matchPassword(String password, String encodedPassword);

}
