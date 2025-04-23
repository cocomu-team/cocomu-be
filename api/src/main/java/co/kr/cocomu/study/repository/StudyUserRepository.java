package co.kr.cocomu.study.repository;

import co.kr.cocomu.study.domain.StudyUser;
import co.kr.cocomu.study.repository.query.StudyUserQueryRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyUserRepository extends JpaRepository<StudyUser, Long>, StudyUserQueryRepository {

    @Query("""
        SELECT CASE WHEN COUNT(su) > 0 THEN true ELSE false END FROM StudyUser su
        WHERE su.user.id = :userId AND su.study.id = :studyId AND su.status = 'JOIN'
    """)
    boolean isUserJoinedStudy(Long userId, Long studyId);

    Optional<StudyUser> findByUser_IdAndStudy_Id(Long userId, Long studyId);

}
