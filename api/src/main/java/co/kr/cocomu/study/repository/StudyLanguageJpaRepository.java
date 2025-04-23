package co.kr.cocomu.study.repository;

import co.kr.cocomu.study.domain.StudyLanguage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyLanguageJpaRepository extends JpaRepository<StudyLanguage, Long> {

    List<StudyLanguage> findAllByStudyId(Long studyId);
    Optional<StudyLanguage> findByStudy_idAndLanguageTag_IdAndDeletedIsFalse(Long studyId, Long tagId);

}
