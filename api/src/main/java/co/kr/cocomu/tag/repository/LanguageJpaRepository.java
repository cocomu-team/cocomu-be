package co.kr.cocomu.tag.repository;

import co.kr.cocomu.tag.domain.LanguageTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageJpaRepository extends JpaRepository<LanguageTag, Long> {
}
