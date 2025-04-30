package co.kr.cocomu.study.dto.response;

import co.kr.cocomu.study.domain.vo.MembershipRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "스터디 사용자 응답")
public class StudyMemberDto {

    @Schema(description = "스터디 사용자 식별자", example = "1")
    private Long studyUserId;
    @Schema(description = "스터디원 식별자", example = "1")
    private Long userId;
    @Schema(description = "스터디원 닉네임", example = "코코무")
    private String nickname;
    @Schema(description = "스터디원 프로필 이미지", example = "https://cdn.cocomu.co.kr/images/...")
    private String profileImageUrl;
    @Schema(description = "참여한 코딩 스페이스 수", example = "1")
    private Long joinedSpaceCount;
    @Schema(description = "스터디 권한")
    private MembershipRole role;
    @Schema(description = "스터디 가입 일자")
    private LocalDateTime joinedDate;

}
