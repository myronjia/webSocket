
package com.jkhl.entrance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jkhl.entrance.dao.UserDepartmentDao;
import com.jkhl.entrance.entity.UserDepartment;
import com.unity.common.base.BaseServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: UserDepartmentService
 * date: 2018-12-12 20:21:07
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserDepartmentServiceImpl extends BaseServiceImpl<UserDepartmentDao, UserDepartment> implements IService<UserDepartment> {
    /**
     * 查询指定用户数据权限id集
     *
     * @param  userId 用户id
     * @return 数据权限id集
     */
    public List<Long> findDataPermissionIdListByUserId(Long userId){
        return baseMapper.findDataPermissionIdListByUserId(userId);
    }


}
