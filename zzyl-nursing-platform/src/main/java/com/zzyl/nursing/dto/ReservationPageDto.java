package com.zzyl.nursing.dto;

import com.zzyl.common.core.page.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPageDto extends PageDto {

    private Integer status;

}
