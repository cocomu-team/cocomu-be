package co.kr.cocomu.codingspace.exception;

import co.kr.cocomu.common.exception.dto.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CodingSpaceExceptionCode implements ExceptionCode {

    MAX_USER_COUNT_IS_FOUR(4001, "코딩 스페이스의 최대 인원은 4명입니다."),
    MIN_USER_COUNT_IS_TWO(4002, "코딩 스페이스의 최소 인원은 2명입니다."),
    NOT_FOUND_SPACE(4003, "존재하지 않는 코딩 스페이스입니다."),
    ALREADY_PARTICIPATION_SPACE(4004, "이미 코딩 스페이스에 참여되었습니다."),
    NOT_WAITING_STUDY(4005, "대기중인 코딩 스페이스가 아닙니다."),
    OVER_USER_COUNT(4006, "코딩 스페이스 최대 인원을 초과헀습니다."),
    FINISHED_CODING_SPACE(4007, "종료된 코딩 스페이스입니다."),
    NO_PARTICIPATION_SPACE(4008, "코딩 스페이스에 참여중이지 않습니다."),
    START_MINIMUM_USER_COUNT(4009, "스터디 시작 최소 인원은 2명입니다."),
    NOT_ENTER_SPACE(4010, "코딩 스페이스에 입장하지 않았습니다."),
    MEMBER_CAN_NOT_START(4011, "코딩 스페이스 시작은 방장만 할 수 있습니다."),
    ALREADY_STARTING_SPACE(4012, "이미 시작된 코딩 스페이스입니다."),
    CAN_NOT_FEEDBACK(4013, "스터디 시작 상태일 때만 피드백 모드를 진행할 수 있습니다."),
    CAN_NOT_FINISH(4014, "스터디 피드백 상태일 때만 종료할 수 있습니다."),
    NO_STUDY_MEMBERSHIP(4015, "스터디에 참여한 회원이 아닙니다.");

    private final int code;
    private final String message;

    }
