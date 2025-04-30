package co.kr.cocomu.study.exception;

import co.kr.cocomu.common.exception.dto.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MembershipExceptionCode implements ExceptionCode {

    NO_PARTICIPATION(3101, "스터디에 참여중인 사용자가 아닙니다."),
    ;

    private final int code;
    private final String message;

}