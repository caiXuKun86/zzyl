package com.zzyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;

import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.domain.NursingPlan;
import com.zzyl.nursing.vo.NursingProjectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.NursingProjectMapper;
import com.zzyl.nursing.domain.NursingProject;
import com.zzyl.nursing.service.INursingProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 护理项目Service业务层处理
 *
 * @author alexis
 * @date 2025-06-02
 */
@Service
public class NursingProjectServiceImpl extends ServiceImpl<NursingProjectMapper, NursingProject> implements INursingProjectService {
    @Autowired
    private NursingProjectMapper nursingProjectMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    private final String REDIS_KEY = "cache:nursingProjects:all";

    /**
     * 查询护理项目
     *
     * @param id 护理项目主键
     * @return 护理项目
     */
    @Override
    public NursingProject selectNursingProjectById(Long id) {
        return getById(id);
    }

    /**
     * 查询护理项目列表
     *
     * @param nursingProject 护理项目
     * @return 护理项目
     */
    @Override
    public List<NursingProject> selectNursingProjectList(NursingProject nursingProject) {
        return nursingProjectMapper.selectNursingProjectList(nursingProject);
    }

    /**
     * 新增护理项目
     *
     * @param nursingProject 护理项目
     * @return 结果
     */
    @Override
    public int insertNursingProject(NursingProject nursingProject) {
        boolean save = save(nursingProject);
        cleanCache();
        return save ? 1 : 0;
    }

    /**
     * 修改护理项目
     *
     * @param nursingProject 护理项目
     * @return 结果
     */
    @Override
    public int updateNursingProject(NursingProject nursingProject) {
        boolean b = updateById(nursingProject);
        cleanCache();
        return b ? 1 : 0;
    }

    /**
     * 批量删除护理项目
     *
     * @param ids 需要删除的护理项目主键
     * @return 结果
     */
    @Override
    public int deleteNursingProjectByIds(Long[] ids) {

        boolean b = removeByIds(Arrays.asList(ids));
        cleanCache();
        return b ? 1 : 0;
    }

    /**
     * 删除护理项目信息
     *
     * @param id 护理项目主键
     * @return 结果
     */
    @Override
    public int deleteNursingProjectById(Long id) {
        boolean b = removeById(id);
        cleanCache();
        return b ? 1 : 0;
    }

    /**
     * 查询所有护理项目
     *
     * @return 护理项目列表
     */
    @Override
    public List<NursingProjectVo> getAll() {
        List<NursingProjectVo> list = (List<NursingProjectVo>) redisTemplate.opsForValue().get(REDIS_KEY);
        if (list != null) {
            return list;
        }
        list = nursingProjectMapper.getAll();
        redisTemplate.opsForValue().set(REDIS_KEY, list);
        return list;
    }

    public void cleanCache() {
        redisTemplate.delete(REDIS_KEY);
    }
}
