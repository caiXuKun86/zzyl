package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.CodeGenerator;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.domain.*;
import com.zzyl.nursing.dto.CheckInApplyDto;
import com.zzyl.nursing.mapper.*;
import com.zzyl.nursing.vo.CheckInConfigVo;
import com.zzyl.nursing.vo.CheckInDetailVo;
import com.zzyl.nursing.vo.CheckInElderVo;
import com.zzyl.nursing.vo.ElderFamilyVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.service.ICheckInService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * 入住Service业务层处理
 * 
 * @author alexis
 * @date 2026-03-24
 */
@Service
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements ICheckInService
{
    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private BedMapper bedMapper;

    @Autowired
    private ContractMapper contractMapper;
    @Autowired
    private CheckInConfigMapper checkInConfigMapper;

    /**
     * 查询入住
     * 
     * @param id 入住主键
     * @return 入住
     */
    @Override
    public CheckIn selectCheckInById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询入住列表
     * 
     * @param checkIn 入住
     * @return 入住
     */
    @Override
    public List<CheckIn> selectCheckInList(CheckIn checkIn)
    {
        return checkInMapper.selectCheckInList(checkIn);
    }

    /**
     * 新增入住
     * 
     * @param checkIn 入住
     * @return 结果
     */
    @Override
    public int insertCheckIn(CheckIn checkIn)
    {
        return save(checkIn) ? 1 : 0;
    }

    /**
     * 修改入住
     * 
     * @param checkIn 入住
     * @return 结果
     */
    @Override
    public int updateCheckIn(CheckIn checkIn)
    {
        return updateById(checkIn) ? 1 : 0;
    }

    /**
     * 批量删除入住
     * 
     * @param ids 需要删除的入住主键
     * @return 结果
     */
    @Override
    public int deleteCheckInByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除入住信息
     * 
     * @param id 入住主键
     * @return 结果
     */
    @Override
    public int deleteCheckInById(Long id)
    {
        return removeById(id) ? 1 : 0;
    }

    @Transactional
    @Override
    public void apply(CheckInApplyDto dto) {
        Long count = elderMapper.selectCount(
                new LambdaQueryWrapper<Elder>()
                        .eq(Elder::getIdCardNo, dto.getCheckInElderDto().getIdCardNo())
                        .in(Elder::getStatus, List.of(1, 4))
        );
        if(count > 0) {
            throw new BaseException("老人已入住");
        }
        Bed bed = bedMapper.selectBedById(dto.getCheckInConfigDto().getBedId());
        bed.setBedStatus(1);

        Elder elder = insertOrUpdateElder(bed, dto);

        String contractNo = "HT" + CodeGenerator.generateContractNumber();
        insertContrast(contractNo,elder,dto);

        // 新增入住信息
        CheckIn checkIn = insertCheckInInfo(elder, dto);

        // 新增入住配置信息
        insertCheckInConfig(checkIn.getId(), dto);

    }

    @Override
    public CheckInDetailVo detail(Long id) {
        CheckIn checkIn = checkInMapper.selectById(id);
        if(checkIn == null) {
            throw new BaseException("入住信息不存在");
        }
        Long elderId = checkIn.getElderId();
        Elder elder = elderMapper.selectById(elderId);
        if(elder == null) {
            throw new BaseException("老人信息不存在");
        }
        CheckInElderVo elderVo = new CheckInElderVo();

        BeanUtils.copyProperties(elder, elderVo);
        String remark = checkIn.getRemark();
        List<ElderFamilyVo> elderFamilyVos=null;
        if(remark != null) {
            elderFamilyVos = JSON.parseArray(remark, ElderFamilyVo.class);
        }



        CheckInConfigVo configVo = new CheckInConfigVo();
        CheckInConfig checkInConfig = checkInConfigMapper.selectOne(new LambdaQueryWrapper<CheckInConfig>().eq(CheckInConfig::getCheckInId, checkIn.getId()));
        BeanUtils.copyProperties(checkInConfig, configVo);
        configVo.setStartDate(checkIn.getStartDate());
        configVo.setEndDate(checkIn.getEndDate());
        configVo.setBedNumber(checkIn.getBedNumber());

        Contract contract = contractMapper.selectOne(new LambdaQueryWrapper<Contract>().eq(Contract::getElderId, elder.getId()));
        CheckInDetailVo detailVo = new CheckInDetailVo();
        detailVo.setCheckInElderVo(elderVo);
        detailVo.setElderFamilyVoList(elderFamilyVos);
        detailVo.setCheckInConfigVo(configVo);
        detailVo.setContract(contract);
        return detailVo;

    }

    private void insertCheckInConfig(Long checkInId, CheckInApplyDto checkInApplyDto) {
        CheckInConfig checkInConfig = new CheckInConfig();
        BeanUtils.copyProperties(checkInApplyDto.getCheckInConfigDto(), checkInConfig);
        checkInConfig.setCheckInId(checkInId);
        checkInConfigMapper.insert(checkInConfig);
    }

    private CheckIn insertCheckInInfo(Elder elder, CheckInApplyDto dto) {
        CheckIn checkIn = new CheckIn();
        checkIn.setElderId(elder.getId());
        checkIn.setElderName(elder.getName());
        checkIn.setIdCardNo(elder.getIdCardNo());
        checkIn.setNursingLevelName(dto.getCheckInConfigDto().getNursingLevelName());
        checkIn.setStartDate(dto.getCheckInConfigDto().getStartDate());
        checkIn.setEndDate(dto.getCheckInConfigDto().getEndDate());
        checkIn.setBedNumber(elder.getBedNumber());
        checkIn.setRemark(JSON.toJSONString(dto.getElderFamilyDtoList()));
        checkIn.setStatus(0);
        checkInMapper.insert(checkIn);
        return checkIn;
    }

    private void insertContrast(String contractNo, Elder elder, CheckInApplyDto dto) {
        Contract contract=new Contract();
        BeanUtils.copyProperties(dto.getCheckInContractDto(),contract);
        contract.setContractNumber(contractNo);
        contract.setElderId(elder.getId());
        contract.setElderName(elder.getName());
        // 状态、开始时间、结束时间
        // 签约时间小于等于当前时间，合同生效中
        LocalDateTime checkInStartTime = dto.getCheckInConfigDto().getStartDate();
        LocalDateTime checkInEndTime = dto.getCheckInConfigDto().getEndDate();
        Integer status = checkInStartTime.isAfter(LocalDateTime.now()) ? 0 : 1;
        contract.setStatus(status);
        contract.setStartDate(checkInStartTime);
        contract.setEndDate(checkInEndTime);
        contractMapper.insert(contract);

    }

    private Elder insertOrUpdateElder(Bed bed, CheckInApplyDto dto) {
        Elder elder = new Elder();
        BeanUtils.copyProperties(dto.getCheckInElderDto(), elder);
        elder.setBedId(bed.getId());
        elder.setBedNumber(bed.getBedNumber());

        // 查询老人信息，（身份证号、状态不为1）
        LambdaQueryWrapper<Elder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Elder::getIdCardNo, dto.getCheckInElderDto().getIdCardNo()).ne(Elder::getStatus, List.of(1,4));
        Elder elderDb =  elderMapper.selectOne(lambdaQueryWrapper);
        if(ObjectUtils.isNotEmpty(elderDb)) {
            // 修改
            elderMapper.updateById(elder);
        }else {
            // 新增
            elderMapper.insert(elder);
        }
        return elder;
    }
}
