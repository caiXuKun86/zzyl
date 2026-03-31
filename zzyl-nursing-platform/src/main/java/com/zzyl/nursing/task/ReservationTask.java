package com.zzyl.nursing.task;

import com.zzyl.nursing.service.IContractService;
import com.zzyl.nursing.service.IReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("reservationTask")
@Slf4j
public class ReservationTask {

    @Autowired
    private IReservationService reservationService;

    public void updateReservationStatus() {
        reservationService.updateReservationStatus();

    }
}