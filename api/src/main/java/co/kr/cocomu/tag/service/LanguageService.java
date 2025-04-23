package co.kr.cocomu.tag.service;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.dto.response.LanguageDto;
import co.kr.cocomu.tag.domain.LanguageTag;
import co.kr.cocomu.tag.exception.LanguageExceptionCode;
import co.kr.cocomu.tag.repository.LanguageJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LanguageService {

    private final LanguageJpaRepository languageJpaRepository;

    public List<LanguageTag> getLanguagesByIdIn(final List<Long> languageIds) {
        final List<LanguageTag> languageTags = languageJpaRepository.findAllById(languageIds);
        if (languageTags.size() != languageIds.size()) {
            throw new BadRequestException(LanguageExceptionCode.INVALID_REQUEST);
        }

        return languageTags;
    }

    public List<LanguageDto> getAllLanguages() {
        return languageJpaRepository.findAll()
                .stream()
                .map(LanguageTag::toDto)
                .toList();
    }

}
