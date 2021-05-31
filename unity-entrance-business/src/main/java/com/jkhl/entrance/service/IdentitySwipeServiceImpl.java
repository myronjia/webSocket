package com.jkhl.entrance.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jkhl.entrance.controller.MyWebSocket;
import com.jkhl.entrance.dao.IdentitySwipeDao;
import com.jkhl.entrance.entity.IdentityInformation;
import com.jkhl.entrance.entity.IdentitySwipe;
import com.jkhl.entrance.enums.AdmittanceEnum;
import com.jkhl.entrance.enums.GenderEnum;
import com.jkhl.entrance.enums.PlaceCodeEnum;
import com.jkhl.entrance.util.TimeUtil;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constants.ConstString;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;


/**
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IdentitySwipeServiceImpl extends BaseServiceImpl<IdentitySwipeDao, IdentitySwipe> {



    /**
     * 统计接口
     * @param entity 查询条件
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author JH
     * @since 2020/3/2 14:41
     */
    public Map<String,Object> statistics(IdentitySwipe entity) {

        long current = System.currentTimeMillis();
        List<IdentitySwipe> list = baseMapper.statistics(entity.getPark(),TimeUtil.getDailyStartTime(current, "GMT+8:00"),TimeUtil.getDailyEndTime(current, "GMT+8:00"));

        Map<String,Object> map = new HashMap<>();
        Map<Integer, List<IdentitySwipe>> isAdmittanceMap = list.stream().collect(Collectors.groupingBy(IdentitySwipe::getIsAdmittance, Collectors.toList()));
        //刷证人次
        map.put("cumulativeNumber",list.size());
        List<IdentitySwipe> passList = isAdmittanceMap.getOrDefault(1, Lists.newArrayList());
        List<IdentitySwipe> collect = passList.stream().filter(TimeUtil.distinctByKey(IdentitySwipe::getIdentityInformationNumber)).collect(Collectors.toList());
        //通行人数
        map.put("currentNum",collect.size());
        //禁行人次
        map.put("nonCurrentNum",isAdmittanceMap.getOrDefault(0, Lists.newArrayList()).size());
        Integer liveCount = informationService.liveCount(entity.getPark());

        //入园占比
        if(liveCount == 0 ) {
            map.put("percent",0);
        }else {
            map.put("percent",(collect.size() * 100 )/liveCount);
        }

        return map;

    }

    @Autowired
    private MyWebSocket myWebSocket;
    @Autowired
    private  IdentityInformationServiceImpl informationService;

    private Set<String> numberSet = new CopyOnWriteArraySet<>();

    public void saveIdentitySwipes(IdentitySwipe park) {
        super.saveOrUpdate(park);
//        IdentitySwipe entity = new IdentitySwipe();
//        entity.setType(1);
//        park.setStatistics(statistics(entity));
        if(CollectionUtils.isEmpty(numberSet)) {
            List<String> numbers = informationService.distinctNumber();
            numberSet.addAll(numbers);
        }
        if(!numberSet.contains(park.getIdentityInformationNumber())) {
            if(park.getIsAdmittance() == 1) {
                numberSet.add(park.getIdentityInformationNumber());
                IdentityInformation entity = new IdentityInformation();
                entity.setIdentityInformationNumber(park.getIdentityInformationNumber());
                entity.setPark(park.getPark());
                entity.setName(park.getName());
                entity.setFloorNumber(park.getFloorNumber());
                entity.setDepartment(park.getDepartment());
                entity.setIsLive(1);
                entity.setIsAdmittance(park.getIsAdmittance());
                entity.setLastPassTime(System.currentTimeMillis());
                informationService.save(entity);
            }
        }else{
            LambdaQueryWrapper<IdentityInformation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(IdentityInformation::getIdentityInformationNumber , park.getIdentityInformationNumber());
            wrapper.eq(IdentityInformation::getIsLive , 1);
            wrapper.orderByDesc(IdentityInformation::getGmtCreate);
            List<IdentityInformation> identityInformationList = informationService.list(wrapper);
            if(CollectionUtils.isNotEmpty(identityInformationList)){
                IdentityInformation identityInformation = identityInformationList.get(0);
                identityInformation.setLastPassTime(System.currentTimeMillis());
                informationService.updateById(identityInformation);
            }
        }
        park.setBirthdayDate(DateUtils.formatDate(park.getBirthday(), "yyyy'年'MM'月'dd'日'"));
        park.setGmtCreateDate(DateUtils.timeStamp2Date(park.getGmtCreate()));
        park.setGenderTitle( GenderEnum.of(park.getGender()).getName());
        String parkAddress = "";
        if (StringUtils.isNotBlank(park.getPark())){
            parkAddress = parkAddress+ PlaceCodeEnum.of(park.getPark()).getName();
        }
        if (StringUtils.isNotBlank(park.getFloorNumber())){
            parkAddress = parkAddress+park.getFloorNumber();
        }
        if (StringUtils.isNotBlank(park.getRoomNumber())){
            parkAddress = parkAddress+park.getRoomNumber();
        }
        park.setParkAddress(parkAddress);
        park.setAddress(str(park));
        park.setIdentityInformationNumber(idMask(park.getIdentityInformationNumber() , 6,8));
        park.setPassInfo(statistics(park));
        myWebSocket.sendInfo(JSON.toJSONString(park));
    }

    /**
     * 处理户籍
     * @param park 户籍信息
     * @return 省
     */
    private String str(IdentitySwipe park) {
        String address = park.getAddress();
        String address2 = "";
        if (StringUtils.isNotBlank(address)) {
            address2 = address.substring(0, 2);
            String neimenggu = "内蒙";
            String heilongjiang = "黑龙";
            if (neimenggu.equals(address2)) {
                address2 = "内蒙古";
            }
            if (heilongjiang.equals(address2)) {
                address2 = "黑龙江";
            }
        }
        return address2;
    }
    /**
     * 用户身份证号码的打码隐藏加星号加*
     * <p>18位和非18位身份证处理均可成功处理</p>
     * <p>参数异常直接返回null</p>
     *
     * @param idCardNum 身份证号码
     * @param front     需要显示前几位
     * @param end       需要显示末几位
     * @return 处理完成的身份证
     */
    public static String idMask(String idCardNum, int front, int end) {
        //身份证不能为空
        if (TextUtils.isEmpty(idCardNum)) {
            return null;
        }
        //需要截取的长度不能大于身份证号长度
        if ((front + end) > idCardNum.length()) {
            return null;
        }
        //需要截取的不能小于0
        if (front < 0 || end < 0) {
            return null;
        }
        //计算*的数量
        int asteriskCount = idCardNum.length() - (front + end);
        StringBuffer asteriskStr = new StringBuffer();
        for (int i = 0; i < asteriskCount; i++) {
            asteriskStr.append("*");
        }
        String regex = "(\\w{" + String.valueOf(front) + "})(\\w+)(\\w{" + String.valueOf(end) + "})";
        return idCardNum.replaceAll(regex, "$1" + asteriskStr + "$3");
    }

    public Map<String ,Object> selectIdentitySwipes(String park) {
        Map<String ,Object> map = new HashMap<>(8);
        List<IdentitySwipe> deviceNoList = baseMapper.selectDeviceNo(park);
        //每个公寓机子不多
        for (IdentitySwipe aDeviceNoList : deviceNoList) {
            if (aDeviceNoList == null) {
                continue;
            }
            String deviceNo = aDeviceNoList.getDeviceNo();
            if (!StringUtils.isNotBlank(deviceNo)) {
                continue;
            }
            List<IdentitySwipe> identitySwipeList = baseMapper.selectIdentitySwipeList(park, deviceNo, 0L, 5L);
            map.put(deviceNo, convert2List(identitySwipeList));
        }
        return map;
    }

    public List<Integer> selectIdentitySwipesCount(String park) {
        List<IdentitySwipe> deviceNoList = baseMapper.selectIdentitySwipesCount(park);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        List<Integer> list = new ArrayList<>();
        add:for (int i = 0; i <= hour; i ++){
            for (IdentitySwipe identitySwipe : deviceNoList) {
                if (StringUtils.isNotBlank(identitySwipe.getHourDate())) {
                    Integer hourDate = Integer.valueOf(identitySwipe.getHourDate().substring(11));
                    if (i == hourDate){
                        list.add(identitySwipe.getPeopleNumber());
                        continue add ;
                    }
                }
            }
            list.add(0);
        }
        return list;
    }

    /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<IdentitySwipe> list){

        return JsonUtil.<IdentitySwipe>ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                }
                ,IdentitySwipe::getId,
                IdentitySwipe::getIdIdentityInformation,
                IdentitySwipe::getGmtCreate,
                IdentitySwipe::getGmtModified,
                IdentitySwipe::getIdentityInformationNumber,
                IdentitySwipe::getIsAdmittance,
                IdentitySwipe::getBirthday,
                IdentitySwipe::getAddress,
                IdentitySwipe::getGender,
                IdentitySwipe::getValidTime,
                IdentitySwipe::getIdCardHead,
                IdentitySwipe::getDeviceNo,
                IdentitySwipe::getPark,
                IdentitySwipe::getEthnic,
                IdentitySwipe::getDepartment,
                IdentitySwipe::getName

        );
    }

    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m,IdentitySwipe entity){
        if(!StringUtils.isEmpty(entity.getCreator())) {
            if(entity.getCreator().indexOf(ConstString.SEPARATOR_POINT)>-1) {
                m.put("creator", entity.getCreator().split(ConstString.SPLIT_POINT)[1]);
            }
            else {
                m.put("creator", entity.getCreator());
            }
        }
        if(!StringUtils.isEmpty(entity.getEditor())) {
            if(entity.getEditor().indexOf(ConstString.SEPARATOR_POINT)>-1) {
                m.put("editor", entity.getEditor().split(ConstString.SPLIT_POINT)[1]);
            }
            else {
                m.put("editor", entity.getEditor());
            }
        }
        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtCreateDate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
        if (entity.getIsAdmittance() != null) {
            m.put("isAdmittanceTitle", AdmittanceEnum.of(entity.getIsAdmittance()).getName());
        }
        if (entity.getGender() != null) {
            m.put("genderTitle", GenderEnum.of(entity.getGender()).getName());
        }
        if (StringUtils.isNotBlank(entity.getPark())) {
            String parkAddress = "";
            m.put("parkTitle", PlaceCodeEnum.of(entity.getPark()).getName());
            parkAddress = parkAddress+ PlaceCodeEnum.of(entity.getPark()).getName();
            if (StringUtils.isNotBlank(entity.getFloorNumber())){
                parkAddress = parkAddress+entity.getFloorNumber();
            }
            if (StringUtils.isNotBlank(entity.getRoomNumber())){
                parkAddress = parkAddress+entity.getRoomNumber();
            }
            m.put("parkAddress", parkAddress);
        }
        if (entity.getBirthday() != null) {
            m.put("birthdayDate", DateUtils.formatDate(entity.getBirthday(), "yyyy'年'MM'月'dd'日'"));
            m.put("birthday", DateUtils.formatDate(entity.getBirthday(), "yyyy'-'MM'-'dd"));
        }
        if (StringUtils.isNotBlank(entity.getAddress())){
            m.put("address", str(entity));
        }
        if (StringUtils.isNotBlank(entity.getIdentityInformationNumber())){
            m.put("identityInformationNumber",idMask(entity.getIdentityInformationNumber() , 6,8));
        }
    }





}
