package com.zzyl.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzyl.common.core.domain.R;
import com.zzyl.nursing.domain.Floor;
import com.zzyl.nursing.vo.TreeVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 楼层Mapper接口
 *
 * @author ruoyi
 * @date 2024-04-26
 */
@Mapper
public interface FloorMapper extends BaseMapper<Floor>
{










    /**
     * 查询所有楼层（负责老人）
     * @return 结果
     */
    List<Floor> selectAllByNur();

    List<TreeVo> getRoomAndBedByBedStatus(Integer status);
}
