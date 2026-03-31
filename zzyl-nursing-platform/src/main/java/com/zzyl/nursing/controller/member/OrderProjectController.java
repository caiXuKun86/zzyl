package com.zzyl.nursing.controller.member;

import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.R;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.nursing.domain.NursingProject;
import com.zzyl.nursing.service.INursingProjectService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RequestMapping("/member/orders/project")
@RestController
public class OrderProjectController extends BaseController {

    @Autowired
    private INursingProjectService nursingProjectService;


    @GetMapping("/page")
    public TableDataInfo<List<NursingProject>> page(NursingProject nursingProject) {
        startPage();
        List<NursingProject> list = nursingProjectService.selectNursingProjectList(nursingProject);
        return getDataTable(list);
    }


    @GetMapping("/{id}")
    public R<NursingProject> getInfo(@PathVariable("id") Long id) {
        return R.ok(nursingProjectService.selectNursingProjectById(id));
    }
}
