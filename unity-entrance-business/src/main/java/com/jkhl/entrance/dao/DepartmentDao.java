
package com.jkhl.entrance.dao;


import com.jkhl.entrance.entity.Department;
import com.unity.common.base.BaseDao;
import org.springframework.transaction.annotation.Transactional;

/**
 * 组织机构
 *
 * @author creator
 * 生成时间 2018-12-12 20:14:49
 */
@Transactional(rollbackFor = Exception.class)
public interface DepartmentDao extends BaseDao<Department> {

//    /**
//     * 修改排序
//     *
//     * @param id     主键
//     * @param sortId 排序id
//     * @author JH
//     * @date 2019/9/9 14:09
//     */
//    @Update("update rbac_department set i_sort =#{sortId} where is_deleted = '0' and id = #{id} ")
//    void changeOrder(@Param("id") long id, @Param("sortId") long sortId);
//
//    /**
//     * 获取倒序排列的第一天单位id
//     *
//     * @param param 查询条件
//     * @return 单位id
//     * @author gengjiajia
//     * @since 2019/08/23 17:59
//     */
//    @Select("<script> " +
//            "SELECT " +
//            " id " +
//            "FROM " +
//            " rbac_department " +
//            "WHERE " +
//            " is_deleted = 0 " +
//            " <if test='depType != null'> " +
//            " AND dep_type = #{depType} " +
//            " </if> " +
//            " <if test='name != null'> " +
//            "AND name LIKE CONCAT('%', #{name}, '%') " +
//            " </if> " +
//            " <if test='useStatus != null'> " +
//            "AND use_status = #{useStatus} " +
//            " </if> " +
//            " <if test='sort == \"asc\"'> " +
//            "ORDER BY i_sort ASC " +
//            " </if> " +
//            " <if test='sort == \"desc\"'> " +
//            "ORDER BY i_sort DESC " +
//            " </if> " +
//            "LIMIT 0,1" +
//            "</script>")
//    Long getTheFirstDepartmentBySort(Map<String,Object> param);
}

