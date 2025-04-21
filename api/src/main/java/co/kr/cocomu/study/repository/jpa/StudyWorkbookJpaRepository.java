package co.kr.cocomu.study.repository.jpa;

import co.kr.cocomu.study.domain.StudyWorkbook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyWorkbookJpaRepository extends JpaRepository<StudyWorkbook, Long> {
    List<StudyWorkbook> findAllByStudyId(Long studyId);
}
