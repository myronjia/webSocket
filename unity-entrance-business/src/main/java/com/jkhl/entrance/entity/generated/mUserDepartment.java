package com.jkhl.entrance.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户机构权限
 * @author creator
 * 生成时间 2018-12-24 19:44:00
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mUserDepartment extends BaseEntity {


        
        /**
        * 编号_用户
        **/
        @TableField("id_rbac_user")
        private Long idRbacUser ;
        
        
        
        /**
        * 编号_组织机构
        **/
        @TableField("id_rbac_department")
        private Long idRbacDepartment ;
        
        

    public mUserDepartment(){}
}




