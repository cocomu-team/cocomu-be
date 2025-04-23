package co.kr.cocomu.study.repository.query;

import co.kr.cocomu.study.dto.request.StudyUserFilterDto;
import co.kr.cocomu.study.dto.response.LeaderDto;
import co.kr.cocomu.study.dto.response.StudyMemberDto;
import java.util.List;
import java.util.Map;

public interface MembershipQuery {

    Map<Long, LeaderDto> findLeaderByStudies(List<Long> studyIds);
    LeaderDto findLeaderByStudyId(Long studyId);
    List<StudyMemberDto> findMembers(Long studyId, StudyUserFilterDto filter);

}
