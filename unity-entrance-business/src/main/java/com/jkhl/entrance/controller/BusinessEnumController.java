package com.jkhl.entrance.controller;

import com.jkhl.entrance.util.BusinessEnumUtil;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author wangbin
 * @since 2020/3/2
 */
@Controller
@RequestMapping("/util/enum")
public class BusinessEnumController extends BaseWebController {
    private final static String BUSS_ENUM_PKG = "com.jkhl.entrance.enums";
    private Map<String, Object> enumMap = null;
    @PostMapping("/get")
    public Mono<ResponseEntity<SystemResponse<Object>>> getEnumInfo(@RequestParam String key){
        if(null == enumMap){
            enumMap =  BusinessEnumUtil.getAllBusinessEnumInfo(BUSS_ENUM_PKG);
        }
        if(enumMap.containsKey(key.toLowerCase())){
            return success(enumMap.get(key.toLowerCase()));
        }
        return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "没有查询到对应枚举信息");
    }
}
