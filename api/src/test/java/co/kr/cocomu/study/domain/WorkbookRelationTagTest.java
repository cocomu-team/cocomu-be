package co.kr.cocomu.study.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import co.kr.cocomu.tag.domain.WorkbookTag;
import org.junit.jupiter.api.Test;

class WorkbookRelationTagTest {

    @Test
    void 스터디_문제집_정보가_같다() {
        // given
        WorkbookRelation workbookRelation = new WorkbookRelation(mock(Study.class), 1L);

        // when
        boolean result = workbookRelation.hasSameTagId(1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 스터디_문제집_정보가_다르다() {
        // given
        WorkbookRelation workbookRelation = new WorkbookRelation(mock(Study.class), 2L);

        // when
        boolean result = workbookRelation.hasSameTagId(1L);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 스터디_문제집_태그를_사용하지_않는다() {
        // given
        WorkbookRelation workbookRelation = new WorkbookRelation(mock(Study.class), 1L);

        // when
        workbookRelation.unUseTag();

        // then
        assertThat(workbookRelation.isDeleted()).isTrue();
    }

    @Test
    void 스터디_문제집_태그를_재사용한다() {
        // given
        WorkbookRelation workbookRelation = new WorkbookRelation(mock(Study.class), 1L);
        workbookRelation.unUseTag();

        // when
        workbookRelation.useTag();

        // then
        assertThat(workbookRelation.isDeleted()).isFalse();
    }

}