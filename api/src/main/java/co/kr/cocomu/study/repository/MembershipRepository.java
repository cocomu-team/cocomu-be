package co.kr.cocomu.study.repository;

import co.kr.cocomu.study.domain.Membership;
import co.kr.cocomu.study.repository.query.MembershipQuery;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MembershipRepository extends JpaRepository<Membership, Long>, MembershipQuery {

    @Query("""
        SELECT CASE WHEN COUNT(sm) > 0 THEN true ELSE false END FROM Membership sm
        WHERE sm.userId = :userId AND sm.study.id = :studyId AND sm.status = 'JOIN'
    """)
    boolean isUserJoinedStudy(Long userId, Long studyId);

    Optional<Membership> findByUserIdAndStudy_Id(Long userId, Long studyId);

}
