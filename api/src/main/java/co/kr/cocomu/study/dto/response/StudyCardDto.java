package co.kr.cocomu.study.dto.response;

import co.kr.cocomu.study.domain.vo.StudyStatus;
import co.kr.cocomu.workbook.dto.WorkbookDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "스터디 쿼리 조회 결과")
public class StudyCardDto {

    @Schema(description = "스터디 ID", example = "1")
    private Long id;
    @Schema(description = "스터디 참여 가능 여부", example = "false")
    private Boolean joinable;
    @Schema(description = "스터디 명", example = "코코무 스터디")
    private String name;
    @Schema(description = "스터디 공개 여부", example = "PUBLIC")
    private StudyStatus status;
    @Schema(description = "스터디 언어 정보")
    private List<LanguageDto> languages;
    @Schema(description = "스터디 문제집 정보")
    private List<WorkbookDto> workbooks;
    @Schema(description = "스터디 설명", example = "열심히 하는 스터디")
    private String description;
    @Schema(description = "스터디 현재 인원 수", example = "1")
    private int currentUserCount;
    @Schema(description = "스터디 전체 인원 수", example = "100")
    private int totalUserCount;
    @Schema(description = "스터디 생성일자")
    private LocalDateTime createdAt;
    @Schema(description = "스터디장 정보")
    private LeaderDto leader;

}