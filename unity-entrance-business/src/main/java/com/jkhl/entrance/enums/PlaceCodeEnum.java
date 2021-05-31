package com.jkhl.entrance.enums;

import lombok.AllArgsConstructor;

/**
 * 使用状态
 * @author jzw
 * 生成时间 2020年3月2日11:04:56
 */
@AllArgsConstructor
public enum PlaceCodeEnum {

    QNGY("QNGY", "青年公寓"),
    YKGY("YKGY", "永康公寓"),
    YCCF("YCCF", "亦城财富"),
    KCJY("KCJY", "科创家园"),
    JHCY("JHCY", "经海产业园"),
    SWYY("SWYY", "生物医药园"),
    CXDS("CXDS", "创新大厦"),
    LSDS("LSDS", "隆盛大厦"),
    YDGG("YDGG", "移动硅谷置业"),
    YCGY("YCGY", "永昌工业"),
    YCKJ("YCKJ", "亦城科技中心"),
    SZGC("SZGC", "数字工厂"),
    CSYQ("CSYQ", "测试园区"),
    ;


    public static PlaceCodeEnum of(String modul) {
        for (PlaceCodeEnum value : PlaceCodeEnum.values()) {
            if (value.getId().equals(modul)) {
                return value;
            }
        }
        return null;
    }
    
    /**
     * 判断值是否在枚举中存在
     * @param id
     * @return
     */
    public static boolean exist(String id) {
        boolean flag = false;
        for (PlaceCodeEnum e : PlaceCodeEnum.values()) {
            if (e.getId().equals(id)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
    
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setCode(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}



