package co.kr.cocomu.tag.service;

import co.kr.cocomu.study.dto.response.LanguageDto;
import co.kr.cocomu.tag.domain.LanguageTag;
import co.kr.cocomu.tag.repository.LanguageTagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LanguageTagService {

    private final LanguageTagRepository languageTagRepository;

    public boolean existsAllTagIds(final List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return false;
        }

        final int count = languageTagRepository.countByIdIn(tagIds);
        return count == tagIds.size();
    }

    public List<LanguageDto> getAllTags() {
        return languageTagRepository.findAll()
                .stream()
                .map(LanguageTag::toDto)
                .toList();
    }

}
