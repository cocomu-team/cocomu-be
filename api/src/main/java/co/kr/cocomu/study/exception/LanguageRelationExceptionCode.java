package co.kr.cocomu.study.exception;

import co.kr.cocomu.common.exception.dto.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LanguageRelationExceptionCode implements ExceptionCode {

    INVALID_RELATION(3201, "스터디에서 사용하지 않는 언어 태그입니다."),
    ;

    private final int code;
    private final String message;

    }
