package com.example.utils.model;

/**
 * Created by qianliao.zhuang on 2017/6/1.
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

    public void setUnit(int unit) {
        this.unit = unit;
    }
}
