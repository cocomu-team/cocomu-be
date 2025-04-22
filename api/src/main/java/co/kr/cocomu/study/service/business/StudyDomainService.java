package co.kr.cocomu.study.service.business;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.common.exception.domain.NotFoundException;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.StudyUser;
import co.kr.cocomu.study.dto.request.CreatePublicStudyDto;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.study.repository.jpa.StudyRepository;
import co.kr.cocomu.study.repository.jpa.StudyUserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyDomainService {

    private final StudyRepository studyRepository;
    private final StudyUserRepository studyUserRepository;

    public Study getStudyWithThrow(final Long studyId) {
        return studyRepository.findById(studyId)
            .orElseThrow(() -> new NotFoundException(StudyExceptionCode.NOT_FOUND_STUDY));
    }

    public StudyUser getStudyUserWithThrow(final Long studyId, final Long userId) {
        return studyUserRepository.findByUser_IdAndStudy_Id(userId, studyId)
            .orElseThrow(() -> new NotFoundException(StudyExceptionCode.NOT_FOUND_STUDY_USER));
    }

    public void validateStudyMembership(final Long userId, final Long studyId) {
        if (!studyUserRepository.isUserJoinedStudy(userId, studyId)) {
            throw new BadRequestException(StudyExceptionCode.NO_PARTICIPATION_USER);
        }
    }

    public void validateStudyCreation(final CreatePublicStudyDto dto) {
        validateEmptyWorkbooks(dto.workbooks());
        validateEmptyLanguages(dto.languages());
    }

    private static void validateEmptyWorkbooks(final List<Long> workbookIds) {
        if (workbookIds.isEmpty()) {
            throw new BadRequestException(StudyExceptionCode.WORKBOOK_IS_REQUIRED_VALUE);
        }
    }

    private void validateEmptyLanguages(final List<Long> languageIds) {
        if (languageIds.isEmpty()) {
            throw new BadRequestException(StudyExceptionCode.LANGUAGE_IS_REQUIRED_VALUE);
        }
    }

}
