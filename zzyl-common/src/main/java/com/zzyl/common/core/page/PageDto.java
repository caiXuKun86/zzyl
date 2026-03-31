package com.zzyl.common.core.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDto {

    /** 当前记录起始索引 */
    private Integer pageNum;

    /** 每页显示记录数 */
    private Integer pageSize;

}
