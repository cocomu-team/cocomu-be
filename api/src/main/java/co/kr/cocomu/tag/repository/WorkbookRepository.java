package co.kr.cocomu.tag.repository;

import co.kr.cocomu.tag.domain.WorkbookTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkbookRepository extends JpaRepository<WorkbookTag, Long> {
}
