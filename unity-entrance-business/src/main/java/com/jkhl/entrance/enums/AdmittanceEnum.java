package com.jkhl.entrance.enums;

import lombok.AllArgsConstructor;


/**
 * 操作类型
 * @author macha
 * 生成时间 2019-08-07 18:39:37
 */
@AllArgsConstructor
public enum AdmittanceEnum {

    YES(1, "允许通行"),
        NO(0, "禁止通行"),
    ;
    
    
    public static AdmittanceEnum of(Integer id) {
        if (id.equals(YES.getId())) {
           return YES;
        }
            if (id.equals(NO.getId())) {
           return NO;
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
        for (AdmittanceEnum e: AdmittanceEnum.values()){
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



