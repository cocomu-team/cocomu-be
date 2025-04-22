package co.kr.cocomu.language.repository;

import co.kr.cocomu.language.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageJpaRepository extends JpaRepository<Language, Long> {
}
