package co.kr.cocomu.study.service;

import co.kr.cocomu.codingspace.service.CodingSpaceQueryService;
import co.kr.cocomu.common.exception.domain.NotFoundException;
import co.kr.cocomu.study.domain.Language;
import co.kr.cocomu.study.domain.Study;
import co.kr.cocomu.study.domain.Workbook;
import co.kr.cocomu.study.dto.request.GetAllStudyFilterDto;
import co.kr.cocomu.study.dto.request.StudyUserFilterDto;
import co.kr.cocomu.study.dto.response.AllStudyCardDto;
import co.kr.cocomu.study.dto.response.LanguageDto;
import co.kr.cocomu.study.dto.response.LeaderDto;
import co.kr.cocomu.study.dto.response.StudyCardDto;
import co.kr.cocomu.study.dto.page.StudyDetailPageDto;
import co.kr.cocomu.study.dto.response.StudyMemberDto;
import co.kr.cocomu.study.dto.response.WorkbookDto;
import co.kr.cocomu.study.exception.StudyExceptionCode;
import co.kr.cocomu.study.repository.jpa.LanguageRepository;
import co.kr.cocomu.study.repository.jpa.StudyRepository;
import co.kr.cocomu.study.repository.jpa.StudyUserRepository;
import co.kr.cocomu.study.repository.jpa.WorkbookRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyQueryService {

    private final StudyDomainService studyDomainService;
    private final CodingSpaceQueryService codingSpaceQueryService;
    private final StudyRepository studyQuery;
    private final StudyUserRepository studyUserQuery;
    private final WorkbookRepository workbookQuery;
    private final LanguageRepository languageQuery;

    public AllStudyCardDto getAllStudyCard(final GetAllStudyFilterDto dto, final Long userId) {
        final Long totalStudyCount = studyQuery.countStudyCardsWithFilter(dto, userId);
        final List<StudyCardDto> studyPages = studyQuery.findTop12StudyCardsWithFilter(dto, userId);
        final List<Long> studyIds = studyPages.stream().map(StudyCardDto::getId).toList();
        setStudyInformation(studyIds, studyPages);

        return new AllStudyCardDto(totalStudyCount, studyPages);
    }

    public StudyCardDto getStudyCard(final Long studyId, final Long userId) {
        return studyQuery.findStudyPagesByStudyId(studyId, userId)
            .map(studyPage -> {
                studyPage.setLanguages(languageQuery.findLanguageByStudyId(studyId));
                studyPage.setWorkbooks(workbookQuery.findWorkbookByStudyId(studyId));
                studyPage.setLeader(studyUserQuery.findLeaderByStudyId(studyId));
                return studyPage;
            })
            .orElseThrow(() -> new NotFoundException(StudyExceptionCode.NOT_FOUND_STUDY));
    }

    public List<WorkbookDto> getAllWorkbooks() {
        final List<Workbook> workbooks = workbookQuery.findAll();
        return workbooks.stream()
            .map(Workbook::toDto)
            .toList();
    }

    public List<LanguageDto> getAllLanguages() {
        final List<Language> languages = languageQuery.findAll();
        return languages.stream()
            .map(Language::toDto)
            .toList();
    }

    public StudyDetailPageDto getStudyDetailPage(final Long studyId, final Long userId) {
        final Study study = studyDomainService.getStudyWithThrow(studyId);
        studyDomainService.validateStudyMembership(userId, study.getId());
        return StudyDetailPageDto.from(study);
    }

    public List<StudyMemberDto> findAllMembers(final Long userId, final Long studyId, final StudyUserFilterDto dto) {
        studyDomainService.validateStudyMembership(userId, studyId);
        final List<StudyMemberDto> members = studyUserQuery.findMembers(studyId, dto);

        final List<Long> memberIds = members.stream().map(StudyMemberDto::getUserId).toList();
        final Map<Long, Long> spaceCounts = codingSpaceQueryService.countJoinedSpacesByMembers(studyId, memberIds);

        for (final StudyMemberDto member : members) {
            member.setJoinedSpaceCount(spaceCounts.getOrDefault(member.getUserId(), 0L));
        }

        return members;
    }

    public List<StudyCardDto> getStudyCardsByUser(final Long userId, final Long viewerId, final Long lastIndex) {
        final List<StudyCardDto> studyCards = studyQuery.findTop20UserStudyCards(userId, viewerId, lastIndex);
        final List<Long> studyIds = studyCards.stream().map(StudyCardDto::getId).toList();
        setStudyInformation(studyIds, studyCards);

        return studyCards;
    }

    private void setStudyInformation(final List<Long> studyIds, final List<StudyCardDto> studyPages) {
        final Map<Long, List<LanguageDto>> languageByStudies = languageQuery.findLanguageByStudies(studyIds);
        final Map<Long, List<WorkbookDto>> workbookByStudies = workbookQuery.findWorkbookByStudies(studyIds);
        final Map<Long, LeaderDto> LeaderByStudies = studyUserQuery.findLeaderByStudies(studyIds);

        for (StudyCardDto studyPage: studyPages) {
            studyPage.setLanguages(languageByStudies.getOrDefault(studyPage.getId(), List.of()));
            studyPage.setWorkbooks(workbookByStudies.getOrDefault(studyPage.getId(), List.of()));
            studyPage.setLeader(LeaderByStudies.get(studyPage.getId()));
        }
    }

}
