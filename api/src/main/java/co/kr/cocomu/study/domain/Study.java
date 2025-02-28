package co.kr.cocomu.study.domain;

import co.kr.cocomu.common.repository.TimeBaseEntity;
import co.kr.cocomu.study.domain.vo.StudyStatus;
import co.kr.cocomu.study.dto.request.CreatePublicStudyDto;
import co.kr.cocomu.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cocomu_study")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Study extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private User leader;

    @Column(length = 50, nullable = false)
    private String name;
    @Column(length = 6)
    private String password;
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(20)")
    private StudyStatus status;

    @Column(nullable = false)
    private int currentUserCount;
    @Column(nullable = false)
    private int totalUserCount;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyJudge> judges = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyLanguage> languages = new ArrayList<>();

    private Study(
        final User leader,
        final String name,
        final String password,
        final String description,
        final StudyStatus status,
        final int totalUserCount
    ) {
        this.leader = leader;
        this.name = name;
        this.password = password;
        this.description = description;
        this.status = status;
        this.currentUserCount = 1;
        this.totalUserCount = totalUserCount;
    }

    public static Study createPublicStudy(final User leader, final CreatePublicStudyDto dto) {
        return new Study(leader, dto.name(), null, dto.description(), StudyStatus.PUBLIC, dto.totalUserCount());
    }

    public void increaseCurrentUserCount() {
        this.currentUserCount++;
    }

    public void addJudges(final List<Judge> judges) {
        for (Judge judge : judges) {
            addJudge(judge);
        }
    }

    public void addJudge(final Judge judge) {
        final StudyJudge studyJudge = StudyJudge.of(this, judge);
        this.judges.add(studyJudge);
    }

    public void addLanguages(final List<Language> languages) {
        for (Language language : languages) {
            addLanguage(language);
        }
    }

    public void addLanguage(final Language language) {
        final StudyLanguage studyLanguage = StudyLanguage.of(this, language);
        this.languages.add(studyLanguage);
    }

}
