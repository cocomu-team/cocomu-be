package co.kr.cocomu.study.exception;

import co.kr.cocomu.common.exception.dto.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagRelationExceptionCode implements ExceptionCode {

    INVALID_TAG_INFORMATION(3201, "잘못된 태그 정보입니다."),
    ;

    private final int code;
    private final String message;

}
