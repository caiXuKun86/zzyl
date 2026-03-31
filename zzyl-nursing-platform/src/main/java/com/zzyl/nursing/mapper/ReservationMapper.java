package com.zzyl.nursing.mapper;

import java.time.LocalDateTime;
import java.util.List;
import com.zzyl.nursing.domain.Reservation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzyl.nursing.vo.TimeCountVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 预约信息Mapper接口
 * 
 * @author alexis
 * @date 2026-03-31
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation>
{
    List<TimeCountVo> selectCountByTime(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
