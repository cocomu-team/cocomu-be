package co.kr.cocomu.study.service.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.cocomu.study.domain.WorkbookRelation;
import co.kr.cocomu.tag.domain.WorkbookTag;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkbookRelationTagDomainServiceTest {

    @InjectMocks
    WorkbookRelationBusiness workbookRelationBusiness;

    @Test
    void 추가_문제집_중_기존_문제집에_포함되지_않으면_추출한다() {
        // given
        WorkbookTag existingWorkbookTag = mock(WorkbookTag.class);
        WorkbookTag newWorkbookTag = mock(WorkbookTag.class);

        WorkbookRelation existingRelation = mock(WorkbookRelation.class);
        when(existingRelation.hasSameTag(existingWorkbookTag)).thenReturn(true);
        when(existingRelation.hasSameTag(newWorkbookTag)).thenReturn(false);

        List<WorkbookTag> workbookTags = List.of(existingWorkbookTag, newWorkbookTag);
        List<WorkbookRelation> existing = List.of(existingRelation);

        // when
        List<WorkbookTag> result = workbookRelationBusiness.extractNewTags(workbookTags, existing);

        // then
        assertThat(result).hasSize(1);
        assertThat(result).contains(newWorkbookTag);
    }

    @Test
    void 추가_문제집_정보가_기존_스터디_문제집에서_사용을_중지했다면_재사용한다() {
        // given
        WorkbookTag existingWorkbookTag = mock(WorkbookTag.class);

        WorkbookRelation existingRelation = mock(WorkbookRelation.class);
        when(existingRelation.hasSameTag(existingWorkbookTag)).thenReturn(true);

        List<WorkbookTag> workbookTags = List.of(existingWorkbookTag);
        List<WorkbookRelation> existing = List.of(existingRelation);

        // when
        workbookRelationBusiness.activateSelectedRelations(existing, workbookTags);

        // then
        verify(existingRelation).useTag();
    }


    // workbooks 에 포함되지 않은 studyWorkbook 이 있다면, 미사용 상태로 업데이트
    @Test
    void 추가_문제집_정보에_포함되지_않는_기존_스터디_문제집은_사용을_중지한다() {
        // given
        WorkbookTag existingWorkbookTag = mock(WorkbookTag.class);
        WorkbookRelation existingRelation = mock(WorkbookRelation.class);
        when(existingRelation.hasSameTag(existingWorkbookTag)).thenReturn(false);

        List<WorkbookTag> workbookTags = List.of(existingWorkbookTag);
        List<WorkbookRelation> existing = List.of(existingRelation);

        // when
        workbookRelationBusiness.deactivateUnselectedRelations(existing, workbookTags);

        // then
        verify(existingRelation).unUseTag();
    }

}