
package com.jkhl.entrance.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jkhl.entrance.dao.UserDao;
import com.jkhl.entrance.entity.User;
import com.jkhl.entrance.util.IpAdrressUtil;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constants.ConstString;
import com.unity.common.constants.Constants;
import com.unity.common.enums.PlatformTypeEnum;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.util.GsonUtils;
import com.unity.common.util.RedisUtils;
import constant.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pojos.Customer;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户信息业务处理
 * <p>
 * ClassName: UserService
 * date: 2018-12-12 20:21:11
 *
 * @author creator
 * @since JDK 1.8
 */
@Slf4j
@Service
public class UserHelpServiceImpl extends BaseServiceImpl<UserDao, User> implements IService<User> {
    private final RedisUtils redisUtils;
    private final StringRedisTemplate stringRedisTemplate;

    public UserHelpServiceImpl(RedisUtils redisUtils, StringRedisTemplate stringRedisTemplate) {
        this.redisUtils = redisUtils;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    /**
     * 维护用户最后一次登录信息
     *
     * @param user 用户信息
     * @param os   所属平台
     * @author gengjiajia
     * @since 2019/07/27 15:03
     */
    @Async
    void updateLoginInfo(User user, Integer os, Date now, HttpServletRequest request) {
        user.setLastLoginIp(IpAdrressUtil.getIpAdrress(request));
        user.setLastLoginPlatform(os);
//        user.setSource(os);
        user.setGmtLoginLast(now);
        super.updateById(user);
    }

    /***
     *   保存用户信息到redis
     * @param user
     * @param os
     * @param tokenStr
     * @param customer
     */

    void saveCustomer(User user, Integer os, String tokenStr, Customer customer) {
        //token存入redis 生成key 规则 —> 固定头:登录终端:登录账号
        String key = RedisConstants.LOGINNAME2TOKEN.concat(RedisConstants.KEY_JOINER)
                .concat(os.toString())
                .concat(RedisConstants.KEY_JOINER)
                .concat(user.getLoginName());
        //用户信息有效期
        Integer day = os.equals(PlatformTypeEnum.ANDROID.getType()) || os.equals(PlatformTypeEnum.IOS.getType())
                ? Constants.APP_TOKEN_EXPIRE_DAY : Constants.PC_TOKEN_EXPIRE_DAY;
        //获取原token 用于清除登录信息
        String oldToken = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(oldToken)) {
            redisUtils.removeCurrentUserByToken(oldToken);
        }
        //存储本次的token 到redis
        stringRedisTemplate.opsForValue().set(key, tokenStr, day, TimeUnit.DAYS);
        // redis缓存用户信息
        customer.setId(user.getId());
        customer.setLoginName(user.getLoginName());
        customer.setEmail(user.getEmail());
        customer.setPhone(user.getPhone());
        customer.setPwd(user.getPwd());
        customer.setHeadPic(user.getHeadPic());
        customer.setName(user.getName());
        customer.setIdRbacDepartment(user.getIdRbacDepartment());
        customer.setNameRbacDepartment(user.getDepartment());
        customer.setUserType(user.getUserType());
        customer.setOs(os);
        redisUtils.putCurrentUserByToken(tokenStr, customer, day);
    }







}
