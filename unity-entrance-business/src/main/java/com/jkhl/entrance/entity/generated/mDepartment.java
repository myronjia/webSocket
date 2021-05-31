package com.jkhl.entrance.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织机构
 * @author creator
 * 生成时间 2018-12-24 19:43:58
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mDepartment extends BaseEntity {

    /**
     * 机构名称
     **/
    @TableField("name")
    private String name ;



    /**
     * 父Id
     **/
    @TableField("id_parent")
    private Long idParent ;



    /**
     * 树层次
     **/
    @TableField("i_level")
    private Integer level ;


    /**
     * 级次编码
     **/
    @TableField("gradation_code")
    private String gradationCode ;

    /**
     * 类型:status:1 公司 company,2 园区 park,3 供应商 supplier,4 部门 depart
     */
    @TableField("dep_type")
    private Integer depType;

    public mDepartment(){}
}




