package co.kr.cocomu.tag.service;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.tag.domain.WorkbookTag;
import co.kr.cocomu.tag.exception.WorkbookExceptionCode;
import co.kr.cocomu.tag.repository.WorkbookRepository;
import co.kr.cocomu.tag.dto.WorkbookDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkbookService {

    private final WorkbookRepository workbookRepository;

    public List<WorkbookTag> getWorkbooksByIdIn(final List<Long> workbookIds) {
        final List<WorkbookTag> workbookTags = workbookRepository.findAllById(workbookIds);
        if (workbookTags.size() != workbookIds.size()) {
            throw new BadRequestException(WorkbookExceptionCode.INVALID_REQUEST);
        }

        return workbookTags;
    }

    public List<WorkbookDto> getAllWorkbooks() {
        return workbookRepository.findAll()
                .stream()
                .map(WorkbookTag::toDto)
                .toList();
    }

}
