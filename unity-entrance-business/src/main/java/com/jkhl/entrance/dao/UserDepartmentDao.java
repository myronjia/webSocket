
package com.jkhl.entrance.dao;

import com.jkhl.entrance.entity.UserDepartment;
import com.unity.common.base.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户机构权限
 *
 * @author creator
 * 生成时间 2018-12-12 20:14:51
 */
public interface UserDepartmentDao extends BaseDao<UserDepartment> {

    /**
     * 查询指定用户数据权限id集
     *
     * @param  userId 用户id
     * @return 数据权限id集
     */
    @Select("SELECT DISTINCT du.id_rbac_department FROM  rbac_user u  " +
            " INNER JOIN  rbac_m_user_department du  ON u.id = du.id_rbac_user " +
            "WHERE u.is_deleted = 0 AND du.is_deleted = 0 " +
            "AND u.id = #{userId}")

    List<Long> findDataPermissionIdListByUserId(Long userId);

}

