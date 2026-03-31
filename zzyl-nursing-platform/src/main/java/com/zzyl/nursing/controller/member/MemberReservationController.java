package com.zzyl.nursing.controller.member;

import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.core.domain.R;
import com.zzyl.common.core.page.PageResult;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.nursing.domain.NursingProject;
import com.zzyl.nursing.domain.Reservation;
import com.zzyl.nursing.dto.ReservationPageDto;
import com.zzyl.nursing.service.IReservationService;

import com.zzyl.nursing.vo.TimeCountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约信息Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/member/reservation")
public class MemberReservationController extends BaseController {

    @Autowired
    private IReservationService reservationService;

    @GetMapping("/cancelled-count")
    public R<Integer> getCancelledReservationCount() {
        return R.ok(reservationService.getCancelledReservationCount());
    }

    @GetMapping("/countByTime")
    public R<List<TimeCountVo>> getCountByTime(Long time) {
        return R.ok(reservationService.getCountByTime(time));
    }

    @PostMapping
    public AjaxResult addReservation(@RequestBody Reservation reservation) {
        reservationService.addReservation(reservation);
        return AjaxResult.success();
    }
    @GetMapping("/page")
    public R<PageResult<Reservation>> page(ReservationPageDto dto) {
        return R.ok(reservationService.selectReservationList(dto));
    }

    @PutMapping("/{id}/cancel")
    public AjaxResult cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return AjaxResult.success();
    }
}