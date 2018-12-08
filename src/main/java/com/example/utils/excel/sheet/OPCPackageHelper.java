package com.example.utils.excel.sheet;

import com.example.utils.excel.exception.PoiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author qianliao.zhuang
 */
@Slf4j
public final class OPCPackageHelper {

    public static OPCPackage open(Source<?> source) {
        if (source instanceof PoiFile) {
            return open((PoiFile<?>) source);
        }
        if (source instanceof PoiInputStream) {
            return open((PoiInputStream<?>) source);
        }
        throw new PoiException("source is neither PoiFile nor PoiInputStream type");
    }

    public static OPCPackage open(PoiFile<? extends File> file) {
        try {
            return OPCPackage.open(file.get(), PackageAccess.READ);
        } catch (InvalidFormatException e) {
            log.error("open OPCPackage file[{}] failed", file.name(), e);
            throw new PoiException("open OPCPackage file failed");
        }
    }

    public static OPCPackage open(PoiInputStream<? extends InputStream> inputStream) {
        try {
            return OPCPackage.open(inputStream.get());
        } catch (InvalidFormatException | IOException e) {
            log.error("open OPCPackage stream failed", e);
            throw new PoiException("open OPCPackage stream failed");
        }
    }
}
