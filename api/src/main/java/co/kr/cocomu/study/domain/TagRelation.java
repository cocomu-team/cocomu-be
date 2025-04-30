package co.kr.cocomu.study.domain;

public interface TagRelation {

    boolean hasSameTagId(Long tagId);
    void useTag();
    void unUseTag();
    boolean isDeleted();

}
