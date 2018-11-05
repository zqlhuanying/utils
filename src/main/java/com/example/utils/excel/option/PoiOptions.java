package com.example.utils.excel.option;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author zhuangqianliao
 */
public final class PoiOptions {
    @Getter
    private int skip;
    @Getter
    private int sheetIndex;
    /**
     * 只在 WorkbookBigSheet 中才有效
     * 默认值：65535
     */
    @Getter
    private int threshold;

    /**
     * 用户自定义信息，可以在 ResultAdvice 以及 ErrorHandler 中访问
     * 可能不太合适
     */
    @Getter
    private Attribute<?> attributes;

    /**
     * 忽略字段
     */
    @Getter
    private List<String> ignoreFields;

    public static PoiOptionsBuilder settings() {
        return new PoiOptionsBuilder();
    }

    private PoiOptions(PoiOptionsBuilder builder) {
        this.skip = builder.skip;
        this.sheetIndex = builder.sheetIndex;
        this.threshold = builder.threshold;
        this.attributes = builder.attributes;
        this.ignoreFields = builder.ignoreFields;
        checkArgument(this.skip > -1, "skip must be negative");
        checkArgument(this.sheetIndex > -1, "sheetIndex must be negative");
        checkArgument(this.threshold > -1, "threshold must be negative");
    }

    @Accessors(chain = true)
    @Data
    public static class PoiOptionsBuilder {
        private int skip = 1;
        private int sheetIndex = 0;
        private int threshold = 0x0000ffff;
        private Attribute<?> attributes;
        private List<String> ignoreFields;

        public PoiOptions build() {
            return new PoiOptions(this);
        }
    }
}
