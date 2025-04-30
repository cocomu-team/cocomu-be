package co.kr.cocomu.tag.service;

import co.kr.cocomu.tag.domain.WorkbookTag;
import co.kr.cocomu.tag.dto.WorkbookDto;
import co.kr.cocomu.tag.repository.WorkbookTagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkbookTagService {

    private final WorkbookTagRepository workbookTagRepository;

    public boolean existsAllTagIds(final List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return false;
        }

        final int count = workbookTagRepository.countByIdIn(tagIds);
        return count == tagIds.size();
    }

    public List<WorkbookDto> getAllWorkbooks() {
        return workbookTagRepository.findAll()
                .stream()
                .map(WorkbookTag::toDto)
                .toList();
    }

}
