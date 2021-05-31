package com.jkhl.entrance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import com.jkhl.entrance.entity.generated.mIdentityInformation;


        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "identity_information")
public class IdentityInformation extends mIdentityInformation{



    @TableField(exist=false)
    private Long gmtRecord;



    /**
     * 是否高危字段
     */
    @TableField(exist=false)
    private String isFlagTitle;

    /**
     * 是否允许通行字段
     */
    @TableField(exist=false)
    private String IsAdmittanceTitle;


    /**
     * 是否允许通行字段
     */
    @TableField(exist=false)
    private String intoDays;



    /**
     * 开始时间
     * */
    @TableField(exist = false)
    private String startTime;

    /**
     * 结束时间
     * */
    @TableField(exist = false)
    private String endTime;


    @TableField(exist=false)
    private String provinceName ;

    /**
     * 1:查当天 2：查前七天
     * */
    @TableField(exist=false)
    private Integer type ;


    /**
     * 数量
     * */
    @TableField(exist=false)
    private Long count ;


}

