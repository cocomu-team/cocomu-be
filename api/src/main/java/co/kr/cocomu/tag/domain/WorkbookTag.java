package co.kr.cocomu.tag.domain;


import co.kr.cocomu.tag.dto.WorkbookDto;
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
@Table(name = "cocomu_workbook_tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Getter
public class WorkbookTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workbook_tag_id")
    private Long id;
    private String name;
    private String imageUrl;

    protected WorkbookTag(final String name, final String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public static WorkbookTag of(final String name, final String imageUrl) {
        return new WorkbookTag(name, imageUrl);
    }

    public WorkbookDto toDto() {
        return new WorkbookDto(this.id, this.name, this.imageUrl);
    }

}
