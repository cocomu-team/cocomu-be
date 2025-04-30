package co.kr.cocomu.study.repository;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.vo.StudyStatus;
import co.kr.cocomu.study.repository.query.StudyQueryRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long>, StudyQueryRepository {
    Optional<Study> findByIdAndStatusNot(Long id, StudyStatus status);
}
