package com.zzyl.nursing.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zzyl.common.core.domain.model.LoginUser;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.framework.web.service.TokenService;
import com.zzyl.nursing.dto.UserLoginRequestDto;
import com.zzyl.nursing.service.WechatService;
import com.zzyl.nursing.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.FamilyMemberMapper;
import com.zzyl.nursing.domain.FamilyMember;
import com.zzyl.nursing.service.IFamilyMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 老人家属Service业务层处理
 * 
 * @author alexis
 * @date 2026-03-31
 */
@Service
public class FamilyMemberServiceImpl extends ServiceImpl<FamilyMemberMapper, FamilyMember> implements IFamilyMemberService
{
    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Autowired
    private WechatService wechatService;

    @Autowired
    private TokenService tokenService;

    static List<String> DEFAULT_NICKNAME_PREFIX = ListUtil.of(
            "生活更美好",
            "大桔大利",
            "日富一日",
            "好柿开花",
            "柿柿如意",
            "一椰暴富",
            "大柚所为",
            "杨梅吐气",
            "天生荔枝"
    );

    /**
     * 查询老人家属
     * 
     * @param id 老人家属主键
     * @return 老人家属
     */
    @Override
    public FamilyMember selectFamilyMemberById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询老人家属列表
     * 
     * @param familyMember 老人家属
     * @return 老人家属
     */
    @Override
    public List<FamilyMember> selectFamilyMemberList(FamilyMember familyMember)
    {
        return familyMemberMapper.selectFamilyMemberList(familyMember);
    }

    /**
     * 新增老人家属
     * 
     * @param familyMember 老人家属
     * @return 结果
     */
    @Override
    public int insertFamilyMember(FamilyMember familyMember)
    {
        return save(familyMember) ? 1 : 0;
    }

    /**
     * 修改老人家属
     * 
     * @param familyMember 老人家属
     * @return 结果
     */
    @Override
    public int updateFamilyMember(FamilyMember familyMember)
    {
        return updateById(familyMember) ? 1 : 0;
    }

    /**
     * 批量删除老人家属
     * 
     * @param ids 需要删除的老人家属主键
     * @return 结果
     */
    @Override
    public int deleteFamilyMemberByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除老人家属信息
     * 
     * @param id 老人家属主键
     * @return 结果
     */
    @Override
    public int deleteFamilyMemberById(Long id)
    {
        return removeById(id) ? 1 : 0;
    }

    @Override
    public LoginVo login(UserLoginRequestDto userLoginRequestDto) {

        String openid = wechatService.getOpenid(userLoginRequestDto.getCode());
        FamilyMember familyMember = lambdaQuery().eq(FamilyMember::getOpenId, openid).one();
        if(familyMember==null){
            familyMember = new FamilyMember();
            familyMember.setOpenId(openid);
        }
        String phone = wechatService.getPhone(userLoginRequestDto.getPhoneCode());
        familyMember.setPhone(phone);
        saveOrUpdateFamilyMember(familyMember);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", familyMember.getId());
        claims.put("nickName", familyMember.getName());
        String token = tokenService.createToken(claims);
        LoginVo loginVo = new LoginVo();
        loginVo.setNickName(familyMember.getName());
        loginVo.setToken(token);
        return loginVo;
    }

    private void saveOrUpdateFamilyMember(FamilyMember familyMember) {
        if(ObjectUtil.isNotEmpty(familyMember.getId())){
            familyMemberMapper.updateById(familyMember);
            return;
        }
        String nickName = DEFAULT_NICKNAME_PREFIX.get((int) (Math.random() * DEFAULT_NICKNAME_PREFIX.size()))
                + StringUtils.substring(familyMember.getPhone(), 7);

        familyMember.setName(nickName);
        save(familyMember);

    }
}
