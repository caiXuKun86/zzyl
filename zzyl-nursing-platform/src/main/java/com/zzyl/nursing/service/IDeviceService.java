package com.zzyl.nursing.service;

import java.util.List;

import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.nursing.domain.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.vo.DeviceDetailVo;
import com.zzyl.nursing.vo.ProductVo;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author alexis
 * @date 2026-04-01
 */
public interface IDeviceService extends IService<Device>
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public Device selectDeviceById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param device 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<Device> selectDeviceList(Device device);



    public void deleteDevice(String iotId);



    void syncProductList();

    List<ProductVo> allProduct();

    void registerDevice(DeviceDto dto);

    DeviceDetailVo getDeviceDetail(String iotId);

    AjaxResult queryServiceProperties(String iotId);

    void updateDevice(DeviceDto dto);
}
