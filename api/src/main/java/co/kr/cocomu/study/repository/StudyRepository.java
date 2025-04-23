package co.kr.cocomu.study.repository;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.repository.query.StudyQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long>, StudyQueryRepository {
}
