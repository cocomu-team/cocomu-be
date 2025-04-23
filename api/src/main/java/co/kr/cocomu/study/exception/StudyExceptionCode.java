package co.kr.cocomu.study.exception;

import co.kr.cocomu.common.exception.dto.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StudyExceptionCode implements ExceptionCode {

    NOT_FOUND_STUDY(3001, "존재하지 않는 스터디입니다."),
    ALREADY_PARTICIPATION_STUDY(3002, "이미 스터디에 참여되었습니다."),
    LEADER_MUST_USE_REMOVE(3003, "스터디장은 스터디 삭제를 이용해주세요."),
    USER_IS_NOT_LEADER(3004, "스터디 리더가 아닙니다."),
    REMAINING_MEMBER(3005, "스터디원이 남아있으면 스터디를 제거할 수 없습니다."),
    NO_PARTICIPATION_USER(3006, "해당 스터디의 스터디원이 아닙니다."),

    STUDY_IS_FULL(3008, "스터디 현재 인원은 최대 인원을 초과할 수 없습니다."),
    ALREADY_LEADER_EXISTS(3009, "이미 방장이 존재합니다."),
    STUDY_REQUIRES_LEADER(3010, "스터디는 리더가 필요합니다."),
    STUDY_PASSWORD_WRONG(3011, "스터디 비밀번호가 잘못됐습니다."),
    NOT_FOUND_STUDY_USER(3012, "스터디에서 사용자를 찾을 수 없습니다."),
    USE_PRIVATE_JOIN(3013, "비공개 스터디 참여를 이용해주세요."),
    USE_PUBLIC_JOIN(3014, "공개 스터디 참여를 이용해주세요."),
    REQUIRED_WORKBOOK_TAG(3015, "문제집 태그는 필수 입력 값입니다."),
    REQUIRED_LANGUAGE_TAG(3016, "언어 태그는 필수 입력 값입니다.");

    private final int code;
    private final String message;

    }
