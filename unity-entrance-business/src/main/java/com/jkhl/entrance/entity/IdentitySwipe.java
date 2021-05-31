package com.jkhl.entrance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import com.jkhl.entrance.entity.generated.mIdentitySwipe;

import java.util.Date;
import java.util.Map;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "identity_swipe")
public class IdentitySwipe extends mIdentitySwipe{
        
        
    /**
     * 1 今日 2本周 3本月
     * */
   @TableField(exist = false)
   private Integer type;
    /**
     * 出生年月日
     * */
   @TableField(exist = false)
   private String birthdayDate;
    /**
     * 创建日期
     * */
   @TableField(exist = false)
   private String gmtCreateDate;
    /**
     * 现居住地
     * */
   @TableField(exist = false)
   private String parkAddress;
    /**
     * 性别:flag:1 男,0 女
     * */
   @TableField(exist = false)
   private String genderTitle;


    /**
     * 统计信息
     * */
    @TableField(exist = false)
    Map<String,Object> statistics;


    @TableField(exist = false)
    private String dayOfMonth;


    @TableField(exist = false)
    private Integer hourOfDay;

    /**
     * 人数
     */
    @TableField(exist = false)
    private Integer peopleNumber;
    /**
     * 小时
     */
    @TableField(exist = false)
    private String hourDate;

    /**
     * 通行情况
     */
    @TableField(exist = false)
    private Object passInfo;
}

