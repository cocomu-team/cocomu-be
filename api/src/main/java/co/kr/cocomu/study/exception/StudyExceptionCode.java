package co.kr.cocomu.study.exception;

import co.kr.cocomu.common.exception.dto.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StudyExceptionCode implements ExceptionCode {

    NOT_FOUND_STUDY(3001, "존재하지 않는 스터디입니다."),
    ALREADY_PARTICIPATION_STUDY(3002, "이미 스터디에 참여되었습니다."),
    CAN_NOT_REMOVE(3005, "스터디원이 남아있으면 스터디를 제거할 수 없습니다."),
    LEADER_CAN_NOT_LEAVE(3006, "스터디장은 스터디를 나갈 수 없습니다."),
    MEMBER_CAN_NOT_REMOVE(3007, "스터디원은 스터디를 제거할 수 없습니다."),
    MEMBER_CAN_NOT_EDIT(3008, "스터디원은 스터디를 수정할 수 없습니다."),
    REQUIRED_STUDY_PASSWORD(3007, "스터디 비밀번호는 필수 입력 값입니다."),

    STUDY_IS_FULL(3008, "스터디 최대 인원을 초과했습니다."),
    STUDY_PASSWORD_WRONG(3011, "스터디 비밀번호가 잘못됐습니다."),
    NOT_FOUND_STUDY_USER(3012, "스터디에서 사용자를 찾을 수 없습니다."),
    ;

    private final int code;
    private final String message;

    }
