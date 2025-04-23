package co.kr.cocomu.tag.exception;

import co.kr.cocomu.common.exception.dto.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LanguageTagExceptionCode implements ExceptionCode {

    INVALID_REQUEST(5101, "잘못된 요청입니다.");

    private final int code;
    private final String message;

}