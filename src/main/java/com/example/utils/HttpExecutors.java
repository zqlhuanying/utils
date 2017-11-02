package com.example.utils;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLInitializationException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author qianliao.zhuang
 */
@Getter
public final class HttpExecutors {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpExecutors.class);
    private static final String ENCODEING = Charsets.UTF_8.name();
    private static final String QUERY_REGEX = "(?<=%s=)([^&]*)";
    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;
    private static final int DEFAULT_MAX_ROUTE_CONNECTIONS = 2;
    private static final ContentType MULTIPART_FORM_DATA = ContentType.create(
            "multipart/form-data", ENCODEING);
    public static final ContentType APPLICATION_JSON = ContentType.create(
            "application/json", ENCODEING);
    public static final ContentType APPLICATION_FORM_URLENCODED = ContentType.create(
            "application/x-www-form-urlencoded", ENCODEING);

    public static HttpExecutors.Builder create() {
        return new Builder();
    }

    private final String url;
    private final Map<String, String> headers;
    private final Map<String, String> params;
    private final String postBody;
    private final CookieStore cookies;
    /**
     * 用于区分二进制、文件、图片等请求
     */
    private final boolean isBinary;
    private final Map<String, File> binaryMap;
    /**
     * 从连接池获取链接超时设置
     */
    private final int connectionRequestTimeout;
    /**
     * 与服务器建立链接超时设置
     */
    private final int connectTimeout;
    /**
     * 等待服务器返回数据超时设置
     */
    private final int socketTimeout;
    /**
     * 连接池最大连接数
     */
    private final int maxTotalConnections;
    /**
     * 每个路由最大连接数
     */
    private final int maxRouteConnections;
    private final boolean isHttps;
    private ResponseHandler handler;

    private HttpClient httpClient;

    private HttpExecutors(
            final String url,
            final Map<String, String> headers,
            final Map<String, String> params,
            final String postBody,
            final CookieStore cookies,
            final boolean isBinary,
            final Map<String, File> binaryMap,
            final int connectionRequestTimeout,
            final int connectTimeout,
            final int socketTimeout,
            final int maxTotalConnections,
            final int maxRouteConnections,
            final boolean isHttps,
            final ResponseHandler handler) {
        this.url = url;
        this.headers = headers;
        this.params = params;
        this.postBody = postBody;
        this.cookies = cookies;
        this.isBinary = isBinary;
        this.binaryMap = binaryMap;
        this.connectionRequestTimeout = connectionRequestTimeout;
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
        this.maxTotalConnections = maxTotalConnections;
        this.maxRouteConnections = maxRouteConnections;
        this.isHttps = isHttps;
        this.handler = handler;
        initHttpClient();
    }

    public String httpGet() {
        checkNotNull(url, "url must be not null");

        String realUrl = buildRealUrl(url, params);
        // http request
        HttpGet httpGet = new HttpGet(realUrl);
        addHeaders(httpGet, headers);
        // http context
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookies);

        return doExecute(httpGet, context);
    }

    public String httpPost() {
        checkNotNull(url, "url must be not null");

        // http request
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(buildHttpEntity());
        addHeaders(httpPost, headers);
        // http context
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookies);

        return doExecute(httpPost, context);
    }

    /**
     * 使用 HttpClient 时，默认使用了 PoolingHttpClientConnectionManager
     * 因此只要设置相应的参数（maxConnTotal、maxConnPerRoute）即可
     */
    private void initHttpClient() {
        httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookies)
                .setSSLSocketFactory(buildSSLConnectionFactory())
                .setDefaultRequestConfig(buildRequestConfig())
                .setMaxConnTotal(maxTotalConnections)
                .setMaxConnPerRoute(maxRouteConnections)
                .build();
    }

    private SSLConnectionSocketFactory buildSSLConnectionFactory() {
        SSLContext context = buildSSLContext();
        if (context != null) {
            return new SSLConnectionSocketFactory(context);
        }
        return null;
    }

    private SSLContext buildSSLContext() {
        if (isHttps) {
            TrustStrategy trustStrategy = new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            };
            try {
                return SSLContextBuilder.create()
                        .loadTrustMaterial(null, trustStrategy)
                        .build();
            } catch (NoSuchAlgorithmException
                    | KeyManagementException
                    | KeyStoreException e) {
                LOGGER.error("build ssl context failed", e);
                throw new SSLInitializationException("ssl init exception", e);
            }
        }
        return null;
    }

    private RequestConfig buildRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                //.setCookieSpec(CookieSpecs.STANDARD_STRICT)
                .build();
    }

    /**
     * just for get method
     * @param url
     * @param params
     * @return
     */
    public static String buildRealUrl(String url, Map<String, String> params) {
        checkNotNull(url, "url must be not null");

        String realUrl = url;
        if (params != null && params.size() > 0) {
            String queryString = buildQueryString(params);
            Joiner joiner = Joiner.on("?");
            if (hasQueryString(url)) {
                joiner = Joiner.on("&");
            }
            realUrl = joiner.join(Lists.newArrayList(realUrl, queryString));
            // another example http://www.baidu.com?&key=value, so remove first &
            int index = realUrl.indexOf("?&");
            if (index > 0) {
                realUrl = realUrl.replaceFirst("&", "");
            }
        }
        return realUrl;
    }

    public static String buildQueryString(final Map<String, String> params) {
        if (params == null) {
            return "";
        }
        Map<String, String> tmp = Maps.toMap(params.keySet(), new Function<String, String>() {
            @Override
            public String apply(String param) {
                return urlEncode(params.get(param));
            }
        });
        return Joiner.on("&")
                .withKeyValueSeparator("=")
                .appendTo(new StringBuilder(), tmp)
                .toString();
    }

    private void addHeaders(HttpRequestBase requestBase, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBase.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * just for post method
     * @return
     */
    private HttpEntity buildHttpEntity() {
        HttpEntity httpEntity;
        if (isBinary) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            // add binary body
            if (binaryMap != null) {
                for (Map.Entry<String, File> entry : binaryMap.entrySet()) {
                    builder.addBinaryBody(
                            entry.getKey(),
                            entry.getValue(),
                            MULTIPART_FORM_DATA,
                            entry.getValue().getName());
                }
            }
            // add other params
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.addTextBody(entry.getKey(), entry.getValue());
                }
            }
            httpEntity = builder.build();
        } else {
            String body = StringUtils.isBlank(postBody) ? buildQueryString(params) : postBody;
            ContentType contentType = StringUtils.isBlank(postBody) ? APPLICATION_FORM_URLENCODED : APPLICATION_JSON;
            StringEntity stringEntity = new StringEntity(body, ENCODEING);
            stringEntity.setContentEncoding(ENCODEING);
            stringEntity.setContentType(contentType.getMimeType());
            httpEntity = stringEntity;
        }
        return httpEntity;
    }

    private String doExecute(HttpUriRequest request, HttpContext context) {
        try {
            return (String) httpClient.execute(request, handler, context);
        } catch (ClientProtocolException e) {
            LOGGER.error("httpGet ClientProtocolException", e);
        } catch (IOException e) {
            LOGGER.error("httpGet IOException", e);
        } catch (Exception e) {
            LOGGER.error("httpGet Exception", e);
        }
        return "";
    }

    private static String urlEncode(String input) {
        if (StringUtils.isBlank(input)) {
            return "";
        }

        try {
            return URLEncoder.encode(input, ENCODEING);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("不支持 {} 编码", ENCODEING, e);
        }

        return input;
    }

    private static boolean hasQueryString(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        List<String> split = Splitter.on('?').splitToList(url);
        return CollectionUtils.isNotEmpty(split) && split.size() > 1;
    }

    /**
     * 从 url 的查询字符串中获取待查询的 query 值
     *
     * @param url:   Target url
     * @param query: The query
     * @return
     */
    public static String getQueryValue(String url, String query) {
        String regex = String.format(QUERY_REGEX, query);
        return filterByPattern(regex, url);
    }

    public static String filterByPattern(String patternString, String data) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            if (StringUtils.isNotBlank(matcher.group(1))) {
                return matcher.group(1);
            }
        }
        return "";
    }

    private static class DefaultResponseHandler implements ResponseHandler{
        @Override
        public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException{
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                LOGGER.warn("statusCode: {}, statusMsg: {}",
                        statusCode, response.getStatusLine().getReasonPhrase());
                return "";
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                LOGGER.warn("no result return");
                return "";
            }

            String result = EntityUtils.toString(entity, ENCODEING);
            // ensure consume
            EntityUtils.consume(entity);
            return result;
        }
    }

    @Setter
    @Accessors(chain = true)
    public static class Builder {
        private String url;
        private Map<String, String> headers;
        private Map<String, String> params;
        private String postBody;
        private CookieStore cookies;
        private boolean isBinary;
        private Map<String, File> binaryMap;
        private int connectionRequestTimeout;
        private int connectTimeout;
        private int socketTimeout;
        private int maxTotalConnections;
        private int maxRouteConnections;
        private boolean isHttps;
        private ResponseHandler handler;

        Builder() {
            super();
            this.headers = null;
            this.params = null;
            this.postBody = null;
            this.cookies = null;
            this.isBinary = false;
            this.binaryMap = null;
            this.connectionRequestTimeout = -1;
            this.connectTimeout = -1;
            this.socketTimeout = -1;
            this.maxTotalConnections = DEFAULT_MAX_TOTAL_CONNECTIONS;
            this.maxRouteConnections = DEFAULT_MAX_ROUTE_CONNECTIONS;
            this.isHttps = false;
            this.handler = new DefaultResponseHandler();
        }

        public HttpExecutors build() {
            return new HttpExecutors(
                    url,
                    headers,
                    params,
                    postBody,
                    cookies,
                    isBinary,
                    binaryMap,
                    connectionRequestTimeout,
                    connectTimeout,
                    socketTimeout,
                    maxTotalConnections,
                    maxRouteConnections,
                    isHttps,
                    handler
            );
        }
    }
}
