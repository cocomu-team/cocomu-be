package co.kr.cocomu.study.domain;

import co.kr.cocomu.admin.dto.response.LanguageResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cocomu_language")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id")
    private Long id;
    private String name;
    private String imageUrl;

    private Language(final String name, final String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public static Language of(final String name, final String imageUrl) {
        return new Language(name, imageUrl);
    }

    public LanguageResponse toDto() {
        return new LanguageResponse(this.id, this.name, this.imageUrl);
    }

}
