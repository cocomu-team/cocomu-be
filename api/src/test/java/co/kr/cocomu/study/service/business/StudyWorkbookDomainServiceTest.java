package co.kr.cocomu.study.service.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.study.domain.StudyWorkbook;
import co.kr.cocomu.workbook.domain.Workbook;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyWorkbookDomainServiceTest {

    @InjectMocks StudyWorkbookDomainService studyWorkbookDomainService;

    @Test
    void 추가_문제집_중_기존_문제집에_포함되지_않으면_추출한다() {
        // given
        Workbook existingWorkbook = mock(Workbook.class);
        Workbook newWorkbook = mock(Workbook.class);

        StudyWorkbook existingRelation = mock(StudyWorkbook.class);
        when(existingRelation.hasSameWorkbook(existingWorkbook)).thenReturn(true);
        when(existingRelation.hasSameWorkbook(newWorkbook)).thenReturn(false);

        List<Workbook> workbooks = List.of(existingWorkbook, newWorkbook);
        List<StudyWorkbook> existing = List.of(existingRelation);

        // when
        List<Workbook> result = studyWorkbookDomainService.extractNewWorkbooks(workbooks, existing);

        // then
        assertThat(result).hasSize(1);
        assertThat(result).contains(newWorkbook);
    }

    @Test
    void 추가_문제집_정보가_기존_스터디_문제집에서_사용을_중지했다면_재사용한다() {
        // given
        Workbook existingWorkbook = mock(Workbook.class);

        StudyWorkbook existingRelation = mock(StudyWorkbook.class);
        when(existingRelation.hasSameWorkbook(existingWorkbook)).thenReturn(true);

        List<Workbook> workbooks = List.of(existingWorkbook);
        List<StudyWorkbook> existing = List.of(existingRelation);

        // when
        studyWorkbookDomainService.activateSelectedWorkbooks(existing, workbooks);

        // then
        verify(existingRelation).useWorkbook();
    }


    // workbooks 에 포함되지 않은 studyWorkbook 이 있다면, 미사용 상태로 업데이트
    @Test
    void 추가_문제집_정보에_포함되지_않는_기존_스터디_문제집은_사용을_중지한다() {
        // given
        Workbook existingWorkbook = mock(Workbook.class);
        StudyWorkbook existingRelation = mock(StudyWorkbook.class);
        when(existingRelation.hasSameWorkbook(existingWorkbook)).thenReturn(false);

        List<Workbook> workbooks = List.of(existingWorkbook);
        List<StudyWorkbook> existing = List.of(existingRelation);

        // when
        studyWorkbookDomainService.deactivateUnselectedWorkbooks(existing, workbooks);

        // then
        verify(existingRelation).unUseWorkbook();
    }

}