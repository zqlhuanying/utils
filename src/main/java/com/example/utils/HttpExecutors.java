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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLInitializationException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author qianliao.zhuang
 */
public final class HttpExecutors {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpExecutors.class);
    private static final String ENCODING = Charsets.UTF_8.name();
    private static final Joiner.MapJoiner QUERY_JOINER = Joiner.on("&").withKeyValueSeparator("=");
    private static final Joiner JOINER = Joiner.on("?");
    private static final Splitter SPLITTER = Splitter.on("?");
    private static final ContentType MULTIPART_FORM_DATA = ContentType.create(
            "multipart/form-data", ENCODING);
    private static final ContentType APPLICATION_JSON = ContentType.create(
            "application/json", ENCODING);
    private static final ContentType APPLICATION_FORM_URLENCODED = ContentType.create(
            "application/x-www-form-urlencoded", ENCODING);
    private static final ContentType TEXT_PLAIN = ContentType.create(
            "text/plain", ENCODING);
    private static final ContentType APPLICATION_OCTET_STREAM = ContentType.create(
            "application/octet-stream");

    @Getter
    private final String url;
    private final Map<String, String> headers;
    private final Map<String, Object> params;
    /**
     * 用于 Post 请求，发送类似 Json/XML等请求
     */
    private final String textBody;
    private final CookieStore cookies;
    /**
     * 用于区分二进制、文件、图片等请求
     */
    private final boolean isBinary;
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
    private final ResponseHandler responseHandler;
    private final boolean ignoreException;
    private final ContentType contentType;
    /**
     * 数据压缩
     */
    private final boolean gzipCompress;
    /**
     * 凭证
     */
    private final CredentialsProvider credentialsProvider;

    private final HttpClient httpClient;

    private HttpExecutors(Builder builder) {
        this.url = builder.url;
        this.headers = builder.headers;
        this.params = builder.params;
        this.textBody = builder.textBody;
        this.cookies = builder.cookies;
        this.isBinary = builder.isBinary;
        this.connectionRequestTimeout = builder.connectionRequestTimeout;
        this.connectTimeout = builder.connectTimeout;
        this.socketTimeout = builder.socketTimeout;
        this.responseHandler = builder.responseHandler;
        this.ignoreException = builder.ignoreException;
        this.contentType = builder.contentType;
        this.gzipCompress = builder.gzipCompress;
        this.credentialsProvider = builder.credentialsProvider;
        this.httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookies)
                .setDefaultCredentialsProvider(credentialsProvider)
                .setDefaultRequestConfig(buildRequestConfig())
                .setConnectionManager(MyPoolingHttpClientConnectionManager.getInstance(builder))
                .build();
    }

    public static HttpExecutors.Builder create(String url) {
        return new Builder().setUrl(url);
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

    private RequestConfig buildRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .build();
    }

    /**
     * Just for get method
     * @param url the origin url
     * @param params the query params
     * @return the real url to be requested
     */
    public static String buildRealUrl(String url, Map<String, Object> params) {
        checkNotNull(url, "url must be not null");

        String realUrl = url;
        if (params != null && params.size() > 0) {
            String queryString = buildQueryString(params);
            Joiner joiner = JOINER;
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

    private static String buildQueryString(final Map<String, Object> params) {
        if (params == null) {
            return "";
        }
        Map<String, String> tmp = Maps.toMap(params.keySet(), new Function<String, String>() {
            @Override
            public String apply(String param) {
                return urlEncode((String) params.get(param));
            }
        });
        return QUERY_JOINER
                .appendTo(new StringBuilder(), tmp)
                .toString();
    }

    private void addHeaders(HttpRequestBase requestBase, Map<String, String> headers) {
        headers.put("Content-type", contentType.getMimeType());
        if (this.gzipCompress) {
            headers.put("Content-Encoding", "gzip");
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBase.addHeader(entry.getKey(), entry.getValue());
        }
    }

    /**
     * just for post method
     * @return HttpEntity
     */
    private HttpEntity buildHttpEntity() {
        HttpEntity httpEntity;
        if (isBinary) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            if (params != null) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    OctetContentBody contentBody = OctetContentBody.valueOf(entry.getValue());
                    builder.addPart(entry.getKey(), contentBody.getContentBody(entry.getValue()));
                }
            }
            httpEntity = builder.build();
        } else {
            String body = StringUtils.isBlank(textBody) ? buildQueryString(params) : textBody;
            ContentType contentType = StringUtils.isBlank(textBody) ? APPLICATION_FORM_URLENCODED : this.contentType;
            StringEntity stringEntity = new StringEntity(body, ENCODING);
            stringEntity.setContentType(contentType.getMimeType());
            httpEntity = stringEntity;
        }
        if (gzipCompress) {
            httpEntity = new GzipCompressingEntity(httpEntity);
        }
        return httpEntity;
    }

    private String doExecute(HttpUriRequest request, HttpContext context) {
        final String requestUrl = request.getURI().toString();
        try {
            return (String) httpClient.execute(request, responseHandler, context);
        } catch (ClientProtocolException e) {
            LOGGER.error("Http execute ClientProtocolException. Url: {}", requestUrl, e);
            return throwOrSwallowException("Http execute ClientProtocolException. Url: " + requestUrl, e);
        } catch (IOException e) {
            LOGGER.error("Http execute IOException. Url: {}", requestUrl, e);
            return throwOrSwallowException("Http execute IOException. Url: " + requestUrl, e);
        } catch (HttpInvokeException e) {
            LOGGER.error("Http invoke exception. Url: {}", requestUrl, e);
            return throwOrSwallowException(e.getMessage() + " Url: " + requestUrl, e);
        } catch (Exception e) {
            LOGGER.error("Http execute Exception. Url: {}", requestUrl, e);
            return throwOrSwallowException("Http execute Exception. Url: " + requestUrl, e);
        }
    }

    private static String urlEncode(String input) {
        if (StringUtils.isBlank(input)) {
            return "";
        }

        try {
            return URLEncoder.encode(input, ENCODING);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("不支持 {} 编码", ENCODING, e);
        }

        return input;
    }

    private static boolean hasQueryString(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        List<String> split = SPLITTER.splitToList(url);
        return CollectionUtils.isNotEmpty(split) && split.size() > 1;
    }

    private String throwOrSwallowException(String message, Throwable e) {
        if (ignoreException) {
            return "";
        } else {
            throw e == null ?
                    new HttpInvokeException(message) : new HttpInvokeException(message, e);
        }
    }

    /**
     * HttpClient 使用 ResponseHandler 会自动释放连接,
     * 因此 ResponseHandler 只需要关心业务实现即可
     */
    private static class DefaultResponseHandler implements ResponseHandler {
        @Override
        public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                LOGGER.warn("statusCode: {}, statusMsg: {}",
                        statusCode, response.getStatusLine().getReasonPhrase());
                throw new HttpInvokeException(
                        String.format("http status: %s, errorMsg: %s", statusCode, response.getStatusLine().getReasonPhrase()),
                        null);
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                LOGGER.warn("response is empty");
                throw new HttpInvokeException("response is empty", null);
            }

            return EntityUtils.toString(entity, ENCODING);
        }
    }

    /**
     * HttpClient 已经内置对 HttpResponse Content-Encoding:gzip, deflate 支持
     * 所以此处 ResponseHandler 是多余的
     * 要想实现自己的gzip/deflate 逻辑，可以禁用内置的解析器
     *
     * HttpClients.custom()
     * .disableContentCompression()
     * .build();
     */
    private static class DecompressResponseHandler extends DefaultResponseHandler {
        @Override
        public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                Header contentEncoding = entity.getContentEncoding();
                if (contentEncoding != null
                        && "gzip".equalsIgnoreCase(contentEncoding.getValue())) {
                    response.setEntity(new GzipDecompressingEntity(entity));
                }
            }
            return super.handleResponse(response);
        }
    }

    private static class MyPoolingHttpClientConnectionManager extends PoolingHttpClientConnectionManager {
        /**
         * 默认连接池最大连接数
         */
        private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;
        /**
         * 默认每个路由最大连接数
         */
        private static final int DEFAULT_MAX_ROUTE_CONNECTIONS = 2;
        /**
         * 默认连接存活时间
         */
        private static final long DEFAULT_TIME_TO_LIVE = -1L;
        /**
         * 默认连接存活时间单位
         */
        private static final TimeUnit DEFAULT_TIME_TO_LIVE_UNIT = TimeUnit.MILLISECONDS;

        private static volatile MyPoolingHttpClientConnectionManager connManager;

        private MyPoolingHttpClientConnectionManager(Builder builder) {
            super(
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.getSocketFactory())
                            .register("https", new SSLConnectionSocketFactory(buildSSLContext(builder)))
                            .build(),
                    null,
                    null,
                    null,
                    builder.timeToLive > 0 ? builder.timeToLive : DEFAULT_TIME_TO_LIVE,
                    builder.timeToLiveUnit != null ? builder.timeToLiveUnit : DEFAULT_TIME_TO_LIVE_UNIT
            );
            this.setMaxTotal(builder.maxTotalConnections > 0 ? builder.maxTotalConnections : DEFAULT_MAX_TOTAL_CONNECTIONS);
            this.setDefaultMaxPerRoute(builder.maxRouteConnections > 0 ? builder.maxRouteConnections : DEFAULT_MAX_ROUTE_CONNECTIONS);
        }

        public static MyPoolingHttpClientConnectionManager getInstance(Builder builder) {
            if (connManager == null) {
                synchronized (MyPoolingHttpClientConnectionManager.class) {
                    if (connManager == null) {
                        connManager = new MyPoolingHttpClientConnectionManager(builder);
                    }
                }
            }
            return connManager;
        }

        private static SSLContext buildSSLContext(Builder builder) {
            if (builder.sslContext != null) {
                return builder.sslContext;
            }

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
                throw new SSLInitializationException("ssl context init failed", e);
            }
        }
    }

    private enum OctetContentBody {
        /**
         * 二进制传输所支持的Body格式
         */
        STRING {
            @Override
            ContentBody getContentBody(Object o) {
                return new StringBody((String) o, TEXT_PLAIN);
            }
        },
        FILE {
            @Override
            ContentBody getContentBody(Object o) {
                File copy = (File) o;
                return new FileBody(copy, MULTIPART_FORM_DATA, copy.getName());
            }
        },
        INPUTSTREAM {
            @Override
            ContentBody getContentBody(Object o) {
                return new InputStreamBody((InputStream) o, APPLICATION_OCTET_STREAM);
            }
        };

        abstract ContentBody getContentBody(Object o);

        public static OctetContentBody valueOf(Object o) {
            if (o instanceof String) {
                return STRING;
            }
            if (o instanceof File) {
                return FILE;
            }
            if (o instanceof InputStream) {
                return INPUTSTREAM;
            }
            throw new IllegalArgumentException(String.format("Type[%s] is not supported", o.getClass().getName()));
        }
    }

    public static class HttpInvokeException extends RuntimeException {
        HttpInvokeException(String message) {
            super(message);
        }

        HttpInvokeException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Setter
    @Accessors(chain = true)
    public static class Builder {
        private String url;
        private Map<String, String> headers;
        private Map<String, Object> params;
        private String textBody;
        private CookieStore cookies;
        private boolean isBinary;
        private int connectionRequestTimeout;
        private int connectTimeout;
        private int socketTimeout;
        private int maxTotalConnections;
        private int maxRouteConnections;
        private long timeToLive;
        private TimeUnit timeToLiveUnit;
        private SSLContext sslContext;
        private ResponseHandler responseHandler;
        private boolean ignoreException;
        private ContentType contentType;
        private boolean gzipCompress;
        private CredentialsProvider credentialsProvider;
        private String userName;
        private String password;

        Builder() {
            super();
            this.headers = new HashMap<>(2);
            this.params = null;
            this.textBody = null;
            this.cookies = null;
            this.isBinary = false;
            this.connectionRequestTimeout = -1;
            this.connectTimeout = -1;
            this.socketTimeout = -1;
            this.maxTotalConnections = -1;
            this.maxRouteConnections = -1;
            this.timeToLive = -1L;
            this.timeToLiveUnit = TimeUnit.MILLISECONDS;
            this.sslContext = null;
            this.responseHandler = new DefaultResponseHandler();
            this.ignoreException = true;
            this.contentType = APPLICATION_JSON;
            this.gzipCompress = false;
            this.credentialsProvider = null;
            this.userName = null;
            this.password = null;
        }

        public HttpExecutors build() {
            checkNotNull(url, "url must be not null");
            if (StringUtils.isNotBlank(userName) || StringUtils.isNotBlank(password)) {
                credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(
                        AuthScope.ANY,
                        new UsernamePasswordCredentials(userName, password)
                );
            }
            return new HttpExecutors(this);
        }
    }
}
