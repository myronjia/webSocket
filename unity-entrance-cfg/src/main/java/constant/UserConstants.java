package constant;

/**
 * 用户 常量池
 * <p>
 * create by gengjiajia at 2019/01/23 16:22
 *
 * @author gengjiajia
 */
public class UserConstants {

    /**
     * 用户前端显示的默认密码
     */
    public static final String DEFALUT_PWD = "pd";
    /**
     * 重置密码
     */
    public static final String RESET_PWD = "cxfb@123456";
    /**
     * 尚亦城对接接口--秘钥
     */
    public static final String SECRET_KEY = "innovation";
    /**
     * 手机号
     */
    public static final String PHONE = "phone";
    /**
     * 名称
     */
    public static final String NAME = "name";
    /**
     * 系统
     */
    public static final String OS = "os";
    /**
     * ID标识
     */
    public static final String ID = "id";
    /**
     * 秘钥
     */
    public static final String SECRET = "secret";
    /**
     * 用户中心组织名称
     */
    public static final String DEPARTNAME = "departName";
    /**
     * 4位验证码
     */
    public static final String VERIFY_CODE = "verifyCode";
    /**
     * 短信验证码
     */
    public static final String VERIF_CODE = "verificationCode";
    /**
     * 短信验证码类型
     */
    public static final String VERIF_TYPE = "messageType";
    /**
     * 单次批量插入最大数量
     */
    public final static int MAX_BATCH_INSERT_NUM = 100;
    /**
     * 单次批量插入最大数量
     */
    public final static int PLUS_MAX_BATCH_INSERT_NUM = 1000;
    /**
     * 用户拥有的按钮资源权限编码
     */
    public static final String BUTTON_CODE_LIST = "buttonCodeList";
    /**
     * 用户拥有的菜单资源权限编码
     */
    public static final String DATA_PERMISSIONID_LIST = "dataPermissionIdList";

    /**
     * 用户拥有的数据权限
     */
    public static final String MENU_CODE_LIST = "menuCodeList";
    /**
     * 左侧菜单栏 地址
     */
    public static final String MENU_ID = "id";
    /**
     * 左侧菜单栏 地址
     */
    public static final String MENU_ID_PARENT = "idParent";
    /**
     * 左侧菜单栏 地址
     */
    public static final String MENU_PATH = "path";
    /**
     * 左侧菜单栏 icon
     */
    public static final String MENU_ICONCLS = "iconCls";
    /**
     * 左侧菜单栏 菜单名称
     */
    public static final String MENU_NAME = "name";
    /**
     * 左侧菜单栏 菜单页面路由
     */
    public static final String MENU_COMPONENT = "component";
    /**
     * 左侧菜单栏 子级菜单
     */
    public static final String MENU_CHILDREN = "children";
    /**
     * 左侧菜单栏 菜单上级节点值
     */
    public static final Long MENU_PARENT_ID = 1L;
    /**
     * 用户 —> 名称 部门、职位    最大限制20字符
     */
    public final static int USER_NAME_COMPANY_POSITION_MAX_LENGTH = 20;
    /**
     * 用户 —> 名称 部门、职位   最小 限制2字符
     */
    public final static int USER_NAME_COMPANY_POSITION_MIN_LENGTH = 2;
    /**
     * 备注   限制300字符
     */
    public final static int USER_NOTES_MAX_LENGTH = 255;
    /**
     * 用户注册默认身份id
     */
    public static final Long PC_DEFAULT_IDENTITY_ID = 1L;
    public static final Long MOBILE_DEFAULT_IDENTITY_ID = 2L;
    /**
     * 项目账号前缀
     */
    public static final String PROJECT_LOGIN_NAME_PREFIX = "@PJ_";
    /**
     * 管理员账号前缀
     */
    public static final String ADMIN_LOGIN_NAME_PREFIX = "@AD_";
    /**
     * 项目及管理员账号 最大限制20字符
     */
    public final static int PJ_AD_LOGIN_NAME_MAX_LENGTH = 24;
    /**
     * 组织类别
     */
    public final static String DEP_TYPE = "dep_type";
    /**
     * 测试地址 尚亦城账号注册
     */
    public final static String SYC_REGISTER_ACCOUNT_URL = "/app/login/registerAccount?phone=PHONE&channel=1";
    /**
     * 测试地址 尚亦城账号取消注册
     */
    public final static String SYC_DELETE_REGISTER_ACCOUNT_URL = "/app/login/delAccountFlag?phone=PHONE&channel=1";
    /**
     * 测试地址 尚亦城验证账号是否已登录接口
     */
    public final static String SYC_VERIFICATION_LOGIN_URL = "/app/login/verificationLogin?token=TOKEN";

    public static void main(String[] args) {
        // System.out.println(Encryption.getEncryption(UserConstants.SECRET_KEY,"customer7fc6f50d9f6c4703a8513e65f7d60f48"));
        int n =4;
        int r = 0;
        while (n != 1) {
            if (n % 2 == 0) {
                n = n / 2;
            } else {
                n = (3 * n + 1) / 2;
            }
            r = r + 1;
        }
        System.out.println(r);
   // }
       // System.out.println(getNum(4));
    }

    private static int getNum(int n){
        if(n<=0){
            return 0;
        }else if(n==1) {
            return reduceNum((3*n+1)/2,1);
        }else {
            return reduceNum(n, 0);
        }
    }

    private static int reduceNum(int n, int m) {
        if (n > 1) {
            m++;
            if (n % 2 == 0) {
                n = n / 2;
            } else {
                n = (3 * n + 1) / 2;
            }
            return reduceNum(n, m);
        } else {
            return m;
        }
    }


}
