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
@Table(name = "cocomu_study_workbook_relation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Getter
public class WorkbookRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_workbook_relation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "study_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workbook_tag_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private WorkbookTag workbookTag;

    @Column(nullable = false)
    private boolean deleted;

    protected WorkbookRelation(final Study study, final WorkbookTag tag) {
        this.study = study;
        this.workbookTag = tag;
        this.deleted = false;
    }

    public static List<WorkbookRelation> createRelations(final Study study, final List<WorkbookTag> tags) {
        return tags.stream()
            .map(tag -> new WorkbookRelation(study, tag))
            .toList();
    }

    public boolean hasSameTag(final WorkbookTag other) {
        return other.equals(getWorkbookTag());
    }

    public void useTag() {
        deleted = false;
    }

    public void unUseTag() {
        deleted = true;
    }

}
