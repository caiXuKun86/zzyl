package com.zzyl.nursing.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.nursing.service.WechatService;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class WechatServiceImpl implements WechatService {
    @Value("${wechat.appId}")
    private String appId;
    @Value("${wechat.appSecret}")
    private String appSecret;
    // 登录
    private static final String REQUEST_URL = "https://api.weixin.qq.com/sns/jscode2session?grant_type=authorization_code";

    // 获取token
    private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

    // 获取手机号
    private static final String PHONE_REQUEST_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=";
    @Override
    public String getOpenid(String code) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appid","wx9033622bdc68ad7c");
        paramMap.put("secret","249a00e04d9cdb3598b72354b87d378c");
        paramMap.put("js_code",code);

        String result = HttpUtil.get(REQUEST_URL, paramMap);
        JSONObject jsonObject = JSONUtil.parseObj(result);
        if(ObjectUtil.isNotEmpty(jsonObject.getInt("errcode"))){
            throw new BaseException(jsonObject.getStr("errmsg"));
        }

        return jsonObject.getStr("openid");


    }

    @Override
    public String getPhone(String detailCode) {
        String token = getToken();
        String url = PHONE_REQUEST_URL + token;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("code", detailCode);
        // 发起请求
        String result = HttpUtil.post(url, JSONUtil.toJsonStr(paramMap));
        // 是一个map
        JSONObject jsonObject = JSONUtil.parseObj(result);
        // 判断接口响应是否出错
        if(jsonObject.getInt("errcode") != 0) {
            throw new RuntimeException(jsonObject.getStr("errmsg"));
        }

        return jsonObject.getJSONObject("phone_info").getStr("phoneNumber");
    }

    private String getToken() {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appid","wx9033622bdc68ad7c");
        paramMap.put("secret","249a00e04d9cdb3598b72354b87d378c");
        HttpUtil.get(TOKEN_URL);
        // 发起请求
        String result = HttpUtil.get(TOKEN_URL, paramMap);
        // 是一个map
        JSONObject jsonObject = JSONUtil.parseObj(result);
        // 判断接口响应是否出错
        if(ObjectUtil.isNotEmpty(jsonObject.getInt("errcode"))) {
            throw new RuntimeException(jsonObject.getStr("errmsg"));
        }

        String token = jsonObject.getStr("access_token");

        return token;
    }



}
