package pojos;

import com.unity.common.pojos.AuthUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 当前登录人信息
 * <p>
 * @author gengjiajia
 * @since 2018/12/21 13:45
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Customer extends AuthUser {
    /**
     * 按钮资源编码列表
     */
    public List<String> buttonCodeList;

    /**
     * 菜单资源编码列表
     */
    public List<String> menuCodeList;

    /**
     * 用户拥有的角色
     */
    public List<Long> roleList;

    /**
     * 数据权限id列表
     */
    public List<Long> dataPermissionIdList;

    /**
     * 是否超级管理员
     */
    public Integer isSuperAdmin;

    /**
     * 是否管理员
     */
    public Integer isAdmin;

    /**
     * 账号类型
     */
    public Integer userType;

    /**
     * 所属组织类型
     */
    public Integer depType;

    /**
     * 数据范围
     */
    public List<Integer> typeRangeList;
}