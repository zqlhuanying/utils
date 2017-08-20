package com.example.utils;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by qianliao.zhuang on 2017/7/22.
 */
public class HttpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);
    private static final String QUERY_REGEX = "(?<=%s=)([^&]*)";
    private static final String ENCODEING = Charsets.UTF_8.name();

    public static String httpGet(String url) {
        return httpGet(url, new HashMap<String, String>());
    }

    public static String httpGet(String url, Map<String, String> headers){
        return httpGet(url, headers, new HashMap<String, String>());
    }

    public static String httpGet(String url, Map<String, String> headers, Map<String, String> params){
        return httpGet(url, headers, params, null);
    }

    public static String httpGet(String url, CookieStore cookieStore){
        return httpGet(url, null, cookieStore);
    }

    public static String httpGet(String url, Map<String, String> headers, CookieStore cookieStore){
        return httpGet(url, headers, null, cookieStore);
    }

    /**
     * Http Get
     * @param url: Target Url
     * @param headers: Http Header
     * @param params: Http Request Params
     * @param cookieStore: Cookie
     * @return
     */
    public static String httpGet(String url, Map<String, String> headers,
                                  Map<String, String> params, CookieStore cookieStore){
        checkNotNull(url, "url must be not null");

        String realUrl = buildRealUrl(url, params);
        try {
            // http client
            HttpClient httpclient = createSSLInsecureClient(cookieStore);
            // http request
            HttpGet httpget = new HttpGet(realUrl);
            addHeaders(httpget, headers);
            // http context
            HttpClientContext context = HttpClientContext.create();
            context.setCookieStore(cookieStore);

            HttpResponse response = httpclient.execute(httpget, context);
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

            return EntityUtils.toString(entity, ENCODEING);
        } catch (ClientProtocolException e) {
            LOGGER.error("httpGet ClientProtocolException", e);
        } catch (IOException e) {
            LOGGER.error("httpGet IOException", e);
        } catch (Exception e) {
            LOGGER.error("httpGet Exception", e);
        }
        return "";
    }

    public static String httpPost(String url){
        return httpPost(url, new HashMap<String, String>());
    }

    public static String httpPost(String url, Map<String, String> headers){
        return httpPost(url, headers, new HashMap<String, String>());
    }

    public static String httpPost(String url, String postBody){
        return httpPost(url, new HashMap<String, String>(), postBody);
    }

    public static String httpPost(String url, Map<String, String> headers, Map<String, String> params){
        return httpPost(url, headers, params, null);
    }

    public static String httpPost(String url, Map<String, String> headers, String postBody){
        return httpPost(url, headers, postBody, null);
    }

    public static String httpPost(String url, CookieStore cookieStore){
        return httpPost(url, new HashMap<String, String>(), cookieStore);
    }

    public static String httpPost(String url, Map<String, String> headers, CookieStore cookieStore){
        return httpPost(url, headers, new HashMap<String, String>(), cookieStore);
    }

    public static String httpPost(String url, String postBody, CookieStore cookieStore){
        return httpPost(url, null, postBody, cookieStore);
    }

    /**
     * Http Post
     * @param url: Target Url
     * @param headers: Http Header
     * @param params: Http Request Params
     * @param cookieStore: Cookie
     * @return
     */
    public static String httpPost(String url, Map<String, String> headers,
                                 Map<String, String> params, CookieStore cookieStore){
        return httpPost(url, headers, buildQueryString(params), cookieStore);
    }

    /**
     * Http Post
     * @param url: Target Url
     * @param headers: Http Header
     * @param postBody: Http Post Content
     * @param cookieStore: Cookie
     * @return
     */
    public static String httpPost(String url, Map<String, String> headers,
                                  String postBody, CookieStore cookieStore){
        checkNotNull(url, "url must be not null");

        try {
            // http client
            HttpClient httpclient = createSSLInsecureClient(cookieStore);
            // http request
            HttpPost httpPost = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(postBody, ENCODEING);//解决中文乱码问题
            stringEntity.setContentEncoding(ENCODEING);
            httpPost.setEntity(stringEntity);
            addHeaders(httpPost, headers);
            // http context
            HttpClientContext context = HttpClientContext.create();
            context.setCookieStore(cookieStore);

            HttpResponse response = httpclient.execute(httpPost, context);
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

            return EntityUtils.toString(entity, ENCODEING);
        } catch (ClientProtocolException e) {
            LOGGER.error("httpGet ClientProtocolException", e);
        } catch (IOException e) {
            LOGGER.error("httpGet IOException", e);
        } catch (Exception e) {
            LOGGER.error("httpGet Exception", e);
        }
        return "";
    }

    public static CloseableHttpClient createSSLInsecureClient(CookieStore cookieStore) {
        try {
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, new TrustStrategy() { //信任所有
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .setSSLSocketFactory(sslsf)
                    .setDefaultRequestConfig(requestConfig)
                    .build();
            return httpClient;
        } catch (KeyManagementException e) {
            LOGGER.error("createSSLInsecureClient KeyManagementException", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("createSSLInsecureClient NoSuchAlgorithmException", e);
        } catch (KeyStoreException e) {
            LOGGER.error("createSSLInsecureClient KeyStoreException", e);
        }
        return HttpClients.createDefault();
    }

    public static String getQueryString(String url){
        checkNotNull(url, "url must be not null");

        List<String> split = Splitter.on('?').splitToList(url);
        if(CollectionUtils.isEmpty(split) || split.size() <= 1){
            // the url doesn't have query string
            return "";
        }
        return split.get(1);
    }

    public static Map<String, String> getQueryMap(String url){
        String queryString = getQueryString(url);
        if(StringUtils.isBlank(queryString)){
            return Maps.newHashMap();
        }
        return Splitter
                .on("&")
                .withKeyValueSeparator("=")
                .split(queryString);
    }

    public static String buildQueryString(final Map<String, String> params){
        if(params == null){
            return "";
        }
        Map<String, String> tmp = Maps.toMap(params.keySet(), new Function<String, String>() {
            @Override
            public String apply(String input) {
                return urlEncode(params.get(input));
            }
        });
        StringBuilder stringBuilder = new StringBuilder();
        return Joiner.on("&")
                .withKeyValueSeparator("=")
                .appendTo(stringBuilder, tmp)
                .toString();
    }

    // just for get method
    public static String buildRealUrl(String url, Map<String, String> params){
        checkNotNull(url, "url must be not null");

        String realUrl = url;
        if(params != null && params.size() > 0){
            String queryString = buildQueryString(params);
            Joiner joiner = Joiner.on("?");
            if(hasQueryString(url)){
                joiner = Joiner.on("&");
            }
            realUrl = joiner.join(Lists.newArrayList(realUrl, queryString));
            // another example http://www.baidu.com?&key=value, so remove first &
            int index = realUrl.indexOf("?&");
            if(index > 0){
                realUrl = realUrl.replaceFirst("&", "");
            }
        }
        return realUrl;
    }

    /**
     * 从 url 的查询字符串中获取待查询的 query 值
     * @param url: Target url
     * @param query: The query
     * @return
     */
    public static String getQueryValue(String url, String query){
        String regex = String.format(QUERY_REGEX, query);
        return filterByPattern(regex, url);
    }

    private static void addHeaders(HttpRequestBase requestBase, Map<String, String> headerMap) {
        if(headerMap != null){
            Iterator<Map.Entry<String, String>> entries = headerMap.entrySet().iterator();
            while (entries.hasNext()){
                Map.Entry<String, String> entry = entries.next();
                requestBase.addHeader(entry.getKey(), entry.getValue());
            }
        }
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

    private static boolean hasQueryString(String url){
        checkNotNull(url, "url must be not null");

        List<String> split = Splitter.on('?').splitToList(url);
        if(CollectionUtils.isEmpty(split) || split.size() <= 1){
            return false;
        }
        return true;
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
}
