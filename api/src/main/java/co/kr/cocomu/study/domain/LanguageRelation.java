package co.kr.cocomu.study.domain;

import co.kr.cocomu.tag.domain.LanguageTag;
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

@Entity
@Table(name = "cocomu_study_language_relation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LanguageRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_language_relation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "study_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_tag_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private LanguageTag languageTag;

    @Column
    private boolean deleted;

    protected LanguageRelation(final Study study, final LanguageTag tag) {
        this.study = study;
        this.languageTag = tag;
    }

    public static List<LanguageRelation> createRelations(final Study study, final List<LanguageTag> tags) {
        return tags.stream()
            .map(tag -> new LanguageRelation(study, tag))
            .toList();
    }

    public boolean hasSameLTag(final LanguageTag other) {
        return other.equals(getLanguageTag());
    }

    public void useTag() {
        deleted = false;
    }

    public void unUseTag() {
        deleted = true;
    }

}
