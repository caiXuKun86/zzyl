package com.zzyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;

import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.vo.NursingLevelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.NursingLevelMapper;
import com.zzyl.nursing.domain.NursingLevel;
import com.zzyl.nursing.service.INursingLevelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.ObjectUtils;

/**
 * 护理等级Service业务层处理
 *
 * @author alexis
 * @date 2025-06-02
 */
@Service
public class NursingLevelServiceImpl extends ServiceImpl<NursingLevelMapper, NursingLevel> implements INursingLevelService {
    @Autowired
    private NursingLevelMapper nursingLevelMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String CACHE_KEY_PREFIX = "nursingLevel:all";

    /**
     * 查询护理等级
     *
     * @param id 护理等级主键
     * @return 护理等级
     */
    @Override
    public NursingLevel selectNursingLevelById(Long id) {
        return getById(id);
    }

    /**
     * 查询护理等级列表
     *
     * @param nursingLevel 护理等级
     * @return 护理等级
     */
    @Override
    public List<NursingLevel> selectNursingLevelList(NursingLevel nursingLevel) {
        return nursingLevelMapper.selectNursingLevelList(nursingLevel);
    }


    private void deleteCache() {
        // 删除缓存
        redisTemplate.delete(CACHE_KEY_PREFIX);
    }

    @Override
    public int insertNursingLevel(NursingLevel nursingLevel) {
        int flag = nursingLevelMapper.insertNursingLevel(nursingLevel);
        // 删除缓存
        deleteCache();
        return flag;
    }

    /**
     * 修改护理等级
     *
     * @param nursingLevel 护理等级
     * @return 结果
     */
    @Override
    public int updateNursingLevel(NursingLevel nursingLevel) {
        int flag =  nursingLevelMapper.updateNursingLevel(nursingLevel);
        deleteCache();
        return flag;
    }

    /**
     * 批量删除护理等级
     *
     * @param ids 需要删除的护理等级主键
     * @return 结果
     */
    @Override
    public int deleteNursingLevelByIds(Long[] ids) {

        int flag = nursingLevelMapper.deleteNursingLevelByIds(ids);
        deleteCache();
        return flag;
    }

    /**
     * 删除护理等级信息
     *
     * @param id 护理等级主键
     * @return 结果
     */
    @Override
    public int deleteNursingLevelById(Long id) {
        int flag = nursingLevelMapper.deleteNursingLevelById(id);
        deleteCache();
        return flag;
    }

    /**
     * 查询护理等级Vo列表
     *
     * @param nursingLevel 条件
     * @return 结果
     */
    @Override
    public List<NursingLevelVo> selectNursingLevelVoList(NursingLevel nursingLevel) {
        return nursingLevelMapper.selectNursingLevelVoList(nursingLevel);
    }

    @Override
    public List<NursingLevel> listAll() {
        List<NursingLevel> list = (List<NursingLevel>) redisTemplate.opsForValue().get(CACHE_KEY_PREFIX);
        if (!ObjectUtils.isEmpty(list)) {
            return list;
        }
        list = lambdaQuery().eq(NursingLevel::getStatus, 1).list();
        redisTemplate.opsForValue().set(CACHE_KEY_PREFIX, list);
        return list;

    }
}
