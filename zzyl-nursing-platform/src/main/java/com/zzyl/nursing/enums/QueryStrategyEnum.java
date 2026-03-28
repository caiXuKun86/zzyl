package com.zzyl.nursing.enums;

public enum QueryStrategyEnum {
    /**
     * Java 内存组装逻辑
     */
    MEMORY,

    /**
     * 数据库 SQL 关联查询
     */
    SQL;

    /**
     * 也可以提供一个静态方法根据字符串转换，增加鲁棒性
     */
    public static QueryStrategyEnum of(String name) {
        for (QueryStrategyEnum strategy : values()) {
            if (strategy.name().equalsIgnoreCase(name)) {
                return strategy;
            }
        }
        return MEMORY; // 默认值
    }
}