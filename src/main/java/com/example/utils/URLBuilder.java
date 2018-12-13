package com.example.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author qianliao.zhuang
 */
public class URLBuilder {

    private static final String SEPARATOR = "/";

    @Getter
    private String domain;
    @Getter
    private List<String> paths;

    private URLBuilder(String domain) {
        this.domain = domain;
    }

    public static URLBuilder create(String domain) {
        if (StringUtils.isBlank(domain)) {
            throw new NullPointerException("domain must be not null");
        }
        URLBuilder builder = new URLBuilder(domain);
        builder.paths = Lists.newArrayListWithCapacity(4);
        return builder;
    }

    public URLBuilder addPath(String path) {
        if (StringUtils.isBlank(path)) {
            throw new NullPointerException("path must be not null");
        }
        getPaths().add(path);
        return this;
    }

    public String getUrl() {
        if (CollectionUtils.isEmpty(getPaths())) {
            return getDomain();
        }
        String path = Joiner.on(SEPARATOR).join(getPaths());
        path = Files.simplifyPath(path);
        String domain = getDomain();

        if (path.startsWith(SEPARATOR)) {
            path = path.substring(1);
        }
        if (domain.endsWith(SEPARATOR)) {
            domain = domain.substring(0, domain.length() - 1);
        }
        return domain + SEPARATOR + path;
    }
}
