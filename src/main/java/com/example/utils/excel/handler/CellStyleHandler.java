package com.example.utils.excel.handler;


import com.example.utils.excel.enums.PoiCellStyle;
import com.example.utils.excel.mapper.Mapper;

import java.util.List;

/**
 * @author zhuangqianliao
 */
public interface CellStyleHandler<T> {

    /**
     * 获取单元格样式
     * @param rowData: 该行数据
     * @param mapper: Mapper<T>对象
     * @param cellValue: 单元格值
     * @return: 样式列表
     */
    List<PoiCellStyle> getCellStyle(T rowData, Mapper<T> mapper, String cellValue);
}
