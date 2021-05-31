package com.jkhl.entrance.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.SessionHolder;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.enums.FlagEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.GsonUtils;
import com.unity.common.util.RedisUtils;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gengjiajia
 */
@Controller
@Slf4j
@RequestMapping("home")
public class HomeController extends BaseWebController {

    /**
     * 跳转到首页
     *
     * @return 首页地址
     */
    @RequestMapping("index")
    public String index(Model model) {
        try {

            return "index";
        } catch (Exception e) {
            return "login";
        }
    }

    @RequestMapping("aaa")
    @ResponseBody
    public String aaaa(Model model) {
        return "ddddd";
    }


}
