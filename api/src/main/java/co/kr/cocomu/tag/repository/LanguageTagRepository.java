package co.kr.cocomu.tag.repository;

import co.kr.cocomu.tag.domain.LanguageTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageTagRepository extends JpaRepository<LanguageTag, Long> {

    int countByIdIn(List<Long> ids);

}
