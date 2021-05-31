
package com.jkhl.entrance.dao;

import com.jkhl.entrance.entity.User;
import com.unity.common.base.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 用户
 *
 * @author creator
 * 生成时间 2018-12-12 20:14:54
 */
@Transactional(rollbackFor = Exception.class)
@SuppressWarnings("unused")
public interface UserDao extends BaseDao<User> {



    List<User> getUserList(User user);

    /**
     * 获取用户信息及关联的组织信息
     *
     * @param userId 用户id
     * @return 用户信息及关联的组织信息
     * @author gengjiajia
     * @since 2019/03/21 14:42
     */
    @Select("SELECT " +
            " u.*, d. NAME AS department,d.dep_type AS depType " +
            "FROM " +
            " rbac_user u " +
            "LEFT JOIN rbac_department d ON u.id_rbac_department = d.id " +
            "WHERE u.id = #{userId}")
    User getUserInfoById(Long userId);

    /**
     * 根据登录账号获取用户信息及所属公司
     *
     * @param loginName 登录账号
     * @return 用户信息
     * @author gengjiajia
     * @since 2019/07/04 18:48
     */
    @Select(" SELECT u.*, d.name as department,d.dep_type AS depType " +
            " FROM rbac_user u " +
            " LEFT JOIN rbac_department d ON u.id_rbac_department = d.id " +
            " AND d.is_deleted = 0 " +
            " WHERE u.is_deleted = 0 " +
            " AND u.login_name = #{loginName}")
    User getUserInfoByLoginName(String loginName);

    /**
     * 根据指定条件统计用户总数量
     *
     * @param data 包含查询条件
     * @return 用户总数量
     * @author gengjiajia
     * @since 2019/07/08 10:10
     */
    long countUserTotalNum(Map<String, Object> data);

    /**
     * 根据指定条件统计用户总数量
     *
     * @param data 包含查询条件
     * @return 用户总数量
     * @author gengjiajia
     * @since 2019/07/08 10:10
     */
    List<User> findUserListByPage(Map<String, Object> data);

    /**
     * 返回某一个单位的用户id和name的集合
     * @param idRbacDepartment 单位id
    * @return java.util.List<com.unity.rbac.entity.User> 返回符合条件的用户集合
     * @author lifeihong
     * @date 2019/7/11 10:56
    */
    @Select("select id,`name` from rbac_user where is_deleted=0 and id_rbac_department = #{idRbacDepartment}")
    List<User> listUsersByDeptId(@Param("idRbacDepartment") Long idRbacDepartment);

    /**
     * 功能描述 根据单位id查询下面所有的用户
     * @param idRbacDepartment 单位id
     * @return java.util.List<com.unity.rbac.entity.User> 用户集合
     * @author gengzhiqiang
     * @date 2019/7/9 10:51
     */
    @Select(" select id,`name` from rbac_user where is_deleted=0 and id_rbac_department = #{idRbacDepartment} " +
            " order by CONVERT(name USING gbk)  ")
    List<User> listUsersByAllDeptId(@Param("idRbacDepartment") Long idRbacDepartment);

    /**
     * 查询某几个单位下的所有用户，is_deleted为1的也查询
     *
     * @param map {id,单位id的集合}
     * @return java.util.List<com.unity.rbac.entity.User> 返回符合条件的用户集合
     * @author lifeihong
     * @date 2019/7/10 16:58
     */
    @Select("<script>" +
            " select id,name from rbac_user " +
            " <if test='ids!=null'>" +
            " where id_rbac_department in " +
            " <foreach collection='ids' index='index' open='(' separator=',' close=')' item='item'> " +
            "       #{item} " +
            "   </foreach>" +
            " </if>" +
            "</script>")
    List<User> listAllInDepartment(Map<String, Object> map);

    /**
     * 查询某几个单位下的所有用户
     *
     * @param map {id,单位id的集合}
     * @return java.util.List<com.unity.rbac.entity.User> 返回符合条件的用户集合
     * @author lifeihong
     * @date 2019/7/10 16:58
     */
    @Select("<script>" +
            " select id,id_rbac_department from rbac_user where is_deleted=0 " +
            " <if test='ids!=null'>" +
            "  and id_rbac_department in " +
            " <foreach collection='ids' index='index' open='(' separator=',' close=')' item='item'> " +
            "       #{item} " +
            "   </foreach>" +
            " </if>" +
            " AND id_rbac_department !='' " +
            "</script>")
    List<User> listUserInDepartment(Map<String, Object> map);

    /**
     * 获取用户及关联的单位信息列表
     *
     * @return 用户及关联的单位信息列表
     * @author gengjiajia
     * @since 2019/07/12 18:58
     */
    @Select("SELECT " +
            " u.*, d.name AS department,d.dep_type AS depType " +
            "FROM " +
            " rbac_user u " +
            "INNER JOIN rbac_department d ON u.id_rbac_department = d.id " +
            "where u.is_deleted = 0")
    List<User> findUserAndDepartmentList();


    /**
     * 根据角色、单位集合获取用户id集合
     *
     * @param map 参数map
     * @return java.util.List<java.lang.Long>
     * @author JH
     * @date 2019/8/8 18:11
     */
    @Select("<script>" +
            " select a.id,a.id_rbac_department from rbac_user a" +
            " inner join rbac_m_user_role b" +
            " on a.id = b.id_rbac_user" +
            " where a.is_deleted = 0 " +
            " and b.is_deleted = 0 " +
            " and b.id_rbac_role = #{roleId} " +
            " and a.id_rbac_department in" +
            " <foreach collection='departmentIds' index='index' open='(' separator=',' close=')' item='item'> " +
            "       #{item} " +
            "   </foreach>" +
            "</script>")
    List<User> getUserIdsByRoleIdAndDepartmentIds(Map<String, Object> map);




    /**
     * 根据单位id修改对应用户状态
     *
     * @param  useStatus 是否启用 0 是 1 否（主要是考虑到用户这边用的字段叫 isLck）
     * @param idRbacDepartment 单位id
     * @author gengjiajia
     * @since 2019/09/24 10:51
     */
    @Update("UPDATE rbac_user SET is_lock = #{useStatus} WHERE id_rbac_department = #{idRbacDepartment} AND is_deleted = 0")
    @Transactional(rollbackFor = Exception.class)
    void updateIsLockByIdRbacDepartment(@Param("useStatus") Integer useStatus, @Param("idRbacDepartment") Long idRbacDepartment);
}

