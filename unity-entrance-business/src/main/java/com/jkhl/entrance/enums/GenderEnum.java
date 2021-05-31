package com.jkhl.entrance.enums;

import lombok.AllArgsConstructor;

/**
 * @author wangbin
 * @since 2020/3/2
 */
@AllArgsConstructor
public enum GenderEnum {
    MAN(1, "男"),
    WOMAN(0, "女"),
            ;


    public static GenderEnum of(Integer id) {
        if (id.equals(MAN.getId())) {
            return MAN;
        }
        if (id.equals(WOMAN.getId())) {
            return WOMAN;
        }
        ;
        return null;
    }

    /**
     * 判断值是否在枚举中存在
     * @param id
     * @return
     */
    public static boolean exist(int id){
        boolean flag = false;
        for (GenderEnum e: GenderEnum.values()){
            if(e.getId()==id){
                flag = true;
                break;
            }
        }
        return flag;
    }

    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
