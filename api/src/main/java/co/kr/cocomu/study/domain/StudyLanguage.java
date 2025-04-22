package co.kr.cocomu.study.domain;

import co.kr.cocomu.language.domain.Language;
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
@Table(name = "cocomu_study_language")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_language_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "study_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Language language;

    @Column
    private boolean deleted;

    protected StudyLanguage(final Study study, final Language language) {
        this.study = study;
        this.language = language;
    }

    public static List<StudyLanguage> createStudyLanguages(final Study study, final List<Language> languages) {
        return languages.stream()
            .map(language -> new StudyLanguage(study, language))
            .toList();
    }

    public boolean hasSameLanguage(final Language other) {
        return other.equals(getLanguage());
    }

    public void useLanguage() {
        deleted = false;
    }

    public void unUseLanguage() {
        deleted = true;
    }

}
