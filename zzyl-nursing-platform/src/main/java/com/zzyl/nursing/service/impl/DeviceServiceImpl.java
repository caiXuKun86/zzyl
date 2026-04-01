package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.util.*;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.huaweicloud.sdk.iotda.v5.model.*;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.DateTimeZoneConverter;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.vo.DeviceDetailVo;
import com.zzyl.nursing.vo.ProductVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.DeviceMapper;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.service.IDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author alexis
 * @date 2026-04-01
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceService {
    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private IoTDAClient iotDAClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public Device selectDeviceById(Long id) {
        return getById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param device 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<Device> selectDeviceList(Device device) {
        return deviceMapper.selectDeviceList(device);
    }


    @Override
    public void deleteDevice(String iotId) {
        DeleteDeviceRequest request = new DeleteDeviceRequest();
        request.setDeviceId(iotId);
        DeleteDeviceResponse response;
        try {
            response = iotDAClient.deleteDevice(request);
        } catch (Exception e) {
            throw new BaseException("删除失败");
        }
        remove(new LambdaQueryWrapper<Device>().eq(Device::getIotId, iotId));

    }


    @Override
    public void syncProductList() {
        ListProductsRequest request = new ListProductsRequest();
        request.setLimit(50);
        ListProductsResponse response = iotDAClient.listProducts(request);
        if (response.getHttpStatusCode() != 200) {
            throw new BaseException("物联网接口 - 查询产品，同步失败");
        }
        // 存储到redis
        redisTemplate.opsForValue().set("iot:product_list", JSONUtil.toJsonStr(response.getProducts()));
    }

    @Override
    public List<ProductVo> allProduct() {
        String jsonStr = redisTemplate.opsForValue().get("iot:product_list");
        if (StringUtils.isEmpty(jsonStr)) {
            return Collections.emptyList();
        }
        return JSONUtil.toList(jsonStr, ProductVo.class);

    }

    @Override
    public void registerDevice(DeviceDto dto) {
        Long count = lambdaQuery().eq(Device::getDeviceName, dto.getDeviceName()).count();
        if (count > 0) {
            throw new BaseException("设备名称已存在，请重新输入");
        }
        count = lambdaQuery().eq(Device::getNodeId, dto.getNodeId()).count();
        if (count > 0) {
            throw new BaseException("设备标识码已存在，请重新输入");
        }

        // 判断同一位置是否绑定了相同的产品
        count = count(Wrappers.<Device>lambdaQuery()
                .eq(Device::getProductKey, dto.getProductKey())
                .eq(Device::getBindingLocation, dto.getBindingLocation())
                .eq(Device::getLocationType, dto.getLocationType())
                .eq(dto.getPhysicalLocationType() != null, Device::getPhysicalLocationType, dto.getPhysicalLocationType()));
        if (count > 0) {
            throw new BaseException("该老人/位置已绑定该产品，请重新选择");
        }

        // 注册设备--->IoT平台
        AddDeviceRequest request = new AddDeviceRequest();
        AddDevice body = new AddDevice();
        body.withProductId(dto.getProductKey());
        body.withDeviceName(dto.getDeviceName());
        body.withNodeId(dto.getNodeId());

        // 秘钥设置
        AuthInfo authInfo = new AuthInfo();
        String secret = UUID.randomUUID().toString().replaceAll("-", "");
        authInfo.withSecret(secret);
        body.setAuthInfo(authInfo);
        request.setBody(body);
        AddDeviceResponse response;
        try {
            response = iotDAClient.addDevice(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("物联网接口 - 注册设备，调用失败");
        }
        // 本地保存设备
        // 属性拷贝
        Device device = BeanUtil.toBean(dto, Device.class);

        // 秘钥
        device.setSecret(secret);
        // 设备id、设备绑定状态
        device.setIotId(response.getDeviceId());
        save(device);
    }

    @Override
    public DeviceDetailVo getDeviceDetail(String iotId) {

        Device device = lambdaQuery().eq(Device::getIotId, iotId).one();
        if (ObjectUtil.isEmpty(device)) {
            return null;
        }

        ShowDeviceRequest request = new ShowDeviceRequest();
        request.setDeviceId(iotId);
        ShowDeviceResponse response;
        try {
            response = iotDAClient.showDevice(request);
        } catch (Exception e) {
            throw new BaseException("物联网接口 - 查询设备详情，调用失败");
        }

        DeviceDetailVo vo = new DeviceDetailVo();
        BeanUtils.copyProperties(device, vo);
        vo.setCreateTime(LocalDateTime.now());
        vo.setDeviceStatus(response.getStatus());
        String activeTimeStr = response.getActiveTime();
        // 日期转换
        if (StringUtils.isNotEmpty(activeTimeStr)) {
            // 把字符串转换为LocalDateTime
            LocalDateTime activeTime = LocalDateTimeUtil.parse(activeTimeStr, DatePattern.UTC_MS_PATTERN);
            // 日期时区转换
            vo.setActiveTime(DateTimeZoneConverter.utcToShanghai(activeTime));
        }
        return vo;
    }

    @Override
    public AjaxResult queryServiceProperties(String iotId) {

        ShowDeviceShadowRequest request = new ShowDeviceShadowRequest();
        request.setDeviceId(iotId);
        ShowDeviceShadowResponse response;
        try {
            response = iotDAClient.showDeviceShadow(request);
        } catch (Exception e) {
            throw new BaseException("物联网接口 - 查询设备影子，调用失败");
        }
        List<DeviceShadowData> shadow = response.getShadow();
        if (CollUtil.isEmpty(shadow)) {
            List<Object> emptyList = Collections.emptyList();
            return AjaxResult.success(emptyList);
        }
        DeviceShadowProperties reported = shadow.get(0).getReported();
        JSONObject jsonObject = JSONUtil.parseObj(reported.getProperties());
        // 事件上报时间
        String eventTimeStr = reported.getEventTime();
        // 把字符串转换为LocalDateTime
        LocalDateTime eventTimeLocalDateTime = LocalDateTimeUtil.parse(eventTimeStr, "yyyyMMdd'T'HHmmss'Z'");
        // 时区转换
        LocalDateTime eventTime = DateTimeZoneConverter.utcToShanghai(eventTimeLocalDateTime);
        List<Map<String, Object>> list = new ArrayList<>();
        jsonObject.forEach((k, v) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("functionId", k);
            map.put("value", v);
            map.put("eventTime", eventTime);
            list.add(map);
        });
        return AjaxResult.success(list);

    }

    @Override
    public void updateDevice(DeviceDto dto) {
        UpdateDeviceRequest request = new UpdateDeviceRequest();
        request.setDeviceId(dto.getIotId());
        UpdateDevice body = new UpdateDevice();
        body.setDeviceName(dto.getDeviceName());
        request.setBody(body);

        UpdateDeviceResponse response;
        try {
            response = iotDAClient.updateDevice(request);
        } catch (Exception e) {
            throw new BaseException("修改失败");
        }

        Device device = new Device();
        BeanUtils.copyProperties(dto, device);
        updateById(device);

    }
}
