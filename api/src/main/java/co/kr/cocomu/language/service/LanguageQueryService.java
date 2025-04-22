package co.kr.cocomu.language.service;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.language.domain.Language;
import co.kr.cocomu.language.exception.LanguageExceptionCode;
import co.kr.cocomu.language.repository.LanguageJpaRepository;
import co.kr.cocomu.study.dto.response.LanguageDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LanguageQueryService {

    private final LanguageJpaRepository languageJpaRepository;

    public List<Language> getLanguagesByIdIn(final List<Long> languageIds) {
        final List<Language> languages = languageJpaRepository.findAllById(languageIds);
        if (languages.size() != languageIds.size()) {
            throw new BadRequestException(LanguageExceptionCode.INVALID_REQUEST);
        }

        return languages;
    }

    public List<LanguageDto> getAllLanguages() {
        return languageJpaRepository.findAll()
                .stream()
                .map(Language::toDto)
                .toList();
    }

}
