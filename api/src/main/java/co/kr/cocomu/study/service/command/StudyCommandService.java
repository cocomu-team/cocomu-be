package co.kr.cocomu.study.service.command;

import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.Membership;
import co.kr.cocomu.study.dto.request.CreatePrivateStudyDto;
import co.kr.cocomu.study.dto.request.CreatePublicStudyDto;
import co.kr.cocomu.study.dto.request.EditStudyDto;
import co.kr.cocomu.study.repository.StudyRepository;
import co.kr.cocomu.study.service.LanguageRelationService;
import co.kr.cocomu.study.service.MembershipService;
import co.kr.cocomu.study.service.PasswordService;
import co.kr.cocomu.study.service.WorkbookRelationService;
import co.kr.cocomu.study.service.business.StudyDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StudyCommandService {

    private final StudyDomainService studyDomainService;
    private final PasswordService passwordService;
    private final WorkbookRelationService workbookRelationService;
    private final LanguageRelationService languageRelationService;
    private final StudyRepository studyRepository;
    private final MembershipService membershipService;

    public Long createPublicStudy(final Long userId, final CreatePublicStudyDto dto) {
        final Study study = Study.createPublicStudy(dto);
        workbookRelationService.addWorkbooksToStudy(study, dto.workbooks());
        languageRelationService.addRelationToStudy(study, dto.languages());
        membershipService.joinLeader(study, userId);

        final Study savedStudy = studyRepository.save(study);
        return savedStudy.getId();
    }

    public Long joinPublicStudy(final Long userId, final Long studyId) {
        final Study study = studyDomainService.getStudyWithThrow(studyId);
        membershipService.joinMember(study, userId);

        return study.getId();
    }

    public Long createPrivateStudy(final CreatePrivateStudyDto dto, final Long userId) {
        final String encodedPassword = passwordService.encodeStudyPassword(dto.password());
        final Study study = Study.createPrivateStudy(dto, encodedPassword);
        workbookRelationService.addWorkbooksToStudy(study, dto.workbooks());
        languageRelationService.addRelationToStudy(study, dto.languages());
        membershipService.joinLeader(study, userId);

        final Study savedStudy = studyRepository.save(study);
        return savedStudy.getId();
    }

    public Long joinPrivateStudy(final Long userId, final Long studyId, final String password) {
        final Study study = studyDomainService.getStudyWithThrow(studyId);
        passwordService.validatePassword(password, study.getPassword());
        membershipService.joinMember(study, userId);

        return study.getId();
    }

    public void leaveStudy(final Long userId, final Long studyId) {
        final Membership membership = studyDomainService.getStudyUserWithThrow(studyId, userId);
        membership.leaveStudy();
    }

    public void removeStudy(final Long userId, final Long studyId) {
        final Membership membership = studyDomainService.getStudyUserWithThrow(studyId, userId);
        membership.removeStudy();
    }

    public Long editPublicStudy(final Long studyId, final Long userId, final EditStudyDto dto) {
        final Membership membership = studyDomainService.getStudyUserWithThrow(studyId, userId);
        membership.editPublicStudy(dto);

        workbookRelationService.changeWorkbooksToStudy(membership.getStudy(), dto.workbooks());
        languageRelationService.changeRelationToStudy(membership.getStudy(), dto.languages());

        return membership.getStudyId();
    }

    public Long editPrivateStudy(final Long studyId, final Long userId, final EditStudyDto dto) {
        final Membership membership = studyDomainService.getStudyUserWithThrow(studyId, userId);
        final String encodedPassword = passwordService.encodeStudyPassword(dto.password());
        membership.editPrivateStudy(dto, encodedPassword);

        workbookRelationService.changeWorkbooksToStudy(membership.getStudy(), dto.workbooks());
        languageRelationService.changeRelationToStudy(membership.getStudy(), dto.languages());

        return membership.getStudyId();
    }

}
