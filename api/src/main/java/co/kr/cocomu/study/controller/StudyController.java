package co.kr.cocomu.study.controller;

import co.kr.cocomu.common.api.Api;
import co.kr.cocomu.common.api.NoContent;
import co.kr.cocomu.study.controller.code.StudyApiCode;
import co.kr.cocomu.study.controller.docs.StudyControllerDocs;
import co.kr.cocomu.study.dto.page.StudyDetailPageDto;
import co.kr.cocomu.study.dto.page.StudyPageDto;
import co.kr.cocomu.study.dto.request.CreatePrivateStudyDto;
import co.kr.cocomu.study.dto.request.CreatePublicStudyDto;
import co.kr.cocomu.study.dto.request.EditStudyDto;
import co.kr.cocomu.study.dto.request.GetAllStudyFilterDto;
import co.kr.cocomu.study.dto.request.JoinPrivateStudyDto;
import co.kr.cocomu.study.dto.request.StudyUserFilterDto;
import co.kr.cocomu.study.dto.response.AllStudyCardDto;
import co.kr.cocomu.study.dto.response.FilterOptionsDto;
import co.kr.cocomu.study.dto.response.LanguageDto;
import co.kr.cocomu.study.dto.response.StudyCardDto;
import co.kr.cocomu.study.dto.response.StudyMemberDto;
import co.kr.cocomu.study.service.command.StudyCommandService;
import co.kr.cocomu.study.service.query.StudyQueryService;
import co.kr.cocomu.tag.dto.WorkbookDto;
import co.kr.cocomu.tag.service.LanguageService;
import co.kr.cocomu.tag.service.WorkbookService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/studies")
@RequiredArgsConstructor
@Slf4j
public class StudyController implements StudyControllerDocs {

    private final StudyCommandService studyCommandService;
    private final StudyQueryService studyQueryService;
    private final WorkbookService workbookService;
    private final LanguageService languageService;

    @GetMapping("/filter-options")
    public Api<FilterOptionsDto> getFilterOptions(@AuthenticationPrincipal final Long userId) {
        final List<LanguageDto> allLanguages = languageService.getAllLanguages();
        final List<WorkbookDto> allWorkbooks = workbookService.getAllWorkbooks();
        final FilterOptionsDto result = new FilterOptionsDto(allWorkbooks, allLanguages);

        return Api.of(StudyApiCode.GET_FILTER_OPTIONS_SUCCESS, result);
    }

    @PostMapping("/public")
    public Api<Long> createPublicStudy(
        @AuthenticationPrincipal final Long userId,
        @Valid @RequestBody final CreatePublicStudyDto dto
    ) {
        final Long publicStudyId = studyCommandService.createPublicStudy(userId, dto);
        return Api.of(StudyApiCode.CREATE_STUDY_SUCCESS, publicStudyId);
    }

    @PostMapping("/public/{studyId}/join")
    public Api<Long> joinPublicStudy(
        @AuthenticationPrincipal final Long userId,
        @PathVariable final Long studyId
    ) {
        final Long publicStudyId = studyCommandService.joinPublicStudy(userId, studyId);
        return Api.of(StudyApiCode.JOIN_STUDY_SUCCESS, publicStudyId);
    }

    @GetMapping
    public Api<AllStudyCardDto> getAllStudyCard(
        @AuthenticationPrincipal final Long userId,
        @ModelAttribute final GetAllStudyFilterDto filter
    ) {
        final AllStudyCardDto studyPages = studyQueryService.getAllStudyCard(filter, userId);
        return Api.of(StudyApiCode.GET_ALL_STUDIES_SUCCESS, studyPages);
    }

    @GetMapping("/{studyId}/study-information")
    public Api<StudyCardDto> getStudyInfo(
        @AuthenticationPrincipal final Long userId,
        @PathVariable final Long studyId
    ) {
        final StudyCardDto result = studyQueryService.getStudyCard(studyId, userId);
        return Api.of(StudyApiCode.GET_STUDY_INFO_SUCCESS, result);
    }

