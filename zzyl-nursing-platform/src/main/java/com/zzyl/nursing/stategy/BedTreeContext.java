package com.zzyl.nursing.stategy;

import com.zzyl.nursing.enums.QueryStrategyEnum;
import com.zzyl.nursing.vo.TreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class BedTreeContext {

    // 使用 EnumMap 替代 HashMap，性能更高且更规范
    private final Map<QueryStrategyEnum, BedTreeStrategy> strategyMap = new EnumMap<>(QueryStrategyEnum.class);

    @Autowired
    public BedTreeContext(List<BedTreeStrategy> strategies) {
        strategies.forEach(s -> strategyMap.put(s.getStrategyType(), s));
    }

    public List<TreeVo> getBedTree(QueryStrategyEnum type, Integer status) {
        // 默认兜底逻辑：如果传入 null，使用 MEMORY
        BedTreeStrategy strategy = strategyMap.getOrDefault(type, strategyMap.get(QueryStrategyEnum.MEMORY));
        return strategy.getTree(status);
    }
}