package co.kr.cocomu.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import co.kr.cocomu.admin.dto.request.CreateLanguageRequest;
import co.kr.cocomu.admin.dto.request.CreateWorkbookRequest;
import co.kr.cocomu.admin.exception.AdminExceptionCode;
import co.kr.cocomu.common.exception.domain.NotFoundException;
import co.kr.cocomu.study.domain.Language;
import co.kr.cocomu.study.domain.Workbook;
import co.kr.cocomu.study.dto.response.LanguageDto;
import co.kr.cocomu.study.dto.response.WorkbookDto;
import co.kr.cocomu.study.repository.jpa.LanguageRepository;
import co.kr.cocomu.study.repository.jpa.WorkbookRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private LanguageRepository languageRepository;
    @Mock private WorkbookRepository workbookRepository;

    @InjectMocks private AdminService adminService;

    @Test
    void 문제집이_추가가_된다() {
        // given
        CreateWorkbookRequest dto = new CreateWorkbookRequest("백준", "이미지URL");
        Workbook savedWorkbook = Workbook.of("백준", "이미지URL");
        ReflectionTestUtils.setField(savedWorkbook, "id", 1L);

        given(workbookRepository.save(any(Workbook.class))).willReturn(savedWorkbook);

        // when
        WorkbookDto result = adminService.addWorkbook(dto);

        // then
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void 언어가_추가_된다() {
        // given
        CreateLanguageRequest dto = new CreateLanguageRequest("자바", "이미지URL");
        Language savedLanguage = Language.of("자바", "이미지URL");
        ReflectionTestUtils.setField(savedLanguage, "id", 1L);

        given(languageRepository.save(any(Language.class))).willReturn(savedLanguage);

        // when
        LanguageDto result = adminService.addLanguage(dto);

        // then
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void 문제집이_삭제된다() {
        // given
        Workbook workBook = Workbook.of("백준", "이미지 URL");
        given(workbookRepository.findById(anyLong())).willReturn(Optional.of(workBook));

        // when
        adminService.deleteWorkbook(1L);

        // then
        verify(workbookRepository).delete(workBook);
    }

    @Test
    void 존재하지_않는_문제집_삭제시_예외가_발생한다() {
        // given
        given(workbookRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminService.deleteWorkbook(1L))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", AdminExceptionCode.NOT_FOUND_WORKBOOK);
    }

    @Test
    void 언어가_삭제된다() {
        // given
        Language language = Language.of("자바", "이미지URL");
        given(languageRepository.findById(anyLong())).willReturn(Optional.of(language));

        // when
        adminService.deleteLanguage(1L);

        // then
        verify(languageRepository).delete(language);
    }

    @Test
    void 존재하지_않는_언어_삭제시_예외가_발생한다() {
        // given
        given(languageRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminService.deleteLanguage(1L))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", AdminExceptionCode.NOT_FOUND_LANGUAGE);
    }

}