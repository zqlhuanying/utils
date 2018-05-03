package com.example.utils.fastjson.typereference;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author qianliao.zhuang
 * 承接Web层返回的数据
 */
@Slf4j
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -9009950023183744177L;

    // 返回值 0：正确 ：其他 不正确，可以适当使用调用链ID
    @Getter
    @Setter
    private Long errcode;

    // 操作消息
    @Getter
    @Setter
    private String errmsg;

    @Getter
    @Setter
    private T data;

    /**
     * 支持 T 多层嵌套的写法，如T: PageResponse<UserInfo>
     * @param apiResponse
     * @param pojo
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> checkAndGet(final String apiResponse, final TypeReference<T> pojo) {
        BaseResponse<T> response = from(apiResponse, pojo);
/*        if (BaseErrorCode.isFail(response.getErrcode())) {
            throw new HttpInvokeException(response.getErrcode(), response.getErrmsg());
        }*/
        return response;
    }

    public static <T> BaseResponse<T> checkAndGet(final String apiResponse, final Class<T> pojo) {
        return checkAndGet(apiResponse, new TypeReference<T>(){
            @Override
            public Type getType() {
                return pojo;
            }
        });
    }

    public static <T> BaseResponse<List<T>> checkAndGetList(final String apiResponse, final Class<T> pojo) {
        return checkAndGet(apiResponse, new TypeReference<List<T>>(pojo){});
    }

    /**
     * 转换api层返回的数据
     * @param apiResponse
     * @param pojo: data类型
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> BaseResponse<T> from(final String apiResponse, final TypeReference<T> pojo) {
        BaseResponse response = from(apiResponse);
        if (response.getData() == null) {
            return response;
        }
        T data = JSONObject.parseObject(JSONObject.toJSONString(response.getData()), pojo.getType());
        response.setData(data);
        return response;
    }

    /**
     * 转换api层返回的数据，data 默认Object类型
     * @param apiResponse
     * @return
     */
    public static BaseResponse from(final String apiResponse) {
        if (StringUtils.isBlank(apiResponse)) {
            throw new IllegalArgumentException("api response is empty");
        }
        BaseResponse response = JSONObject.parseObject(apiResponse, BaseResponse.class);
        if (response.getErrcode() == null
                || StringUtils.isBlank(response.getErrmsg())) {
            throw new IllegalArgumentException("api response is wrong format");
        }
        return response;
    }
}
