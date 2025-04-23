package co.kr.cocomu.tag.service;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.tag.domain.WorkbookTag;
import co.kr.cocomu.tag.exception.WorkbookTagExceptionCode;
import co.kr.cocomu.tag.repository.WorkbookTagRepository;
import co.kr.cocomu.tag.dto.WorkbookDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkbookTagService {

    private final WorkbookTagRepository workbookTagRepository;

    public List<WorkbookTag> getTagsByIdIn(final List<Long> workbookIds) {
        final List<WorkbookTag> workbookTags = workbookTagRepository.findAllById(workbookIds);
        if (workbookTags.size() != workbookIds.size()) {
            throw new BadRequestException(WorkbookTagExceptionCode.INVALID_REQUEST);
        }

        return workbookTags;
    }

    public List<WorkbookDto> getAllWorkbooks() {
        return workbookTagRepository.findAll()
                .stream()
                .map(WorkbookTag::toDto)
                .toList();
    }

}
