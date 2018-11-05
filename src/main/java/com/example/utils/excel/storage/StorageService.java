package com.example.utils.excel.storage;

import java.io.File;

/**
 * @author zhuangqianliao
 */
public interface StorageService {
    /**
     * 存储数据
     * @param file: 待存储的文件
     * @return String: 存储后可以访问的路径
     */
    String store(File file);
}
