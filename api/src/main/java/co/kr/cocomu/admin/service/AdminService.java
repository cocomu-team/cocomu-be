package co.kr.cocomu.admin.service;

import co.kr.cocomu.admin.dto.request.CreateLanguageRequest;
import co.kr.cocomu.admin.dto.request.CreateWorkbookRequest;
import co.kr.cocomu.admin.exception.AdminExceptionCode;
import co.kr.cocomu.common.exception.domain.NotFoundException;
import co.kr.cocomu.tag.domain.LanguageTag;
import co.kr.cocomu.tag.domain.WorkbookTag;
import co.kr.cocomu.study.dto.response.LanguageDto;
import co.kr.cocomu.tag.dto.WorkbookDto;
import co.kr.cocomu.tag.repository.LanguageTagRepository;
import co.kr.cocomu.tag.repository.WorkbookTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final WorkbookTagRepository workbookTagRepository;
    private final LanguageTagRepository languageTagRepository;

    public WorkbookDto addWorkbook(final CreateWorkbookRequest dto) {
        final WorkbookTag workBook = WorkbookTag.of(dto.workbookName(), dto.workbookImageUrl());
        return workbookTagRepository.save(workBook).toDto();
    }

    public LanguageDto addLanguage(final CreateLanguageRequest dto) {
        final LanguageTag languageTag = LanguageTag.of(dto.languageName(), dto.languageImageUrl());
        return languageTagRepository.save(languageTag).toDto();
    }

    public void deleteLanguage(final Long languageId) {
        final LanguageTag languageTag = languageTagRepository.findById(languageId)
            .orElseThrow(() -> new NotFoundException(AdminExceptionCode.NOT_FOUND_LANGUAGE));
        languageTagRepository.delete(languageTag);
    }

    public void deleteWorkbook(final Long workbookId) {
        final WorkbookTag workBook = workbookTagRepository.findById(workbookId)
            .orElseThrow(() -> new NotFoundException(AdminExceptionCode.NOT_FOUND_WORKBOOK));
        workbookTagRepository.delete(workBook);
    }

}
