package co.kr.cocomu.workbook.repository;

import co.kr.cocomu.workbook.domain.Workbook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkbookRepository extends JpaRepository<Workbook, Long> {
}
