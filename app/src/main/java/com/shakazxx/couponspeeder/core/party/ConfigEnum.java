package com.shakazxx.couponspeeder.core.party;

public enum ConfigEnum {

    article_num("article_num", "int"),
    article_time("article_time", "int"),
    video_num("video_num", "int"),
    video_minute("video_minute", "int"),
    enable_article("enable_article", "boolean"),
    enable_video("enable_video", "boolean"),
    enable_tv("enable_tv", "boolean"),
    enable_single_quiz("enable_single_quiz", "boolean"),
    enable_two_person_quiz("enable_two_person_quiz", "boolean"),
    enable_four_person_quiz("enable_four_person_quiz", "boolean"),
    ;

    public String code;
    public String type;

    ConfigEnum(String code, String type) {
        this.code = code;
        this.type = type;
    }
}
