package co.kr.cocomu.admin.controller.code;

import co.kr.cocomu.common.api.ApiCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AdminApiCode implements ApiCode {

    ADD_WORKBOOK_SUCCESS(10000, "스터디 문제집 정보가 추가되었습니다."),
    ADD_LANGUAGE_SUCCESS(10001, "스터디 언어 정보가 추가되었습니다."),
    DELETE_WORKBOOK_SUCCESS(10002, "스터디 문제집 정보가 삭제되었습니다."),
    DELETE_LANGUAGE_SUCCESS(10003, "스터디 언어 정보가 삭제되었습니다."),
    ;

    private final int code;
    private final String message;

}
