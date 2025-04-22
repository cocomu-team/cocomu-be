package co.kr.cocomu.workbook.service;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.workbook.domain.Workbook;
import co.kr.cocomu.workbook.exception.WorkbookExceptionCode;
import co.kr.cocomu.workbook.repository.WorkbookRepository;
import co.kr.cocomu.workbook.dto.WorkbookDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkbookQueryService {

    private final WorkbookRepository workbookRepository;

    public List<Workbook> getWorkbooksByIdIn(final List<Long> workbookIds) {
        final List<Workbook> workbooks = workbookRepository.findAllById(workbookIds);
        if (workbooks.size() != workbookIds.size()) {
            throw new BadRequestException(WorkbookExceptionCode.INVALID_REQUEST);
        }

        return workbooks;
    }

    public List<WorkbookDto> getAllWorkbooks() {
        return workbookRepository.findAll()
                .stream()
                .map(Workbook::toDto)
                .toList();
    }

}
