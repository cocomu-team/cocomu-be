package co.kr.cocomu.study.domain;

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
public class WorkbookRelation implements TagRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_workbook_relation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Study study;

    @Column(nullable = false)
    private Long workbookTagId;

    @Column(nullable = false)
    private boolean deleted;

    protected WorkbookRelation(final Study study, final Long tagId) {
        this.study = study;
        this.workbookTagId = tagId;
        this.deleted = false;
    }

    public static List<WorkbookRelation> createRelations(final Study study, final List<Long> tagIds) {
        return tagIds.stream()
            .map(tagId -> new WorkbookRelation(study, tagId))
            .toList();
    }

    public boolean hasSameTagId(final Long tagId) {
        return tagId.equals(workbookTagId);
    }

    public void useTag() {
        deleted = false;
    }

    public void unUseTag() {
        deleted = true;
    }

}
