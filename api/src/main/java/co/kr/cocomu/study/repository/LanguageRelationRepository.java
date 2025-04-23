package co.kr.cocomu.study.repository;

import co.kr.cocomu.study.domain.LanguageRelation;
import co.kr.cocomu.study.repository.query.LanguageRelationQuery;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRelationRepository extends JpaRepository<LanguageRelation, Long>, LanguageRelationQuery {

    List<LanguageRelation> findAllByStudyId(Long studyId);
    Optional<LanguageRelation> findByStudy_idAndLanguageTag_IdAndDeletedIsFalse(Long studyId, Long tagId);

}
