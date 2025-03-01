package co.kr.cocomu.study.domain;


import co.kr.cocomu.admin.dto.response.WorkbookResponse;
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
@Table(name = "cocomu_workbook")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Workbook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workbook_id")
    private Long id;
    private String name;
    private String imageUrl;

    private Workbook(final String name, final String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public static Workbook of(final String name, final String imageUrl) {
        return new Workbook(name, imageUrl);
    }

    public WorkbookResponse toDto() {
        return new WorkbookResponse(this.id, this.name, this.imageUrl);
    }

}
