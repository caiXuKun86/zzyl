package com.zzyl.nursing.controller;

import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.core.domain.R;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.service.IDeviceService;
import com.zzyl.nursing.vo.DeviceDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能设备Controller
 *
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/nursing/device")
@Api(tags = "智能设备的接口")
public class DeviceController extends BaseController {
    @Autowired
    private IDeviceService deviceService;

    /**
     * 查询设备列表
     */
    @PreAuthorize("@ss.hasPermi('elder:device:list')")
    @GetMapping("/list")
    @ApiOperation("查询设备列表")
    public TableDataInfo list(Device device) {
        startPage();
        List<Device> list = deviceService.selectDeviceList(device);
        return getDataTable(list);
    }

    @PostMapping("/syncProductList")
    public AjaxResult syncProductList() {
        deviceService.syncProductList();
        return success();
    }

    @GetMapping("/allProduct")
    public AjaxResult allProduct() {
        return AjaxResult.success(deviceService.allProduct());
    }

    @PostMapping("/register")
    public AjaxResult register(@RequestBody DeviceDto dto) {
        deviceService.registerDevice(dto);
        return success();
    }
    @GetMapping("/{iotId}")
    public R<DeviceDetailVo> getDeviceDetail(@PathVariable("iotId") String iotId){
        return R.ok(deviceService.getDeviceDetail(iotId));
    }

    @GetMapping("/queryServiceProperties/{iotId}")
    public AjaxResult queryServiceProperties(@PathVariable("iotId") String iotId){
        return deviceService.queryServiceProperties(iotId);
    }

    @PutMapping
    public AjaxResult update(@RequestBody DeviceDto dto) {
        deviceService.updateDevice(dto);
        return success();
    }

    @DeleteMapping("/{iotId}")
    public AjaxResult delete(@PathVariable("iotId") String iotId) {
        deviceService.deleteDevice(iotId);
        return AjaxResult.success();
    }


}