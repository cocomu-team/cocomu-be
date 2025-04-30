package co.kr.cocomu.study.repository;

import co.kr.cocomu.study.domain.LanguageRelation;
import co.kr.cocomu.study.repository.query.LanguageRelationQuery;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRelationRepository extends JpaRepository<LanguageRelation, Long>, LanguageRelationQuery {

    List<LanguageRelation> findAllByStudyId(Long studyId);
    boolean existsByStudy_IdAndLanguageTagIdAndDeletedIsFalse(Long studyId, Long tagId);

}
