package com.zzyl.nursing.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("树形结构VO")
public class TreeVo {
    /**
     * 菜单ID
     */
    @ApiModelProperty(value = "菜单ID")
    private String value;
    
    /**
     * 菜单名称
     */
    @ApiModelProperty(value = "菜单名称")
    private String label;


    /**
     * 子菜单
     */
    @ApiModelProperty(value = "子菜单")
    private List<TreeVo> children;
}