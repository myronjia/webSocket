
package com.jkhl.entrance.service;
import constant.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jkhl.entrance.dao.UserDao;
import com.jkhl.entrance.entity.Department;
import com.jkhl.entrance.entity.User;
import com.jkhl.entrance.entity.UserDepartment;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.base.SessionHolder;
import com.unity.common.constants.ConstString;
import com.unity.common.constants.RedisKeys;
import com.unity.common.enums.PlatformTypeEnum;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.*;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojos.Customer;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

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
public class UserServiceImpl extends BaseServiceImpl<UserDao, User> implements IService<User> {

    private final RedisUtils redisUtils;
    private final StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserDepartmentServiceImpl userDepartmentService;;
    @Autowired
    private DepartmentServiceImpl departmentService;;
    @Autowired
    private UserHelpServiceImpl userHelpService;
    public UserServiceImpl(RedisUtils redisUtils, StringRedisTemplate stringRedisTemplate) {
        this.redisUtils = redisUtils;
        this.stringRedisTemplate = stringRedisTemplate;
    }



    /**
     * 全局统一登录
     *
     * @param loginName 登录账号
     * @param pwd       登录密码
     * @param os        登录平台
     * @return 用户信息及权限信息
     * @author lm
     * @since 2020年2月19日15:57:37
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> unityLogin(String loginName, String pwd, Integer os) {
        Date now = new Date();
        //1.通过账号获取用户信息
        User user = baseMapper.getUserInfoByLoginName(loginName);
        if (user != null) {
            if (user.getIdRbacDepartment() == null) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.LOGIN_DATA_SATUS_ERR)
                        .message("暂未分配所属园区，请联系管理员")
                        .build();
            }
            //密码校验
            if (!Encryption.getEncryption(pwd, user.getLoginName()).equalsIgnoreCase(user.getPwd())) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.USERNAME_OR_PASSWORD_ERROR)
                        .message("用户名或密码错误")
                        .build();
            }
        } else {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.USERNAME_OR_PASSWORD_ERROR)
                    .message("用户不存在")
                    .build();
        }
        //返回数据总载体
        Map<String, Object> info = Maps.newHashMap();
        //生成token
        String tokenStr = EncryptUtil.generateToken(RedisKeys.CUSTOMER);
        info.put("token", tokenStr);
        Customer customer = new Customer();
        //数据权限
        List<Long> dataPermissionIdList =Lists.newArrayList();
        dataPermissionIdList= userDepartmentService.findDataPermissionIdListByUserId(user.getId());
        info.put(UserConstants.DATA_PERMISSIONID_LIST, dataPermissionIdList);
        customer.setDataPermissionIdList(dataPermissionIdList);
        //用户信息存入redis
        userHelpService.saveCustomer(user, os, tokenStr, customer);
        info.put("user", convert2Map(user));
        //维护登录信息
        userHelpService.updateLoginInfo(user, os, now, SessionHolder.getRequest());
        return info;
    }


    private Map<String,Object> convert2Map(User user){
        return JsonUtil.ObjectToMap(user,
                new String[]{"id", "loginName", "phone", "name", "notes", "idRbacDepartment", "department", "userType"},
                (m, u) -> {
                    m.put("gmtCreate", DateUtils.timeStamp2Date(u.getGmtCreate()));
                });
    }

    /**
     * 统一退出登录
     *
     * @author lm
     * @since 2020年3月23日16:05:43
     */
    public void unityLogout(String os) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        String key = RedisConstants.LOGINNAME2TOKEN.concat(RedisConstants.KEY_JOINER)
                .concat(os)
                .concat(RedisConstants.KEY_JOINER)
                .concat(customer.getLoginName());
        String token = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(token)) {
            redisUtils.removeCurrentUserByToken(token);
            stringRedisTemplate.delete(key);
        }
    }





}
