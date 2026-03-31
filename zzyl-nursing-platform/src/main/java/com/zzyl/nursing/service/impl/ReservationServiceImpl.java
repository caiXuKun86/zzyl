package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzyl.common.core.page.PageResult;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.UserThreadLocal;
import com.zzyl.nursing.dto.ReservationPageDto;
import com.zzyl.nursing.vo.TimeCountVo;
import org.apache.poi.ss.formula.functions.Now;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.ReservationMapper;
import com.zzyl.nursing.domain.Reservation;
import com.zzyl.nursing.service.IReservationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 预约信息Service业务层处理
 *
 * @author alexis
 * @date 2026-03-31
 */
@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements IReservationService {
    private static final Object LOCK = new Object();
    @Autowired
    private ReservationMapper reservationMapper;


    @Override
    public Integer getCancelledReservationCount() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.toLocalDate().atStartOfDay();
        LocalDateTime endTime = startTime.plusDays(1L);
        Long count = lambdaQuery()
                .between(Reservation::getUpdateTime, startTime, endTime)
                .eq(Reservation::getUpdateBy, UserThreadLocal.getUserId())
                .eq(Reservation::getStatus, 2)
                .count();

        return count.intValue();
    }

    @Override
    public List<TimeCountVo> getCountByTime(Long time) {
        LocalDateTime now = LocalDateTimeUtil.of(time);
        LocalDateTime startTime = now.toLocalDate().atStartOfDay();
        LocalDateTime endTime = startTime.plusDays(1L);

        return reservationMapper.selectCountByTime(startTime, endTime);


    }

    @Override
    public void addReservation(Reservation reservation) {
        if (getCancelledReservationCount() >= 3) {
            throw new BaseException("取消预约次数过多");
        }
        Long userId = UserThreadLocal.get();
        synchronized (LOCK) {
            Long count = lambdaQuery()
                    .eq(Reservation::getTime, reservation.getTime())
                    .ne(Reservation::getStatus, 2)
                    .count();
            if (count >= 6) {
                throw new BaseException("该时间预约次数已满");
            }
            Reservation reservation2Db = new Reservation();
            BeanUtils.copyProperties(reservation, reservation2Db);
            reservation2Db.setStatus(0);
            reservation2Db.setCreateTime(DateUtils.getNowDate());
            reservation2Db.setUpdateTime(DateUtils.getNowDate());
            reservation2Db.setCreateBy(userId.toString());
            reservation2Db.setUpdateBy(userId.toString());
            saveOrUpdate(reservation2Db);
        }

    }

    @Override
    public PageResult<Reservation> selectReservationList(ReservationPageDto dto) {
        Page<Reservation> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        lambdaQuery()
                .eq(Reservation::getUpdateBy, UserThreadLocal.getUserId())
                .eq(dto.getStatus() != null, Reservation::getStatus, dto.getStatus())
                .page(page);
        PageResult<Reservation> pageResult = new PageResult<>();
        pageResult.setTotal(page.getTotal());
        pageResult.setRows(page.getRecords());
        return pageResult;
    }

    @Override
    public void cancelReservation(Long id) {
        Reservation reservation = getById(id);
        if (reservation.getStatus() != 0) {
            throw new BaseException("只能取消待报道的订单");
        }

        if (!Objects.equals(reservation.getUpdateBy(), UserThreadLocal.get().toString())) {
            throw new BaseException("只能取消自己的订单");
        }
        lambdaUpdate()
                .set(Reservation::getStatus, 2)
                .eq(Reservation::getId,id)
                .eq(Reservation::getUpdateBy, UserThreadLocal.get())
                .update();
    }

    @Override
    public void updateReservationStatus() {
//        LocalDateTime startTime = now.toLocalDate().atStartOfDay();
//        LocalDateTime endTime = startTime.plusDays(1L);LocalDateTime now = LocalDateTimeUtil.now();

        lambdaUpdate()
                .set(Reservation::getStatus, 3)
                .lt(Reservation::getTime,LocalDateTime.now())
                .update();

    }


}
