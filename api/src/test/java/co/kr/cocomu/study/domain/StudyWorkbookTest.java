package co.kr.cocomu.study.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import co.kr.cocomu.workbook.domain.Workbook;
import org.junit.jupiter.api.Test;

class StudyWorkbookTest {

    @Test
    void 스터디_문제집_정보가_같다() {
        // given
        Workbook mockWorkbook = mock(Workbook.class);
        StudyWorkbook studyWorkbook = new StudyWorkbook(mock(Study.class), mockWorkbook);

        // when
        boolean result = studyWorkbook.hasSameWorkbook(mockWorkbook);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 스터디_문제집_정보가_다르다() {
        // given
        Workbook mockWorkbook = mock(Workbook.class);
        StudyWorkbook studyWorkbook = new StudyWorkbook(mock(Study.class), mock(Workbook.class));

        // when
        boolean result = studyWorkbook.hasSameWorkbook(mockWorkbook);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 스터디_문제집_태그를_사용하지_않는다() {
        // given
        StudyWorkbook studyWorkbook = new StudyWorkbook(mock(Study.class), mock(Workbook.class));

        // when
        studyWorkbook.unUseWorkbook();

        // then
        assertThat(studyWorkbook.isDeleted()).isTrue();
    }

    @Test
    void 스터디_문제집_태그를_재사용한다() {
        // given
        StudyWorkbook studyWorkbook = new StudyWorkbook(mock(Study.class), mock(Workbook.class));
        studyWorkbook.unUseWorkbook();

        // when
        studyWorkbook.useWorkbook();

        // then
        assertThat(studyWorkbook.isDeleted()).isFalse();
    }

}