package com.jkhl.entrance.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 身份信息
 * @author creator
 * 生成时间 2020-02-01 16:40:02
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mIdentityInformation extends BaseEntity{


        
        /**
        * 身份证号
        **/
        @CommentTarget("身份证号")
        @TableField("identity_information_number")
        private String identityInformationNumber ;
        
        
        
        /**
        * 姓名
        **/
        @CommentTarget("姓名")
        @TableField("name")
        private String name ;
        
        
        
        /**
        * 手机号
        **/
        @CommentTarget("手机号")
        @TableField("phone")
        private String phone ;
        
        
        
        /**
        * 园区
        **/
        @CommentTarget("园区")
        @TableField("park")
        private String park ;
        
        
        
        /**
        * 楼号
        **/
        @CommentTarget("楼号")
        @TableField("floor_number")
        private String floorNumber ;
        
        
        
        /**
        * 房间号
        **/
        @CommentTarget("房间号")
        @TableField("room_number")
        private String roomNumber ;
        
        
        
        /**
        * 所属公司
        **/
        @CommentTarget("所属公司")
        @TableField("department")
        private String department ;
        
        
        
        /**
        * 公司联系人
        **/
        @CommentTarget("公司联系人")
        @TableField("company_contacts")
        private String companyContacts ;
        
        
        
        /**
        * 公司联系人电话
        **/
        @CommentTarget("公司联系人电话")
        @TableField("company_contacts_phone")
        private String companyContactsPhone ;
        
        
        
        /**
        * 是否有湖北经历:flag:1 是,0 否
        **/
        @CommentTarget("是否有湖北经历:flag:1 是,0 否")
        @TableField("is_flag")
        private Integer isFlag ;


        /**
         * 是否允许通行
         **/
        @CommentTarget("是否允许通行")
        @TableField("is_admittance")
        private Integer isAdmittance ;



        /**
         * 入京时间
         **/
        @CommentTarget("入京时间")
        @TableField("enter_beiJing_time")
        private Date enterBeiJingTime ;


        /**
         * 人员类型，如：保洁、客户等
         **/
        @CommentTarget("人员类型")
        @TableField("person_type")
        private String personType ;


        /**
         * 是否入住:flag:1 是,0 否
         **/
        @CommentTarget("是否入住:flag:1 是,0 否")
        @TableField("is_live")
        private Integer isLive ;


        /**
         * 最后通过时间
         **/
        @CommentTarget("最后通过时间")
        @TableField("last_pass_time")
        private long lastPassTime ;




}




