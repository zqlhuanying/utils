package com.example.utils.excel.enums;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhuangqianliao
 */
public interface PoiCellStyle {

    class Color {
        protected static final Color RED = new Color(IndexedColors.RED.index);

        @Getter
        private short value;

        Color(short value) {
            this.value = value;
        }

        public static Color other(short value) {
            return new Color(value);
        }
    }

    /**
     * 字体设置
     */
    enum Font implements PoiCellStyle {
        /**
         * 颜色
         */
        RED(Color.RED.getValue()) {
            @Override
            public void setCellStyle(Cell cell) {
                Map<FontProperty, Object> properties = new HashMap<>(4);
                properties.put(FontProperty.COLOR, getValue());
                org.apache.poi.ss.usermodel.Font font = getFont(cell, properties);
                font.setColor((short) getValue());
                cell.getCellStyle().setFont(font);
            }
        },

        BOLD(true) {
            @Override
            public void setCellStyle(Cell cell) {
                Map<FontProperty, Object> properties = new HashMap<>(4);
                properties.put(FontProperty.BOLD, getValue());
                org.apache.poi.ss.usermodel.Font font = getFont(cell, properties);
                font.setBold((boolean) getValue());
                cell.getCellStyle().setFont(font);
            }
        };

        @Getter
        private Object value;

        Font(Object value) {
            this.value = value;
        }

        protected org.apache.poi.ss.usermodel.Font getFont(Cell cell, Map<FontProperty, Object> properties) {
            Workbook wb = cell.getSheet().getWorkbook();
            org.apache.poi.ss.usermodel.Font font = wb.getFontAt(cell.getCellStyle().getFontIndex());
            return getFont(wb, font, properties);
        }

        private enum FontProperty {
            /**
             * 字体属性
             */
            BOLD, COLOR, FONT_HEIGHT, FONT_NAME, ITALIC, STRIKEOUT, TYPE_OFFSET, UNDERLINE
        }

        /**
         * method for getting font having special settings additional to given source font
         */
        private static org.apache.poi.ss.usermodel.Font getFont(Workbook wb, org.apache.poi.ss.usermodel.Font fontSrc, Map<FontProperty, Object> properties) {
            boolean isBold = properties.get(FontProperty.BOLD) == null ?
                    fontSrc.getBold() : (boolean) properties.get(FontProperty.BOLD);
            short color = properties.get(FontProperty.COLOR) == null ?
                    fontSrc.getColor() : (short) properties.get(FontProperty.COLOR);
            short fontHeight = properties.get(FontProperty.FONT_HEIGHT) == null ?
                    fontSrc.getFontHeight() : (short) properties.get(FontProperty.FONT_HEIGHT);
            String fontName = properties.get(FontProperty.FONT_NAME) == null ?
                    fontSrc.getFontName() : (String) properties.get(FontProperty.FONT_NAME);
            boolean isItalic = properties.get(FontProperty.ITALIC) == null ?
                    fontSrc.getItalic() : (boolean) properties.get(FontProperty.ITALIC);
            boolean isStrikeout = properties.get(FontProperty.STRIKEOUT) == null ?
                    fontSrc.getStrikeout() : (boolean) properties.get(FontProperty.STRIKEOUT);
            short typeOffset = properties.get(FontProperty.TYPE_OFFSET) == null ?
                    fontSrc.getTypeOffset() : (short) properties.get(FontProperty.TYPE_OFFSET);
            byte underline = properties.get(FontProperty.UNDERLINE) == null ?
                    fontSrc.getUnderline() : (byte) properties.get(FontProperty.UNDERLINE);

            org.apache.poi.ss.usermodel.Font font = wb.findFont(
                    isBold, color, fontHeight, fontName,
                    isItalic, isStrikeout, typeOffset, underline
            );
            if (font == null) {
                font = wb.createFont();
                font.setBold(isBold);
                font.setColor(color);
                font.setFontHeight(fontHeight);
                font.setFontName(fontName);
                font.setItalic(isItalic);
                font.setStrikeout(isStrikeout);
                font.setTypeOffset(typeOffset);
                font.setUnderline(underline);
            }

            return font;
        }
    }

    enum BACKGROUND implements PoiCellStyle {
        /**
         * 背景色
         */
        RED {
            @Override
            public void setCellStyle(Cell cell) {
                cell.getCellStyle().setFillForegroundColor(Color.RED.getValue());
                cell.getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
        }
    }

    void setCellStyle(Cell cell);
}
