package co.kr.cocomu.tag.service;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.dto.response.LanguageDto;
import co.kr.cocomu.tag.domain.LanguageTag;
import co.kr.cocomu.tag.exception.LanguageTagExceptionCode;
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

    public List<LanguageTag> getTagsByIdIn(final List<Long> languageIds) {
        final List<LanguageTag> languageTags = languageTagRepository.findAllById(languageIds);
        if (languageTags.size() != languageIds.size()) {
            throw new BadRequestException(LanguageTagExceptionCode.INVALID_REQUEST);
        }

        return languageTags;
    }

    public List<LanguageDto> getAllTags() {
        return languageTagRepository.findAll()
                .stream()
                .map(LanguageTag::toDto)
                .toList();
    }

}
