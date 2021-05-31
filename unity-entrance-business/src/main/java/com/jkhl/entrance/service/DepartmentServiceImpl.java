
package com.jkhl.entrance.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Lists;
import com.jkhl.entrance.dao.DepartmentDao;
import com.jkhl.entrance.entity.Department;
import com.jkhl.entrance.entity.UserDepartment;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Dic;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.BeanUtils;
import com.unity.common.util.DateUtils;
import com.unity.common.util.GsonUtils;
import com.unity.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.Relation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * 部门业务处理
 * <p>
 * ClassName: DepartmentService
 * date: 2018-12-12 20:21:06
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
@Slf4j
public class DepartmentServiceImpl extends BaseServiceImpl<DepartmentDao, Department> implements IService<Department> {


}
