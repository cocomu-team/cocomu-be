package co.kr.cocomu.tag.domain;

import co.kr.cocomu.study.dto.response.LanguageDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cocomu_language_tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Getter
public class LanguageTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_tag_id")
    private Long id;
    private String name;
    private String imageUrl;

    protected LanguageTag(final String name, final String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public static LanguageTag of(final String name, final String imageUrl) {
        return new LanguageTag(name, imageUrl);
    }

    public LanguageDto toDto() {
        return new LanguageDto(this.id, this.name, this.imageUrl);
    }

}
