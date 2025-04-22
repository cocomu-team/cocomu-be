package co.kr.cocomu.study.repository.jpa;

import co.kr.cocomu.study.domain.StudyLanguage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyLanguageJpaRepository extends JpaRepository<StudyLanguage, Long> {

    List<StudyLanguage> findAllByStudyId(Long studyId);

}