    @GetMapping("/page")
    @Deprecated
    public Api<StudyPageDto> getStudiesPage(@AuthenticationPrincipal final Long userId) {
        final GetAllStudyFilterDto noFilter = GetAllStudyFilterDto.filterNothing();
        final List<LanguageDto> allLanguages = languageService.getAllLanguages();
        final List<WorkbookDto> allWorkbooks = workbookService.getAllWorkbooks();
        final AllStudyCardDto allStudyCard = studyQueryService.getAllStudyCard(noFilter, userId);
        final StudyPageDto result = new StudyPageDto(allWorkbooks, allLanguages, allStudyCard);

        return Api.of(StudyApiCode.GET_STUDY_PAGE_SUCCESS, result);
    }

    @GetMapping("/{studyId}")
    public Api<StudyDetailPageDto> getStudyDetailPage(
        @PathVariable final Long studyId,
        @AuthenticationPrincipal final Long userId
    ) {
        final StudyDetailPageDto result = studyQueryService.getStudyDetailPage(studyId, userId);
        return Api.of(StudyApiCode.GET_STUDY_DETAIL_SUCCESS, result);
    }

    @PostMapping("/private")
    public Api<Long> createPrivateStudy(
        @RequestBody @Valid final CreatePrivateStudyDto dto,
        @AuthenticationPrincipal final Long userId
    ) {
        final Long privateStudyId = studyCommandService.createPrivateStudy(dto, userId);
        return Api.of(StudyApiCode.CREATE_STUDY_SUCCESS, privateStudyId);
    }

    @PostMapping("/private/{studyId}/join")
    public Api<Long> createPrivateStudy(
        @PathVariable final Long studyId,
        @AuthenticationPrincipal final Long userId,
        @Valid @RequestBody final JoinPrivateStudyDto dto
    ) {
        final Long publicStudyId = studyCommandService.joinPrivateStudy(userId, studyId, dto.password());
        return Api.of(StudyApiCode.JOIN_STUDY_SUCCESS, publicStudyId);
    }

    @PostMapping("/{studyId}/leave")
    public NoContent leaveStudy(
        @PathVariable final Long studyId,
        @AuthenticationPrincipal final Long userId
    ) {
        studyCommandService.leaveStudy(userId, studyId);
        return NoContent.from(StudyApiCode.LEAVE_STUDY_SUCCESS);
    }

    @PostMapping("/{studyId}/remove")
    public NoContent removeStudy(
        @PathVariable final Long studyId,
        @AuthenticationPrincipal final Long userId
    ) {
        studyCommandService.removeStudy(userId, studyId);
        return NoContent.from(StudyApiCode.REMOVE_STUDY_SUCCESS);
    }

    @GetMapping("/{studyId}/members")
    public Api<List<StudyMemberDto>> getStudyMembers(
        @PathVariable final Long studyId,
        @AuthenticationPrincipal final Long userId,
        @ModelAttribute final StudyUserFilterDto dto
    ) {
        final List<StudyMemberDto> allMembers = studyQueryService.findAllMembers(userId, studyId, dto);
        return Api.of(StudyApiCode.GET_ALL_MEMBERS_SUCCESS, allMembers);
    }

    @PostMapping("/{studyId}/edit")
    public Api<Long> editStudy(
        @PathVariable final Long studyId,
        @AuthenticationPrincipal final Long userId,
        @RequestBody final EditStudyDto dto
    ) {
        if (dto.publicStudy()) {
            final Long editedStudyId = studyCommandService.editPublicStudy(studyId, userId, dto);
            return Api.of(StudyApiCode.EDIT_STUDY_SUCCESS, editedStudyId);
        }

        final Long editedStudyId = studyCommandService.editPrivateStudy(studyId, userId, dto);
        return Api.of(StudyApiCode.EDIT_STUDY_SUCCESS, editedStudyId);
    }

}
