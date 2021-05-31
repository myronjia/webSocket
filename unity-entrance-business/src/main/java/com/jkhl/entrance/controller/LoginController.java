package com.jkhl.entrance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jkhl.entrance.entity.Department;
import com.jkhl.entrance.entity.User;
import com.jkhl.entrance.service.DepartmentServiceImpl;
import com.jkhl.entrance.service.UserServiceImpl;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.enums.PlatformTypeEnum;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.GsonUtils;
import com.unity.common.util.JsonUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pojos.Customer;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 统一登录
 *
 * <p>
 * create by gengjiajia at 2018/12/17 09:31
 * @author gengjiajia
 */
@Slf4j
@RestController
public class LoginController extends BaseWebController {


    private final UserServiceImpl userService;

    private final DepartmentServiceImpl departmentService;
    private final StringRedisTemplate stringRedisTemplate;


    public LoginController(UserServiceImpl userService, StringRedisTemplate stringRedisTemplate
            ,DepartmentServiceImpl departmentService
    ){
        this.userService = userService;
        this.stringRedisTemplate=stringRedisTemplate;
        this.departmentService=departmentService;
    }
    /**
     * 全局统一登录
     *
     * @param user 包含用户登录条件
     * @return code : 0 表示成功
     * -1001 登录名或密码有误
     * -1010 登录信息有误
     * -1011 用户不存在
     * -1013 缺少必要参数
     *
     * @author lm
     * @since
     */
    @PostMapping("sys/login")
    public Mono<ResponseEntity<SystemResponse<Object>>> sysLogin(@RequestBody User user) {
        log.info("=====《后台登录》login-body:" + GsonUtils.format(user));
        if (StringUtils.isEmpty(user.getLoginName()) || StringUtils.isEmpty(user.getPwd())) {
            return error(SystemResponse.FormalErrorCode.USERNAME_OR_PASSWORD_EMPTY, "请输入正确格式的用户名和密码");
        }
        if(user.getOs() == null || user.getOs() < 0 || user.getOs() > PlatformTypeEnum.SYSTEM.getType()){
            return error(SystemResponse.FormalErrorCode.LOGIN_DATA_ERR, "未获取到当前操作终端类型");
        }
        user.setOs(PlatformTypeEnum.WEB.getType());
        Map map = userService.unityLogin(user.getLoginName(), user.getPwd(), user.getOs());
        return success(map);
    }


    /**
     * 统一退出登录
     *
     * @return code 0 成功
     * @author lm
     * @since 2019/04/02 16:53
     */
    @PostMapping("sys/logout")
    public Mono<ResponseEntity<SystemResponse<Object>>> unityLogout(@RequestBody Map<String, String> body) {
        log.info("=====《全局统一退出登录》login-body:" + GsonUtils.format(body));
        Long os = Long.valueOf(body.get("os"));
        if(body.get("os") == null || os < 0 || os > PlatformTypeEnum.SYSTEM.getType()){
            return error(SystemResponse.FormalErrorCode.LOGIN_DATA_ERR, "未获取到当前操作终端类型");
        }
        userService.unityLogout(body.get("os"));
        return success("退出成功");
    }


    /**
     * 用户对应的园区
     *
     * @return code 0 成功
     * @since 22020年3月23日15:21:58
     */
    @PostMapping("user/department")
    public Mono<ResponseEntity<SystemResponse<Object>>> userDepartment() {
        Customer customer = LoginContextHolder.getRequestAttributes();
        LambdaQueryWrapper<Department> departmentew = new LambdaQueryWrapper<>();
        if(customer.getDataPermissionIdList()!=null &&customer.getDataPermissionIdList().size()>0){
            departmentew.in(Department::getId,customer.getDataPermissionIdList());
        }else {
            departmentew.in(Department::getId,customer.getIdRbacDepartment());
        }
        departmentew.orderByAsc(Department::getSort);
        List<Department> list = departmentService.list(departmentew);
        List<Map<String, Object>> maps = JsonUtil.ObjectToList(list,
                null
                , Department::getId, Department::getName, Department::getNotes
        );
        return success(maps);
    }


}
