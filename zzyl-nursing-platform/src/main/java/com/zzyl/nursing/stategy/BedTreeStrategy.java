package com.zzyl.nursing.stategy;

import com.zzyl.nursing.enums.QueryStrategyEnum;
import com.zzyl.nursing.vo.TreeVo;

import java.util.List;

public interface BedTreeStrategy {
    /**
     * 定义统一的获取树形结构的方法
     */
    List<TreeVo> getTree(Integer status);

    /**
     * 策略标识，用于区分是哪种实现
     */
    QueryStrategyEnum getStrategyType();
}