package com.example.utils.model;

/**
 * @author qianliao.zhuang
 */
public enum TimeUnitEnum {
    MILLI(0),
    SECOND(1),
    MINUTE(2),
    HOUR(3),
    DAY(4),
    WEEK(5),
    MONTH(6),
    YEAR(7);

    private int unit;

    TimeUnitEnum(int unit) {
        this.unit = unit;
    }

    public int getUnit() {
        return unit;
    }
}
