package com.jkhl.entrance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jkhl.entrance.entity.generated.mUserDepartment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@TableName(value = "rbac_m_user_department")
@EqualsAndHashCode(callSuper=false)
public class UserDepartment extends mUserDepartment {
  
  
}

