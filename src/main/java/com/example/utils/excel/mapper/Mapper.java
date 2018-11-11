package com.example.utils.excel.mapper;

import com.example.utils.excel.enums.PoiCellStyle;
import lombok.Getter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author zhuangqianliao
 */
@Getter
public class Mapper<T> {
    private Class<T> clazz;
    private String field;
    private String readMethodName;
    private String writeMethodName;
    private MethodType readMethodType;
    private MethodType writeMethodType;
    private int columnIndex;
    private String columnName;
    private List<PoiCellStyle> cellStyle;

    public Mapper(int columnIndex, String columnName, String fieldName, Class<T> type) {
        this.columnIndex = columnIndex;
        this.columnName = columnName;
        this.field = fieldName;
        this.clazz = type;
        init();
    }

    public Mapper setCellStyle(List<PoiCellStyle> cellStyle) {
        this.cellStyle = cellStyle;
        return this;
    }

    private void init() {
        BeanInfo beanInfo = getBean(this.clazz);
        if (beanInfo == null) {
            throw new NoClassDefFoundError(this.clazz.getName() + " no found");
        }
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            if (descriptor.getDisplayName().equals(this.field)) {
                Method readMethod = descriptor.getReadMethod();
                Method writeMethod = descriptor.getWriteMethod();
                this.readMethodName = readMethod.getName();
                this.writeMethodName = writeMethod.getName();
                this.readMethodType = MethodType.methodType(readMethod.getReturnType());
                this.writeMethodType = MethodType.methodType(void.class, writeMethod.getParameterTypes());
                return;
            }
        }
    }

    private BeanInfo getBean(Class<T> clazz) {
        try {
            return Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            // swallow
        }
        return null;
    }
}
