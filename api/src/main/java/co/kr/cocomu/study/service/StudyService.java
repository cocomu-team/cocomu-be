package co.kr.cocomu.study.service;

import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.common.exception.domain.NotFoundException;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.vo.StudyStatus;
import co.kr.cocomu.study.dto.request.CreateStudyDto;
import co.kr.cocomu.study.dto.request.EditStudyDto;
import co.kr.cocomu.study.dto.request.PasswordDto;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.study.repository.StudyRepository;
import co.kr.cocomu.study.strategy.StudyStrategy;
import co.kr.cocomu.study.factory.StudyFactory;
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
    private final RelationService relationService;
    private final MembershipService membershipService;
    private final StudyRepository studyRepository;

    @Transactional(readOnly = true)
    public Study getStudyWithThrow(final Long studyId) {
        return studyRepository.findByIdAndStatusNot(studyId, StudyStatus.REMOVE)
            .orElseThrow(() -> new NotFoundException(StudyExceptionCode.NOT_FOUND_STUDY));
    }

    public Long create(final Long leaderId, final CreateStudyDto dto) {
        final Study study = studyFactory.generateStudy(dto, leaderId);
        final StudyStrategy studyStrategy = studyFactory.resolveStudyStrategy(dto.publicStudy());
        studyStrategy.updateStatus(study, dto.password());

        relationService.addTags(study, dto.workbookTagIds(), dto.languageTagIds());
        membershipService.joinLeader(study, leaderId);
        
        studyRepository.save(study);
        return study.getId();
    }

    public Long join(final Long userId, final Long studyId, final PasswordDto dto) {
        final Study study = getStudyWithThrow(studyId);
        final StudyStrategy studyStrategy = studyFactory.resolveStudyStrategy(study.isPublic());
        if (!studyStrategy.matchPassword(dto.password(), study.getPassword())) {
            throw new BadRequestException(StudyExceptionCode.STUDY_PASSWORD_WRONG);
        }

        membershipService.joinMember(study, userId);
        return study.getId();
    }

    public void leaveMember(final Long userId, final Long studyId) {
        final Study study = getStudyWithThrow(studyId);
        if (study.isLeader(userId)) {
            throw new BadRequestException(StudyExceptionCode.LEADER_CAN_NOT_LEAVE);
        }

        membershipService.leave(study, userId);
    }

    public void removeStudy(final Long userId, final Long studyId) {
        final Study study = getStudyWithThrow(studyId);
        assertUserIsLeader(study, userId);

        membershipService.leave(study, userId);
        study.remove();
    }

    public Long editStudy(final Long studyId, final Long userId, final EditStudyDto dto) {
        final Study study = getStudyWithThrow(studyId);
        assertUserIsLeader(study, userId);

        final StudyStrategy studyStrategy = studyFactory.resolveStudyStrategy(dto.publicStudy());
        study.updateBasicInfo(dto.name(), dto.description(), dto.totalUserCount());
        studyStrategy.updateStatus(study, dto.password());
        relationService.changeTags(study, dto.workbooks(), dto.languages());

        return study.getId();
    }

    private void assertUserIsLeader(final Study study, final Long userId) {
        if (!study.isLeader(userId)) {
            throw new BadRequestException(StudyExceptionCode.REQUIRES_LEADER_PERMISSION);
        }
    }

}
