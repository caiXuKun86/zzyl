package com.zzyl.nursing.service;

import java.util.List;

import com.zzyl.common.core.page.PageResult;
import com.zzyl.nursing.domain.Reservation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzyl.nursing.dto.ReservationPageDto;
import com.zzyl.nursing.vo.TimeCountVo;

/**
 * 预约信息Service接口
 *
 * @author alexis
 * @date 2026-03-31
 */
public interface IReservationService extends IService<Reservation> {


    Integer getCancelledReservationCount();

    List<TimeCountVo> getCountByTime(Long time);

    void addReservation(Reservation reservation);

    PageResult<Reservation> selectReservationList(ReservationPageDto dto);

    void cancelReservation(Long id);

    void updateReservationStatus();
}
