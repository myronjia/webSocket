
package com.jkhl.entrance.dao;


import com.unity.common.base.BaseDao;
import com.jkhl.entrance.entity.IdentitySwipe;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 身份信息
 * @author creator
 * 生成时间 2020-02-02 13:24:33
 */
public interface IdentitySwipeDao  extends BaseDao<IdentitySwipe>{


    @Select("<script> " +
            "SELECT device_no FROM identity_swipe WHERE is_deleted = 0 " +
            "<if test=\" park != null and park != '' \"> " +
            "   AND  park LIKE #{park}  GROUP BY device_no "   +
            " </if>" +
            "</script>")
    List<IdentitySwipe> selectDeviceNo(@Param("park") String park);
	
    @Select("<script> " +
            "SELECT * FROM identity_swipe WHERE is_deleted = 0 " +
            "<if test=\" park != null and park != '' \"> " +
            "   AND  park LIKE #{park} "  +
            " </if>" +
            "<if test=\" park != null and park != '' \"> " +
            "   AND  device_no = #{deviceNo} " + "  ORDER BY gmt_create DESC     " +
            " </if>" +
            " LIMIT #{start}, #{limit}  " +
            "</script>")
    List<IdentitySwipe> selectIdentitySwipeList(@Param("park") String park ,
                                                @Param("deviceNo") String deviceNo ,
                                                @Param("start") Long start,
                                                @Param("limit") Long limit);

    @Select("<script> " +
            " SELECT " + " COUNT(*) AS peopleNumber , " +
            " FROM_UNIXTIME(gmt_create/1000,'%Y-%m-%d %H')AS hourDate  " +
            " FROM " +
            " identity_swipe " + "WHERE 1=1 " +
            "<if test=\" park != null and park != '' \"> " +
            "   AND  park LIKE #{park}   "   +
            " </if>" +
            " AND FROM_UNIXTIME(gmt_create/1000,'%Y-%m-%d') = DATE_FORMAT(NOW(),'%Y-%m-%d') " +
            " GROUP BY " +
            " hourDate "
            + "ORDER BY " + 
            " hourDate " +
            "</script>")
    List<IdentitySwipe> selectIdentitySwipesCount(@Param("park") String park);



    @Select(
            " SELECT identity_information_number, is_admittance FROM identity_swipe WHERE park = #{park} AND gmt_create > #{gmtStart} AND gmt_create < #{gmtEnd}   "
           )
    List<IdentitySwipe> statistics(@Param("park") String park ,
                                   @Param("gmtStart") Long gmtStart,
                                   @Param("gmtEnd") Long gmtEnd);



}

