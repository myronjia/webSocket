package com.jkhl.entrance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jkhl.entrance.entity.generated.mUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@TableName(value = "rbac_user")
@EqualsAndHashCode(callSuper=false)
public class User extends mUser {

  @TableField(exist=false)
  private String oldPwd;

  /**
   * 公司名称
   **/
  @TableField(exist=false)
  private String department;

  @TableField(exist = false)
  private Integer depType;


  /**
   * 角色
   */
  @TableField(exist=false)
  private String roleIds;

  /**
   * 角色id
   */
  @TableField(exist=false)
  private Long roleId;

  /**
   * 操作终端
   */
  @TableField(exist=false)
  private Integer os;

  /**
   * 用户拥有的角色名称（多个以逗号拼接）
   */
  @TableField(exist=false)
  private String groupConcatRoleName;

  /**
   * 是否超管
   */
  @TableField(exist=false)
  private Integer superAdmin;

  /**
   * 验证码key
   */
  @TableField(exist=false)
  private String verifyKey;

  /**
   * 验证码值
   */
  @TableField(exist=false)
  private String verifyCode;

  public User(){}
}

