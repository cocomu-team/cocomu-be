package co.kr.cocomu.study.service;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.dto.request.EditStudyDto;
import co.kr.cocomu.study.dto.request.CreateStudyDto;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.study.repository.StudyRepository;
import co.kr.cocomu.study.service.business.PasswordBusiness;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StudyService {

    private final StudyFactory studyFactory;
    private final StudyDomainService studyDomainService;
    private final PasswordBusiness passwordBusiness;
    private final WorkbookRelationService workbookRelationService;
    private final LanguageRelationService languageRelationService;
    private final MembershipService membershipService;
    private final StudyRepository studyRepository;

    public Long create(final Long leaderId, final CreateStudyDto dto) {
        final Study study = studyFactory.generateStudy(dto, leaderId);

        if (dto.publicStudy()) {
            study.updatePublicStatus();
        } else {
            final String encodedPassword = passwordBusiness.encodePassword(dto.password());
            study.updatePrivateStatus(encodedPassword);
        }

        workbookRelationService.addWorkbooksToStudy(study, dto.workbookTagIds());
        languageRelationService.addRelationToStudy(study, dto.languageTagIds());
        membershipService.joinLeader(study, leaderId);
        studyRepository.save(study);

        return study.getId();
    }

    public Long join(final Long userId, final Long studyId, final String password) {
        final Study study = studyDomainService.getStudyWithThrow(studyId);
        if (!study.isPublic()) {
            passwordBusiness.validatePassword(password, study.getPassword());
        }
        membershipService.joinMember(study, userId);

        return study.getId();
    }

    public void leaveMember(final Long userId, final Long studyId) {
        final Study study = studyDomainService.getStudyWithThrow(studyId);
        if (study.isLeader(userId)) {
            throw new BadRequestException(StudyExceptionCode.LEADER_CAN_NOT_LEAVE);
        }
        membershipService.leave(study, userId);
    }

    public void removeStudy(final Long userId, final Long studyId) {
        final Study study = studyDomainService.getStudyWithThrow(studyId);
        if (!study.isLeader(userId)) {
            throw new BadRequestException(StudyExceptionCode.MEMBER_CAN_NOT_REMOVE);
        }
        membershipService.leave(study, userId);
        study.remove();
    }

    public Long editStudy(final Long studyId, final Long userId, final EditStudyDto dto) {
        final Study study = studyDomainService.getStudyWithThrow(studyId);
        if (!study.isLeader(userId)) {
            throw new BadRequestException(StudyExceptionCode.MEMBER_CAN_NOT_EDIT);
        }

        study.updateBasicInfo(dto.name(), dto.description(), dto.totalUserCount());
        if (dto.publicStudy()) {
            study.updatePublicStatus();
        } else {
            final String encodedPassword = passwordBusiness.encodePassword(dto.password());
            study.updatePrivateStatus(encodedPassword);
        }
        workbookRelationService.changeWorkbooksToStudy(study, dto.workbooks());
        languageRelationService.changeRelationToStudy(study, dto.languages());

        return study.getId();
    }

}
