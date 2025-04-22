package co.kr.cocomu.admin.service;

import co.kr.cocomu.admin.dto.request.CreateLanguageRequest;
import co.kr.cocomu.admin.dto.request.CreateWorkbookRequest;
import co.kr.cocomu.admin.exception.AdminExceptionCode;
import co.kr.cocomu.common.exception.domain.NotFoundException;
import co.kr.cocomu.language.domain.Language;
import co.kr.cocomu.workbook.domain.Workbook;
import co.kr.cocomu.study.dto.response.LanguageDto;
import co.kr.cocomu.workbook.dto.WorkbookDto;
import co.kr.cocomu.language.repository.LanguageJpaRepository;
import co.kr.cocomu.workbook.repository.WorkbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final WorkbookRepository workbookRepository;
    private final LanguageJpaRepository languageJpaRepository;

    public WorkbookDto addWorkbook(final CreateWorkbookRequest dto) {
        final Workbook workBook = Workbook.of(dto.workbookName(), dto.workbookImageUrl());
        return workbookRepository.save(workBook).toDto();
    }

    public LanguageDto addLanguage(final CreateLanguageRequest dto) {
        final Language language = Language.of(dto.languageName(), dto.languageImageUrl());
        return languageJpaRepository.save(language).toDto();
    }

    public void deleteLanguage(final Long languageId) {
        final Language language = languageJpaRepository.findById(languageId)
            .orElseThrow(() -> new NotFoundException(AdminExceptionCode.NOT_FOUND_LANGUAGE));
        languageJpaRepository.delete(language);
    }

    public void deleteWorkbook(final Long workbookId) {
        final Workbook workBook = workbookRepository.findById(workbookId)
            .orElseThrow(() -> new NotFoundException(AdminExceptionCode.NOT_FOUND_WORKBOOK));
        workbookRepository.delete(workBook);
    }

}
