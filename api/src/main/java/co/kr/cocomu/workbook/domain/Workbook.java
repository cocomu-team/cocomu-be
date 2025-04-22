package co.kr.cocomu.workbook.domain;


import co.kr.cocomu.workbook.dto.WorkbookDto;
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
@Table(name = "cocomu_workbook")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Getter
public class Workbook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workbook_id")
    private Long id;
    private String name;
    private String imageUrl;

    protected Workbook(final String name, final String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public static Workbook of(final String name, final String imageUrl) {
        return new Workbook(name, imageUrl);
    }

    public WorkbookDto toDto() {
        return new WorkbookDto(this.id, this.name, this.imageUrl);
    }

}
