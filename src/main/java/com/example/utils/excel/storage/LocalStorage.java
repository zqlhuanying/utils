package com.example.utils.excel.storage;

import java.io.File;

/**
 * @author zhuangqianliao
 * 存储到本地
 * 默认的存储策略
 */
public class LocalStorage implements StorageService {

    @Override
    public String store(File file) {
        return file.getAbsolutePath();
    }
}
