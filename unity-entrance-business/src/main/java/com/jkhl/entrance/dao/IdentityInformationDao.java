
package com.jkhl.entrance.dao;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.jkhl.entrance.entity.IdentityInformation;
import com.jkhl.entrance.entity.IdentitySwipe;
import com.unity.common.base.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 身份信息
 * @author creator
 * 生成时间 2020-02-01 16:40:02
 */
public interface IdentityInformationDao  extends BaseDao<IdentityInformation>{


    @Select("select a.name,a.department,a.phone,a.room_number,b.gmt_modified gmtRecord,\n" +
            "a.company_contacts,a.company_contacts_phone,a.identity_information_number," +
            "a.is_flag,a.enter_beijing_time from identity_swipe b  \n" +
            "right join identity_information a  on a.id = b.id_identity_information\n" +
            "and a.is_deleted=0 and b.is_deleted=0  ${ew.customSqlSegment} \n" +
            "order by a.i_sort desc,gmtRecord ")
    List<IdentityInformation> getRecordForExport(@Param(Constants.WRAPPER) Wrapper wrapper);



    @Select(
            "SELECT\n" +
                    "\tPie.floor_number,\n" +
                    "\tPie.department,\n" +
                    "\tb.* \n" +
                    "FROM\n" +
                    "\tidentity_information Pie,\n" +
                    "( SELECT gmt_create, park, identity_information_number, is_admittance FROM identity_swipe WHERE  park = #{park} AND gmt_create > #{gmtStart} AND gmt_create < #{gmtEnd} ) b\n" +
                    "WHERE\n" +
                    "\tPie.identity_information_number = b.identity_information_number and Pie.is_deleted = 0;\n" +
                    "\t " )
    List<IdentitySwipe> pass (@Param("park") String park ,
                                        @Param("gmtStart") Long gmtStart,
                                        @Param("gmtEnd") Long gmtEnd);


    @Select("SELECT gmt_create FROM identity_swipe WHERE  park = #{park} AND gmt_create > #{gmtStart} AND gmt_create < #{gmtEnd} ")
    List<IdentitySwipe> reportForm (@Param("park") String park ,
                              @Param("gmtStart") Long gmtStart,
                              @Param("gmtEnd") Long gmtEnd);


    @Select("   SELECT gmt_create, identity_information_number, is_admittance,floor_number FROM identity_swipe WHERE  park = #{park} AND gmt_create > #{gmtStart} AND gmt_create < #{gmtEnd}  ")
    List<IdentitySwipe> floorPassInfo (@Param("park") String park ,
                              @Param("gmtStart") Long gmtStart,
                              @Param("gmtEnd") Long gmtEnd);

    @Select(" SELECT gmt_create,  identity_information_number, is_admittance FROM identity_swipe WHERE  park = #{park} AND gmt_create > #{gmtStart} AND gmt_create < #{gmtEnd}  " )
    List<IdentitySwipe> passInfo (@Param("park") String park ,
                              @Param("gmtStart") Long gmtStart,
                              @Param("gmtEnd") Long gmtEnd);


    @Select(
            "SELECT identity_information_number,park,floor_number,department,is_live    " +
                    "FROM   " +
                    "identity_information   " +
                    "WHERE  " +
                    " park = #{park} and is_deleted = 0 and is_live = 1 ")
    List<IdentityInformation> listByPark (@Param("park") String park );

    @Select(
            "SELECT count(*)   " +
                    "FROM   " +
                    "identity_information   " +
                    "WHERE  " +
                    " park = #{park} and is_deleted = 0 and is_live = 1 ")
    Integer liveCount (@Param("park") String park );

    @Select("SELECT\n" +
                    "\tdepartment,\n" +
                    "\tcount( * ) as count\n" +
                    "FROM\n" +
                    "\tidentity_information \n" +
                    "WHERE\n" +
                    "\tpark = #{park} \n" +
                    "\tAND is_deleted = 0 \n" +
                    "\tAND is_live = 1 \n" +
                    "GROUP BY\n" +
                    "\tdepartment")
    List<IdentityInformation> groupByDepartment (@Param("park") String park );


    @Select("SELECT\n" +
            "\tfloor_number,\n" +
            "\tcount( * ) as count\n" +
            "FROM\n" +
            "\tidentity_information \n" +
            "WHERE\n" +
            "\tpark = #{park} \n" +
            "\tAND is_deleted = 0 \n" +
            "\tAND is_live = 1 \n" +
            "GROUP BY\n" +
            "\tfloor_number")
    List<IdentityInformation> groupByFloor (@Param("park") String park );


    @Select("SELECT DISTINCT\n" +
            "\tidentity_information_number \n" +
            "FROM\n" +
            "\tidentity_information where is_deleted = 0")
    List<String> distinctNumber();




    /**
     * 每晚更新已入住的人员的最后刷卡时间
     *
     */
    @Update("UPDATE identity_information i \n" +
            "SET i.last_pass_time = (\n" +
            "SELECT\n" +
            "\tb.gmt_create \n" +
            "FROM\n" +
            "\tidentity_swipe b \n" +
            "WHERE\n" +
            "\tb.identity_information_number = i.identity_information_number \n" +
            "\tAND b.is_admittance = '1' \n" +
            "ORDER BY\n" +
            "\tb.gmt_create desc\n" +
            "\tLIMIT 1 \n" +
            "\t) \n" +
            "\twhere is_live = 1 " +
            "and is_deleted = 0")
    void updateLastPassTime();


    /**
     * 查询已入住且30天未刷卡人员
     */
    @Select("SELECT\n" +
            "\tidentity_information_number \n" +
            "FROM\n" +
            "\tidentity_information \n" +
            "WHERE\n" +
            "\tlast_pass_time < #{timeMillis} - ( 1000 * 60 * 60 * 24 * #{days} )\n" +
            " and is_live = 1" +
            " and is_deleted = 0 " +
            "\t")
    List<String> queryNotSwiped(@Param("timeMillis") Long timeMillis,
                         @Param("days") Integer days);


    /**
     * 将30天未刷卡的人员是否入住设为否
     * @param informationNumberList 需要锁定的人员身份证号
     */
    @Update("<script> " +
            "update identity_information set is_live = 0\n" +
            " where is_live = 1 " +
            " and is_deleted = 0 " +
            " and identity_information_number in " +
            "\t<foreach item='item' index='index' collection='informationNumberList' " +
            "\topen='(' separator=',' close=')'> " +
            "\t #{item} " +
            "\t</foreach> " +
            "</script>")
    void updateLocked(@Param("informationNumberList") List<String> informationNumberList);


}

