package com.zzyl.nursing.mapper;

import java.util.List;
import com.zzyl.nursing.domain.Device;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author alexis
 * @date 2026-04-01
 */
@Mapper
public interface DeviceMapper extends BaseMapper<Device>
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

    /**
     * 新增【请填写功能名称】
     * 
     * @param device 【请填写功能名称】
     * @return 结果
     */
    public int insertDevice(Device device);

    /**
     * 修改【请填写功能名称】
     * 
     * @param device 【请填写功能名称】
     * @return 结果
     */
    public int updateDevice(Device device);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDeviceById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeviceByIds(Long[] ids);
}
