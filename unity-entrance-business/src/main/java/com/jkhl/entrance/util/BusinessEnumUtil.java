package com.jkhl.entrance.util;


import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

public class BusinessEnumUtil {

    private static Logger log = LoggerFactory.getLogger(BusinessEnumUtil.class);

    public static Map<String, Object> getAllBusinessEnumInfo(String pkg){

        Reflections reflections = new Reflections(pkg);

        Supplier<Map> mapSupplier = HashMap::new;
        Map<String, Object> infoMap = mapSupplier.get();
        Supplier<List> listSupplier = ArrayList::new;
        Set<Class<? extends Enum>> classes =  reflections.getSubTypesOf(Enum.class);
        try {
            for( Class clazz : classes){
                String key = clazz.getSimpleName().toLowerCase();
                if(key.equals("modultableenum")
                    || key.equals("recruitmenttypecodeenum")
                    || key.equals("filenamebyresourceenum")
                ){
                    continue;
                }
                Object[] instances = clazz.getEnumConstants();
                List list = listSupplier.get();
                for(Object instance : instances){
//                    Integer id = (Integer)clazz.getDeclaredMethod("getId").invoke(instance);
                    Object id = clazz.getDeclaredMethod("getId").invoke(instance);
                    String name = (String)clazz.getDeclaredMethod("getName").invoke(instance);
                    Map<String, Object> map = mapSupplier.get();
                    map.put("key", id);
                    map.put("value", name);
                    list.add(map);
                }
                infoMap.put(key, list.toArray());
            }
        }catch (Exception e){
            log.error("枚举扫描异常", e);
        }
        return infoMap;
    }

    public static void main(String[] args) {

        System.out.println(getAllBusinessEnumInfo("com.jkhl.project.enums"));
    }
}
