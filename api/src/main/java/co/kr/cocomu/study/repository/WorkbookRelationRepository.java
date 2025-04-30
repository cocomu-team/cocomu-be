package co.kr.cocomu.study.repository;

import co.kr.cocomu.study.domain.WorkbookRelation;
import co.kr.cocomu.study.repository.query.WorkbookRelationQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkbookRelationRepository extends JpaRepository<WorkbookRelation, Long>, WorkbookRelationQuery {
    List<WorkbookRelation> findAllByStudyId(Long studyId);
}
