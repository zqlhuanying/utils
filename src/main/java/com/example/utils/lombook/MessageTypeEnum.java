package com.example.utils.lombook;

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
    private int type;
    @Getter
    private String name;

    MessageTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static void main(String[] args) {

    }
}
