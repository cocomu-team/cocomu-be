package co.kr.cocomu.study.service.business;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.common.exception.domain.NotFoundException;
import co.kr.cocomu.study.domain.Membership;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.LanguageRelation;
import co.kr.cocomu.study.dto.request.CreatePublicStudyDto;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.study.exception.LanguageRelationExceptionCode;
import co.kr.cocomu.study.repository.LanguageRelationRepository;
import co.kr.cocomu.study.repository.StudyRepository;
import co.kr.cocomu.study.repository.MembershipRepository;
import co.kr.cocomu.tag.domain.LanguageTag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyDomainService {

    private final StudyRepository studyRepository;
    private final MembershipRepository membershipRepository;
    private final LanguageRelationRepository languageRelationRepository;

    public Study getStudyWithThrow(final Long studyId) {
        return studyRepository.findById(studyId)
            .orElseThrow(() -> new NotFoundException(StudyExceptionCode.NOT_FOUND_STUDY));
    }

    public Membership getStudyUserWithThrow(final Long studyId, final Long userId) {
        return membershipRepository.findByUser_IdAndStudy_Id(userId, studyId)
            .orElseThrow(() -> new NotFoundException(StudyExceptionCode.NOT_FOUND_STUDY_USER));
    }

    public void validateStudyMembership(final Long userId, final Long studyId) {
        if (!membershipRepository.isUserJoinedStudy(userId, studyId)) {
            throw new BadRequestException(StudyExceptionCode.NO_PARTICIPATION_USER);
        }
    }

    public LanguageTag getLanguageTagInStudy(final Long studyId, final Long tagId) {
        return languageRelationRepository.findByStudy_idAndLanguageTag_IdAndDeletedIsFalse(studyId, tagId)
            .map(LanguageRelation::getLanguageTag)
            .orElseThrow(() -> new BadRequestException(LanguageRelationExceptionCode.INVALID_RELATION));
    }

}
