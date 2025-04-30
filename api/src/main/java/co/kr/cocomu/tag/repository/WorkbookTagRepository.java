package co.kr.cocomu.tag.repository;

import co.kr.cocomu.tag.domain.WorkbookTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkbookTagRepository extends JpaRepository<WorkbookTag, Long> {

    int countByIdIn(List<Long> ids);

}
