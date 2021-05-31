package com.jkhl.entrance.entity.generated;


import com.baomidou.mybatisplus.annotation.FieldStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

import java.util.Date;

/**
 * 身份信息
 *
 * @author creator
 * 生成时间 2020-02-02 13:24:33
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class mIdentitySwipe extends BaseEntity {


    /**
     * 人员id
     **/
    @CommentTarget("人员id")
    @TableField("id_identity_information")
    private Long idIdentityInformation;


    /**
     * 身份证号
     **/
    @CommentTarget("身份证号")
    @TableField("identity_information_number")
    private String identityInformationNumber;


    /**
     * 是否允许通行
     **/
    @CommentTarget("是否允许通行")
    @TableField("is_admittance")
    private Integer isAdmittance;


    /**
     * 出生日期
     **/
    @CommentTarget("出生日期")
    @TableField("birthday")
    private Date birthday;

    /**
     * 户口所在地
     **/
    @CommentTarget("户口所在地")
    @TableField("address")
    private String address;

    /**
     * 性别:flag:1 男,0 女
     **/
    @CommentTarget("性别")
    @TableField("gender")
    private Integer gender;

    /**
     * 有效期
     **/
    @CommentTarget("有效期")
    @TableField("valid_time")
    private String validTime;

    /**
     * 身份证头像
     **/
    @CommentTarget("身份证头像")
    @TableField("id_card_head")
    private String idCardHead;


    /**
     * 设备号
     **/
    @CommentTarget("设备号")
    @TableField("device_no")
    private String deviceNo;

    /**
     * 园区
     **/
    @CommentTarget("园区")
    @TableField("park")
    private String park;

    /**
     * 民族
     **/
    @CommentTarget("民族")
    @TableField("ethnic")
    private String ethnic;
    /**
     * 姓名
     **/
    @CommentTarget("姓名")
    @TableField("name")
    private String name;
    /**
     * 所属公司
     **/
    @CommentTarget("所属公司")
    @TableField(value = "department", strategy = FieldStrategy.IGNORED)
    private String department;
    /**
     * 楼号
     **/
    @CommentTarget("楼号")
    @TableField(value = "floor_number", strategy = FieldStrategy.IGNORED)
    private String floorNumber;


    /**
     * 房间号
     **/
    @CommentTarget("房间号")
    @TableField(value = "room_number", strategy = FieldStrategy.IGNORED)
    private String roomNumber;
}




