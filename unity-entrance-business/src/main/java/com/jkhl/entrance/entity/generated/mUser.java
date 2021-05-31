package com.jkhl.entrance.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户
 *
 * @author creator
 * 生成时间 2018-12-24 19:44:04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class mUser extends BaseEntity {

    /**
     * 登录名
     **/
    @TableField("login_name")
    private String loginName;

    /**
     * 密码
     **/
    @TableField("pwd")
    private String pwd;

    /**
     * 手机号
     **/
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     **/
    @TableField("email")
    private String email;

    /**
     * 昵称
     **/
    @TableField("nick_name")
    private String nickName;

    /**
     * 姓名
     **/
    @TableField("name")
    private String name;

    /**
     * 职位
     **/
    @TableField("position")
    private String position;

    /**
     * 头像
     **/
    @TableField("head_pic")
    private String headPic;

    /**
     * 微信openId
     **/
    @TableField("wx_open_id")
    private String wxOpenId;

    /**
     * 小程序openId
     **/
    @TableField("wxx_open_id")
    private String wxxOpenId;

    /**
     * 最后登录Ip
     **/
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 最后登录时间
     **/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @TableField("gmt_login_last")
    private Date gmtLoginLast;


    /**
     * 最后登录平台:status:1 web,2 android,3 ios,4 微信,5 小程序
     **/
    @TableField("last_login_platform")
    private Integer lastLoginPlatform;

    /**
     * 用户所属部门
     */
    @TableField(value = "id_rbac_department")
    private Long idRbacDepartment;

    /**
     * 是否锁定:flag:1 是,0 否
     */
    @TableField("is_lock")
    private Integer isLock;

    /**
     * 用户类型:
     */
    @TableField("user_type")
    private Integer userType;

    /**
     * 用户所属名称
     */
    @TableField(value = "name_rbac_department")
    private String nameRbacDepartment;
    public mUser() {
    }
}




