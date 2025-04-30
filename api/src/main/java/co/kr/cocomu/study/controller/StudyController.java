package co.kr.cocomu.study.controller;

import co.kr.cocomu.common.api.Api;
import co.kr.cocomu.common.api.NoContent;
import co.kr.cocomu.study.controller.code.StudyApiCode;
import co.kr.cocomu.study.controller.docs.StudyControllerDocs;
import co.kr.cocomu.study.dto.page.StudyDetailPageDto;
import co.kr.cocomu.study.dto.request.CreateStudyDto;
import co.kr.cocomu.study.dto.request.EditStudyDto;
import co.kr.cocomu.study.dto.request.GetAllStudyFilterDto;
import co.kr.cocomu.study.dto.request.PasswordDto;
import co.kr.cocomu.study.dto.request.StudyUserFilterDto;
import co.kr.cocomu.study.dto.response.AllStudyCardDto;
import co.kr.cocomu.study.dto.response.FilterOptionsDto;
import co.kr.cocomu.study.dto.response.LanguageDto;
import co.kr.cocomu.study.dto.response.StudyCardDto;
import co.kr.cocomu.study.dto.response.StudyMemberDto;
import co.kr.cocomu.study.service.StudyService;
import co.kr.cocomu.study.service.StudyQueryService;
import co.kr.cocomu.tag.dto.WorkbookDto;
import co.kr.cocomu.tag.service.LanguageTagService;
import co.kr.cocomu.tag.service.WorkbookTagService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/studies")
@RequiredArgsConstructor
@Slf4j
public class StudyController implements StudyControllerDocs {

    private final StudyService studyService;
    private final StudyQueryService studyQueryService;
    private final WorkbookTagService workbookTagService;
    private final LanguageTagService languageTagService;

    @PostMapping
    public Api<Long> create(
        @AuthenticationPrincipal final Long userId,
        @Valid @RequestBody final CreateStudyDto dto
    ) {
        final Long result = studyService.create(userId, dto);
        return Api.of(StudyApiCode.CREATE_STUDY_SUCCESS, result);
    }

    @PostMapping("/{studyId}")
    public Api<Long> join(
        @AuthenticationPrincipal final Long userId,
        @PathVariable final Long studyId,
        @RequestBody final PasswordDto dto
    ) {
        final Long publicStudyId = studyService.join(userId, studyId, dto);
        return Api.of(StudyApiCode.JOIN_STUDY_SUCCESS, publicStudyId);
    }

    @PatchMapping("/{studyId}")
    public NoContent leave(
        @PathVariable final Long studyId,
        @AuthenticationPrincipal final Long userId
    ) {
        studyService.leaveMember(userId, studyId);
        return NoContent.from(StudyApiCode.LEAVE_STUDY_SUCCESS);
    }

    @PutMapping("/{studyId}")
    public Api<Long> edit(
        @PathVariable final Long studyId,
        @AuthenticationPrincipal final Long userId,
        @RequestBody final EditStudyDto dto
    ) {
        final Long editedStudyId = studyService.editStudy(studyId, userId, dto);
        return Api.of(StudyApiCode.EDIT_STUDY_SUCCESS, editedStudyId);
    }

    @DeleteMapping("/{studyId}")
    public NoContent remove(
        @PathVariable final Long studyId,
        @AuthenticationPrincipal final Long userId
    ) {
        studyService.removeStudy(userId, studyId);
        return NoContent.from(StudyApiCode.REMOVE_STUDY_SUCCESS);
    }

    @GetMapping("/filter-options")
    public Api<FilterOptionsDto> getFilterOptions(@AuthenticationPrincipal final Long userId) {
        final List<LanguageDto> allLanguages = languageTagService.getAllTags();
        final List<WorkbookDto> allWorkbooks = workbookTagService.getAllWorkbooks();
        final FilterOptionsDto result = new FilterOptionsDto(allWorkbooks, allLanguages);

        return Api.of(StudyApiCode.GET_FILTER_OPTIONS_SUCCESS, result);
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

    @GetMapping("/{studyId}")
    public Api<StudyDetailPageDto> getStudyDetailPage(
        @PathVariable final Long studyId,
        @AuthenticationPrincipal final Long userId
    ) {
        final StudyDetailPageDto result = studyQueryService.getStudyDetailPage(studyId, userId);
        return Api.of(StudyApiCode.GET_STUDY_DETAIL_SUCCESS, result);
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

}
