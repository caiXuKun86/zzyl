package com.zzyl.nursing.stategy;

import com.zzyl.nursing.enums.QueryStrategyEnum;
import com.zzyl.nursing.mapper.BedMapper;
import com.zzyl.nursing.mapper.FloorMapper;
import com.zzyl.nursing.vo.TreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("sqlStrategy")
public class SqlBedTreeStrategy implements BedTreeStrategy {
    @Autowired
    private FloorMapper floorMapper; // 假设在 BedMapper 中写好了复杂的 JOIN SQL

    @Override
    public List<TreeVo> getTree(Integer status) {
        // 直接调用映射了复杂 JOIN 结果的 Mapper 方法
        // SQL 内部通过 resultMap 处理层级关系
        return floorMapper.getRoomAndBedByBedStatus(status);
    }

    @Override
    public QueryStrategyEnum getStrategyType() {
        return QueryStrategyEnum.SQL;

    }
}