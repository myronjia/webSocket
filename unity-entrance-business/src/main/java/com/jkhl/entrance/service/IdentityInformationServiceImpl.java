
package com.jkhl.entrance.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import com.jkhl.entrance.entity.*;
import com.jkhl.entrance.util.TimeUtil;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.ui.Operate;
import com.unity.common.ui.Rule;
import com.unity.common.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jkhl.entrance.dao.IdentityInformationDao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ClassName: IdentityInformationService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2020-02-01 16:40:02
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IdentityInformationServiceImpl extends BaseServiceImpl<IdentityInformationDao, IdentityInformation> {



    public List<IdentityInformation> getRecordForExport(Wrapper wrapper) {

        return baseMapper.getRecordForExport(wrapper);
    }

    public LambdaQueryWrapper<IdentityInformation> getNumSql(LambdaQueryWrapper<IdentityInformation> ex, List<Rule> list) {

        for (Rule r : list) {
            String applyStr = " a.identity_information_number =' " + r.getData() + "'";
            /*if(Operate.bw.getId().equals(r.getOp())){
                applyStr += "  like"  ;
                applyStr +=   "  '%"+r.getData()+"%' ";
            }else if(Operate.eq.getId().equals(r.getOp())){
                applyStr += "  = " ;
                applyStr +=   "  '"+r.getData()+"' ";
            }*/

            ex.apply(applyStr);
        }
        return ex;
    }

    public LambdaQueryWrapper<IdentityInformation> getTimeSql(LambdaQueryWrapper<IdentityInformation> ex, String paraDay) {
        String day = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        if ("one".equals(paraDay)) {
            day = dateFormat.format(new Date());
        }
        if ("two".equals(paraDay)) {

            calendar.set(Calendar.DATE, -1);
            day = dateFormat.format(DateUtils.addDays(new Date(), -1));
        }
        if ("three".equals(paraDay)) {

            day = dateFormat.format(DateUtils.addDays(new Date(), -2));
        }

        String applyStr = " FROM_UNIXTIME(b.gmt_modified/1000, '%Y-%m-%d') ='" + day + "'";

        ex.apply(applyStr);

        return ex;
    }


    @Autowired
    IdentitySwipeServiceImpl swipeService;


    /**
     * 各公司人员构成
     *
     * @param entity 查询条件
     * @return java.lang.Object
     * @author JH
     * @since 2020/3/3 19:32
     */
    public Pie deptCons(IdentityInformation entity) {
        List<IdentityInformation> list= baseMapper.groupByDepartment(entity.getPark());

        Pie pie = new Pie();
        List<Series> seriesData = Lists.newArrayList();

        List<IdentityInformation> copyList = Lists.newArrayList();
        IdentityInformation noDept = new IdentityInformation();
        noDept.setDepartment("未知公司");
        noDept.setCount(0L);
        for (IdentityInformation info : list) {
            if(StringUtils.isBlank(info.getDepartment())) {
                Long count = noDept.getCount();
                count = count + info.getCount();
                noDept.setCount(count);
            } else {
                copyList.add(info);
            }
        }
        copyList.add(noDept);

        for (IdentityInformation info : copyList) {
            Series series = new Series();
            series.setName(info.getDepartment());
            series.setValue(info.getCount());
            seriesData.add(series);
        }

        List<Series> sortList = seriesData.stream().sorted(Comparator.comparing(Series::getValue).reversed()).collect(Collectors.toList());
        List<Series> res = Lists.newArrayList();
        long sum = 0;
        for (int i = 0; i < sortList.size(); i++) {
            if (i < 10) {
                res.add(sortList.get(i));
            } else {
                sum += sortList.get(i).getValue();
            }
        }
        if(sum > 0) {
            Series series = new Series();
            series.setName("其他");
            series.setValue(sum);
            res.add(series);
        }
        pie.setSeriesData(res);
        List<String> collect = res.stream().map(Series::getName).collect(Collectors.toList());
        pie.setLegendData(collect);

        return pie;
    }


    /**
     * 各楼栋人员构成
     *
     * @param entity 查询条件
     * @return java.lang.Object
     * @author JH
     * @since 2020/3/3 19:32
     */
    public Pie floorCons(IdentityInformation entity) {

        List<IdentityInformation> list = baseMapper.groupByFloor(entity.getPark());
        list.forEach(n->n.setFloorNumber(correctFloor(n.getFloorNumber())));
        Pie pie = new Pie();
        List<Series> seriesData = Lists.newArrayList();
        List<IdentityInformation> copyList = Lists.newArrayList();
        IdentityInformation noFloor = new IdentityInformation();
        noFloor.setFloorNumber("未知楼号");
        noFloor.setCount(0L);
        for (IdentityInformation info : list) {
            if("未知楼号".equals(info.getFloorNumber())) {
                Long count = noFloor.getCount();
                count = count + info.getCount();
                noFloor.setCount(count);
            } else {
                copyList.add(info);
            }
        }
        copyList.add(noFloor);

        for (IdentityInformation info : copyList) {

            Series series = new Series();
            series.setName(info.getFloorNumber());
            series.setValue(info.getCount());
            seriesData.add(series);
        }

        List<Series> sortList = seriesData.stream().sorted(Comparator.comparing(Series::getValue).reversed()).collect(Collectors.toList());
        List<Series> res = Lists.newArrayList();
        long sum = 0;
        for (int i = 0; i < sortList.size(); i++) {
            if (i < 10) {
                res.add(sortList.get(i));
            } else {
                sum += sortList.get(i).getValue();
            }
        }
        if(sum > 0) {
            Series series = new Series();
            series.setName("其他");
            series.setValue(sum);
            res.add(series);
        }
        pie.setSeriesData(res);
        List<String> collect = res.stream().map(Series::getName).collect(Collectors.toList());
        pie.setLegendData(collect);


        return pie;
    }


    /**
     * 各省份人员构成
     *
     * @param entity 查询条件
     * @return java.lang.Object
     * @author JH
     * @since 2020/3/3 19:32
     */
    public Pillar provinceCons(IdentityInformation entity) {
        Map<String, String> provinceMap = getProvinceMap();
        List<IdentityInformation> list = baseMapper.listByPark(entity.getPark());

        list.stream().filter(n -> StringUtils.isNoneBlank(n.getIdentityInformationNumber())).forEach(n -> {
            String identityInformationNumber = n.getIdentityInformationNumber();
            String substring = identityInformationNumber.substring(0, 2);
            n.setProvinceName(provinceMap.get(substring));
        });
        //去除空身份证
        List<IdentityInformation> filterList = list.stream().filter(n -> StringUtils.isNoneBlank(n.getIdentityInformationNumber())).collect(Collectors.toList());
        filterList.forEach(n -> {
            String identityInformationNumber = n.getIdentityInformationNumber();
            String substring = identityInformationNumber.substring(0, 2);
            n.setProvinceName(provinceMap.getOrDefault(substring, ""));
        });
        //key 省份名 value 次数
        Map<String, Long> collect = filterList.stream().collect(Collectors.groupingBy(IdentityInformation::getProvinceName, Collectors.counting()));
        List<Series> serieList = Lists.newArrayList();
        Set<String> strings = collect.keySet();
        for (String s : strings) {
            Series series = new Series();
            series.setName(s);
            series.setValue(collect.get(s));
            serieList.add(series);
        }
        List<Series> sortList = serieList.stream().sorted(Comparator.comparing(Series::getValue).reversed()).collect(Collectors.toList());
        Pillar pillar = new Pillar();

        List<String> nameList = Lists.newArrayList();
        List<Long> dataList = Lists.newArrayList();

        long sum = 0;
        for (int i = 0; i < sortList.size(); i++) {
            if (i < 5) {
                nameList.add(sortList.get(i).getName());
                dataList.add(sortList.get(i).getValue());
            } else {
                sum += sortList.get(i).getValue();
            }
        }
        if(sum >0) {
            nameList.add("其他");
            dataList.add(sum);
        }
        pillar.setX(Lists.newArrayList(0, 0.01));
        pillar.setY(nameList);
        pillar.setSeriesData(dataList);

        return pillar;


    }


    private Map<String, String> getProvinceMap() {
        Map<String, String> provinceMap = new HashMap<>();
        provinceMap.put("11", "北京");
        provinceMap.put("12", "天津");
        provinceMap.put("13", "河北");
        provinceMap.put("14", "山西");
        provinceMap.put("15", "内蒙古");
        provinceMap.put("21", "辽宁");
        provinceMap.put("22", "吉林");
        provinceMap.put("23", "黑龙江");
        provinceMap.put("31", "上海");
        provinceMap.put("32", "江苏");
        provinceMap.put("33", "浙江");
        provinceMap.put("34", "安徽");
        provinceMap.put("35", "福建");
        provinceMap.put("36", "江西");
        provinceMap.put("37", "山东");
        provinceMap.put("41", "河南");
        provinceMap.put("42", "湖北");
        provinceMap.put("43", "湖南");
        provinceMap.put("44", "广东");
        provinceMap.put("45", "广西");
        provinceMap.put("46", "海南");
        provinceMap.put("50", "重庆");
        provinceMap.put("51", "四川");
        provinceMap.put("52", "贵州");
        provinceMap.put("53", "云南");
        provinceMap.put("54", "西藏");
        provinceMap.put("61", "陕西");
        provinceMap.put("62", "甘肃");
        provinceMap.put("63", "青海");
        provinceMap.put("64", "宁夏");
        provinceMap.put("65", "新疆");
        provinceMap.put("71", "台湾  ");
        provinceMap.put("81", "香港");
        provinceMap.put("82", "澳门");
        return provinceMap;

    }


    /**
     * 人员通行情况统计表
     *
     * @param entity 查询条件
     * @return java.lang.Object
     * @author JH
     * @since 2020/3/3 19:32
     */
    public Map<String, Object> passInfo(IdentityInformation entity) {


        //先拿到数据
        long gmtStart = TimeUtil.getFirstTimeInDay(entity.getStartTime());
        long gmtEnd = TimeUtil.getLastTimeInDay(entity.getEndTime());
        List<IdentitySwipe> list = baseMapper.passInfo(entity.getPark(), gmtStart, gmtEnd);

        Map<String, Object> res = new HashMap<>();

        list.forEach(n -> {
            String dayStr = DateUtils.timeStamp2Date(n.getGmtCreate(), "yyyy-MM-dd");
            n.setDayOfMonth(dayStr);
        });
        Map<String, List<IdentitySwipe>> dayMap = list.stream().collect(Collectors.groupingBy(IdentitySwipe::getDayOfMonth, Collectors.toList()));
        List<String> days = TimeUtil.getDays(entity.getStartTime(), entity.getEndTime());
        //legendData
        String[] strArr = {"允许通过人数", "禁止通过人数"};
        res.put("legendData", strArr);
        //x轴数据
        res.put("xData", days);
        //通过
        List<Integer> pass = new ArrayList<>();
        //未通过
        List<Integer> nopass = new ArrayList<>();
        for (String str : days) {
            List<IdentitySwipe> aaa = dayMap.get(str);

            if (CollectionUtils.isNotEmpty(aaa)) {
                //按照身份证去重
                    List<IdentitySwipe> swipeList = aaa.stream().filter(TimeUtil.distinctByKey(IdentitySwipe::getIdentityInformationNumber)).collect(Collectors.toList());
                List<IdentitySwipe> passList = swipeList.stream().filter(n -> n.getIsAdmittance() == 1).collect(Collectors.toList());
                List<IdentitySwipe> noPassList = swipeList.stream().filter(n -> n.getIsAdmittance() == 0).collect(Collectors.toList());

                pass.add(passList.size());
                nopass.add(noPassList.size());

            } else {
                pass.add(0);
                nopass.add(0);
            }
        }
        List<PassSeries> passSeriesList = Lists.newArrayList();

        PassSeries passSeries = new PassSeries();
        passSeries.setName("允许通过人数");
        passSeries.setData(pass);
        passSeriesList.add(passSeries);
        PassSeries noPassSeries = new PassSeries();
        noPassSeries.setName("禁止通过人数");
        noPassSeries.setData(nopass);
        passSeriesList.add(noPassSeries);
        res.put("passSeriesList", passSeriesList);
        return res;

    }


    /**
     * 楼栋通行情况统计表
     *
     * @param entity 查询条件
     * @return java.lang.Object
     * @author JH
     * @since 2020/3/3 19:32
     */
    public Map<String, Object> floorPassInfo(IdentityInformation entity) {

        //先拿到数据
        long gmtStart = TimeUtil.getFirstTimeInDay(entity.getStartTime());
        long gmtEnd = TimeUtil.getLastTimeInDay(entity.getEndTime());
        List<IdentitySwipe> list = baseMapper.floorPassInfo(entity.getPark(), gmtStart, gmtEnd);

        Map<String, Object> res = new HashMap<>();

        list.forEach(n -> {
            String dayStr = DateUtils.timeStamp2Date(n.getGmtCreate(), "yyyy-MM-dd");
            n.setDayOfMonth(dayStr);
            n.setFloorNumber(correctFloor(n.getFloorNumber()));
        });
        List<String> days = TimeUtil.getDays(entity.getStartTime(), entity.getEndTime());
        //先按楼号、再按日期分组
        Map<String, Map<String, Long>> collect = list.stream().collect(Collectors.groupingBy(IdentitySwipe::getFloorNumber, Collectors.groupingBy(IdentitySwipe::getDayOfMonth, Collectors.counting())));
        Set<String> floorSets = collect.keySet();

        //x轴数据
        res.put("xData", days);
        //key floor   value 各天的刷证次数
        Map<String, List<Long>> floorCountMap = new HashMap<>();
        List<PassSeries> seriesArrayList = Lists.newArrayList();
        for (String floor : floorSets) {
            //key 2018-02-03 value 刷证次数
            Map<String, Long> dayMap = collect.get(floor);
            //次数
            List<Long> countList = new ArrayList<>();
            for (String str : days) {
                Long dayCount = dayMap.getOrDefault(str, 0L);
                countList.add(dayCount);

            }
            floorCountMap.put(floor, countList);
            PassSeries series = new PassSeries();
            series.setName(floor);
            series.setData(floorCountMap.get(floor));
            seriesArrayList.add(series);

        }

        List<PassSeries> collect1 = seriesArrayList.stream().sorted(Comparator.comparing(PassSeries::getName)).collect(Collectors.toList());
        res.put("seriesArrayList",collect1);
        List<String> nameList = collect1.stream().map(PassSeries::getName).collect(Collectors.toList());
        //legendData
        res.put("legendData", nameList);
        return res;
    }

    private  String correctFloor(String floor) {
        if(floor == null){
            return "未知楼号";
        }
        boolean isNumber = floor.matches("[+-]?[1-9]+[0-9]*(\\.[0-9]+)?");
        if(isNumber) {
            int i = Integer.parseInt(floor);
            if(i>6) {
                return "未知楼号";
            }
            return floor + "号楼";
        } else if(floor.endsWith("号楼")) {
            return floor;
        } else {
            return "未知楼号";
        }
    }


    /**
     * 日期维度当天报表
     *
     * @param entity 查询条件
     * @return java.lang.Object
     * @author JH
     * @since 2020/3/3 19:32
     */
    public Object dayReportForm(IdentityInformation entity) {
        Map<String, Object> map = new HashMap<>();
        Long currentTime = System.currentTimeMillis();
        Long dailyStart = TimeUtil.getDailyStartTime(currentTime, "GMT+8:00");
        Long dailyEnd = TimeUtil.getDailyEndTime(currentTime, "GMT+8:00");

        List<IdentitySwipe> list = baseMapper.reportForm(entity.getPark(),dailyStart,dailyEnd );

        list.forEach(n -> {
            String hourStr = DateUtils.timeStamp2Date(n.getGmtCreate(), "HH");
            n.setHourOfDay(Integer.parseInt(hourStr));
        });
        Map<Integer, List<IdentitySwipe>> hourMap = list.stream().collect(Collectors.groupingBy(IdentitySwipe::getHourOfDay, Collectors.toList()));
        DateReport dateReport = new DateReport();
        List<Integer> xAxisData = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            xAxisData.add(i);
        }
        dateReport.xAxis.setData(xAxisData);

        //人数
        List<Integer> human = new ArrayList<>();
        for (int i : xAxisData) {
            List<IdentitySwipe> swipeList = hourMap.get(i);
            if (CollectionUtils.isNotEmpty(swipeList)) {
                human.add(swipeList.size());
            } else {
                human.add(0);
            }

        }

        DateReport.Serie humanSerie = dateReport.new Serie();
        humanSerie.setName("人数");
        humanSerie.setData(human);
        dateReport.series.add(humanSerie);
        map.put("title", dateReport.getTitle());
        String[] data = {"人数"};
        dateReport.getLegend().setData(data);
        map.put("legend", dateReport.getLegend());
        map.put("xAxis", dateReport.getXAxis());
        map.put("yAxis", dateReport.getYAxis());
        map.put("series", dateReport.getSeries());
        return map;
    }


    /**
     * 日期维度一周报表
     *
     * @param entity 查询条件
     * @return java.lang.Object
     * @author JH
     * @since 2020/3/3 19:32
     */
    public Object weekReportForm(IdentityInformation entity) {
        Map<String, Object> map = new HashMap<>();
        Long currentTime = System.currentTimeMillis();
        Long dailyStart = TimeUtil.beforeWeek();
        Long dailyEnd = TimeUtil.getDailyEndTime(currentTime, "GMT+8:00");
        List<IdentitySwipe> list = baseMapper.reportForm(entity.getPark(),dailyStart,dailyEnd );
        list.forEach(n -> {
            String datStr = DateUtils.timeStamp2Date(n.getGmtCreate(), "yyyy-MM-dd");
            n.setDayOfMonth(datStr);
            String hourStr = DateUtils.timeStamp2Date(n.getGmtCreate(), "HH");
            n.setHourOfDay(Integer.parseInt(hourStr));

        });
        Map<String, Map<Integer, Long>> collect = list.stream().collect(Collectors.groupingBy(IdentitySwipe::getDayOfMonth, Collectors.groupingBy(IdentitySwipe::getHourOfDay, Collectors.counting())));

        List<String> days = TimeUtil.getDays(DateUtils.timeStamp2Date(dailyStart, "yyyy-MM-dd"), DateUtils.timeStamp2Date(dailyEnd, "yyyy-MM-dd"));
        DateReport dateReport = new DateReport();
        List<Integer> xAxisData = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            xAxisData.add(i);
        }
        dateReport.xAxis.setData(xAxisData);

        for(String str : days) {
            Map<Integer, Long> hourCountMap = collect.get(str);
            List<Long> human = new ArrayList<>();
            if(MapUtils.isEmpty(hourCountMap))  {
                //人数
                for (int i = 0; i <= 23; i++) {
                  human.add(0L);
                }

            }else {
                for (int i = 0; i <= 23; i++) {
                    human.add(hourCountMap.getOrDefault(i,0L));
                }
            }
            DateReport.Serie humanSerie = dateReport.new Serie();
            humanSerie.setName(str);
            humanSerie.setData(human);
            dateReport.series.add(humanSerie);
        }

        map.put("title", dateReport.getTitle());
        dateReport.getLegend().setData(days.toArray(new String[0]));
        map.put("legend", dateReport.getLegend());
        map.put("xAxis", dateReport.getXAxis());
        map.put("yAxis", dateReport.getYAxis());
        map.put("series", dateReport.getSeries());
        return map;
    }

    /**
     * 根据园区名称返回有效人员
     * @param park 园区名称
     * @return java.util.List<com.jkhl.entrance.entity.IdentityInformation>
     * @author JH
     * @since 2020/3/23 10:40
     */
    public List<IdentityInformation> listByPark(String park) {
        return baseMapper.listByPark(park);
    }

    public List<String> distinctNumber() {
        return baseMapper.distinctNumber();
    }

    public Integer liveCount(String park) {
        return baseMapper.liveCount(park);
    }

}
