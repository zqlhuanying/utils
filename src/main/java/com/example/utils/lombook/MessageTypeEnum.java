package com.example.utils.lombook;

import lombok.EnumId;
import lombok.Getter;

/**
 * @author zhuangqianliao
 */
public enum MessageTypeEnum {
    /**
     * 测试 Lombook 自定义注解实现
     */
    PC(1, "PC");

    @Getter
    @EnumId
    private int type;
    @Getter
    @EnumId
    private String name;

    MessageTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static void main(String[] args) {

    }
}
