package com.zzyl.nursing.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeCountVo {

    private LocalDateTime time;
    private Integer count;
}
