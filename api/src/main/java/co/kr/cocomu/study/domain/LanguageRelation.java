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

@Entity
@Table(name = "cocomu_study_language_relation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LanguageRelation implements TagRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_language_relation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Study study;

    @Column(nullable = false)
    private Long languageTagId;

    @Column(nullable = false)
    private boolean deleted;

    protected LanguageRelation(final Study study, final Long tagId) {
        this.study = study;
        this.languageTagId = tagId;
        this.deleted = false;
    }

    public static List<LanguageRelation> createRelations(final Study study, final List<Long> tagIds) {
        return tagIds.stream()
            .map(tagId -> new LanguageRelation(study, tagId))
            .toList();
    }

    public boolean hasSameTagId(final Long other) {
        return other.equals(languageTagId);
    }

    public void useTag() {
        deleted = false;
    }

    public void unUseTag() {
        deleted = true;
    }

}
