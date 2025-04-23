package co.kr.cocomu.study.domain;

import co.kr.cocomu.tag.domain.WorkbookTag;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "cocomu_study_workbook")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Getter
public class StudyWorkbook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_workbook_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "study_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workbook_tag_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private WorkbookTag workbookTag;

    @Column(nullable = false)
    private boolean deleted;

    protected StudyWorkbook(final Study study, final WorkbookTag workbookTag) {
        this.study = study;
        this.workbookTag = workbookTag;
        this.deleted = false;
    }

    public static List<StudyWorkbook> createStudyWorkbooks(final Study study, final List<WorkbookTag> workbookTags) {
        return workbookTags.stream()
            .map(workbook -> new StudyWorkbook(study, workbook))
            .toList();
    }

    public boolean hasSameWorkbook(final WorkbookTag workbookTag) {
        return workbookTag.equals(getWorkbookTag());
    }

    public void useWorkbook() {
        deleted = false;
    }

    public void unUseWorkbook() {
        deleted = true;
    }

}
