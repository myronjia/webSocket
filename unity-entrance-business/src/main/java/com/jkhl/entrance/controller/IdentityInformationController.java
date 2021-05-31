
package com.jkhl.entrance.controller;


import cn.afterturn.easypoi.excel.entity.result.ExcelVerifyHandlerResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.jkhl.entrance.entity.IdentityInformation;
import com.jkhl.entrance.entity.IdentitySwipe;
import com.jkhl.entrance.service.IdentityInformationServiceImpl;
import com.jkhl.entrance.service.IdentitySwipeServiceImpl;
import com.jkhl.entrance.util.HttpUtilCover;
import com.unity.common.base.SessionHolder;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constants.ConstString;
import com.unity.common.enums.FlagEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.*;
import com.unity.common.ui.excel.ExcelEntity;
import com.unity.common.ui.excel.ExportEntity;
import com.unity.common.ui.excel.ImportEntity;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;








/**
 * 身份信息
 * @author creator
 * 生成时间 2020-02-01 16:40:02
 */
@Slf4j
@Controller
@RequestMapping("/identityinformation")
public class IdentityInformationController extends BaseWebController {
    @Autowired
    IdentityInformationServiceImpl service;

    @Autowired
    IdentitySwipeServiceImpl identitySwipeService;

    @Autowired
    RedisTemplate redisTemplate;

    private int rowNum = 1;


    @Value("${weather.appid}")
    String weatherAppid;

    @Value("${weather.appsecret}")
    String weatherAppsecret;
    /**
     * 校验身份信息
     * @return 返回身份对象
     */
    @PostMapping("/validInfo")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> validInfo(@RequestBody IdentityInformation entity){

        LambdaQueryWrapper<IdentityInformation> q= new LambdaQueryWrapper<IdentityInformation>();
        q.eq(IdentityInformation::getIdentityInformationNumber,entity.getIdentityInformationNumber());
        IdentityInformation ee = service.getOne(q,false);
        IdentitySwipe swipe = new IdentitySwipe();
        if(ee!=null && ee.getId()!=null){
            swipe.setIdIdentityInformation(ee.getId());
            if(ee.getEnterBeiJingTime()!=null) {
                double distanceOfTwoDate = DateUtils.getDistanceOfTwoDate(ee.getEnterBeiJingTime(), new Date());
                ee.setIntoDays(((int)distanceOfTwoDate)+"天");
            }else{
                ee.setIntoDays("未填报");
            }
        }
        swipe.setIdentityInformationNumber(entity.getIdentityInformationNumber());
        identitySwipeService.save(swipe);

        return success(ee);

    }

    /**
     * 模块入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @return 返回视图
     */
    @RequestMapping("/view/moduleEntrance/{iframe}")
    public String moduleEntrance(Model model,@PathVariable("iframe") String iframe) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("button", "[]");
        return "IdentityInformationList";
    }

    /**
     * 添加或修改表达入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @param id 身份信息id
     * @return 返回视图
     */
    @RequestMapping(value = "/view/editEntrance/{iframe}")
    public String editEntrance(Model model,@PathVariable("iframe") String iframe,String id) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("id", id);
        if(id!=null){
            IdentityInformation entity = service.getById(id);
            if(entity==null) model.addAttribute("entity", "{}");
            else model.addAttribute("entity", JSON.toJSONString(convert2Map(entity)));
        }
        else{
            model.addAttribute("entity", "{}");
        }
        return "IdentityInformationEdit";
    }

     /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {

        LambdaQueryWrapper<IdentityInformation> ew = wrapper(search);

        IPage p = service.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }

         /**
     * 添加或修改
     * @param entity 身份信息实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody IdentityInformation entity) {

        if(entity.getId()==null){
            LambdaQueryWrapper<IdentityInformation> q= new LambdaQueryWrapper<IdentityInformation>();
            q.eq(IdentityInformation::getIdentityInformationNumber,entity.getIdentityInformationNumber());
            if(service.count(q)!=0){
                throw new UnityRuntimeException("身份证已存在");
            }
        }
        service.saveOrUpdate(entity);

        return success(null);
    }

    /**
     * 添加或修改
     * @param entity 身份信息实体
     * @return
     */
    @PostMapping("/allowSave")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  allowSave(@RequestBody IdentityInformation entity) {

        //允许通行
        //如果库中存在，改为已证明
        //如果库中不存在，保存并且设为已证明


        if(entity.getId()==null){
            LambdaQueryWrapper<IdentityInformation> q= new LambdaQueryWrapper<IdentityInformation>();
            q.eq(IdentityInformation::getIdentityInformationNumber,entity.getIdentityInformationNumber());
            List<IdentityInformation> identityInformationList = service.list(q);

            if(CollectionUtils.isNotEmpty(identityInformationList)){
                identityInformationList.stream().peek( v -> v.setIsAdmittance(1)).collect(Collectors.toList());
                service.saveOrUpdateBatch(identityInformationList);
                //throw new UnityRuntimeException("身份证已存在");
            }else{
                entity.setIsAdmittance(1);
                service.saveOrUpdate(entity);
            }
        }

        return success(null);
    }

    @RequestMapping({"/export/excelOne"})
    public void exportExcel(HttpServletResponse res,String cond,String paraDay) {
        rowNum = 1;
        String fileName="人员刷证纪录";
        ExportEntity<IdentityInformation> excel =  ExcelEntity.exportEntity(res);

        try {
            SearchElementGrid search = new SearchElementGrid();
            search.setCond(JSON.parseObject(cond, SearchCondition.class));
            LambdaQueryWrapper<IdentityInformation> ew = wrapperForExport(search,paraDay);
            String sql = "";

            List<IdentityInformation> list = service.getRecordForExport(ew);

            excel.<IdentityInformation>sheet()
                .column("rowNum","序号")
                .column(IdentityInformation::getGmtRecord,"刷证时间")
                .column(IdentityInformation::getIdentityInformationNumber,"身份证号")
                .column(IdentityInformation::getName,"姓名")
                .column(IdentityInformation::getPhone,"手机号")
                .column("enterDate","入京时间")
                .column(IdentityInformation::getRoomNumber,"房间号")
                .column(IdentityInformation::getDepartment,"所属公司")
                .column(IdentityInformation::getCompanyContacts,"负责人")
                .column(IdentityInformation::getCompanyContactsPhone,"负责人电话")
                .column("isFlagTitle","高危否")
                .column(IdentityInformation::getPersonType,"人员类型")
                .entities(convert2List(list))
                .name(fileName)
            .export(fileName);
        }
        catch (Exception ex){
            excel.exportError(fileName,ex);
        }
    }


     /**
     * 获取数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> list(@RequestBody SearchElementGrid search) {

        LambdaQueryWrapper<IdentityInformation> ew = wrapper(search);

        List list = service.list(ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(Long.valueOf(list.size()))
                .items(convert2List(list)).build();
        return success(result);

    }

    /**
     * 查询条件转换
     * @param search 统一查询对象
     * @return
     */
    private LambdaQueryWrapper<IdentityInformation> wrapper(SearchElementGrid search){
        LambdaQueryWrapper<IdentityInformation> ew = null;
        Integer flag=-1;
        Map<String,Long> map = new HashMap<>();
        if(search!=null){
            Rule date = null;
            if(search.getCond()!=null){
                Rule r = search.getCond().findRuleOne("isCard");
                if(r!=null){
                    flag= Integer.valueOf(r.getData().toString()) ;
                    search.getCond().excludeSpecialState("isCard","");
                }
                //获取开始时间和结束时间
                search.getCond().findRule(IdentityInformation::getGmtModified).forEach(rs->{
                    if(Operate.ge.getId().equals(rs.getOp())){
                        //applyStr += "  >= "  ;
                        map.put("start",DateUtils.parseDate(rs.getData()).getTime());
                    }else if(Operate.le.getId().equals(rs.getOp())){
                        //applyStr += "  <= " ;
                        map.put("end",DateUtils.parseDate(rs.getData()).getTime());
                    }
                });
                search.getCond().excludeSpecialState(IdentityInformation::getGmtModified);

            }
            ew = search.toEntityLambdaWrapper(IdentityInformation.class);

        }
        else{
            ew = new LambdaQueryWrapper<IdentityInformation>();
        }

        ew.orderBy(true, false,IdentityInformation::getGmtCreate);

       /* StringBuffer sql = new StringBuffer();
        sql.append("select * from identity_swipe sw where  sw.is_deleted = 0 and sw.identity_information_number = identity_information.identity_information_number");
         if (map.get("start") != null) {
             sql.append(" and sw.gmt_create >= " + map.get("start")).append(" ");
         }
         if (map.get("end") != null) {
             sql.append( " and sw.gmt_create <= " + map.get("end")).append(" ");
         }
        if(flag==0){
            //未刷证
            ew.notExists(sql.toString());
        }else if(flag ==1){
            //刷证
            ew.exists(sql.toString());
        }*/
        LambdaQueryWrapper<IdentitySwipe> wrapper = new LambdaQueryWrapper();
        if (map.get("start") != null) {
            wrapper.ge(IdentitySwipe::getGmtCreate, map.get("start"));
        }
        if (map.get("end") != null) {
            wrapper.le(IdentitySwipe::getGmtCreate, map.get("end"));
        }
        List<String> idNumbers = new ArrayList<>();
        List<IdentitySwipe> IdentitySwipeList = identitySwipeService.list(wrapper);
        List<IdentitySwipe> distinctList = IdentitySwipeList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getIdentityInformationNumber()))),
                        ArrayList::new));
        logger.info("去重后数据数:"+distinctList.size());
        distinctList.forEach(e->{
            if(!StringUtils.isEmpty(e.getIdentityInformationNumber())){
                idNumbers.add(e.getIdentityInformationNumber());
            }
        });
        if(idNumbers.size()>0) {
            if (flag == 0) {
                //未刷证
                ew.notIn(IdentityInformation::getIdentityInformationNumber, idNumbers);
            } else if (flag == 1) {
                //刷证
                ew.in(IdentityInformation::getIdentityInformationNumber, idNumbers);
            }
        }else{
            //查刷证为空
            if (flag == 1){
                ew.eq(IdentityInformation::getId,0);
            }
            //未刷证正常执行

        }
        return ew;
    }

    private LambdaQueryWrapper<IdentityInformation> wrapperForExport(SearchElementGrid search,String paraDay){
        LambdaQueryWrapper<IdentityInformation> ew = null;
        if(search!=null){
            if(search.getCond()!=null){
                /*search.getCond().findRule(IdentityInformation::getIdentityInformationNumber).forEach(r->{
                    r.setField("a.");
                });*/
            }
            List numList = search.getCond().getRules().stream().filter(item->"identityInformationNumber".equals(item.getField())).collect(Collectors.toList());
            ew = search.toEntityLambdaWrapper(IdentityInformation.class);
            if(numList != null &&  numList.size() > 0){
                search.getCond().findRule(IdentityInformation::getIdentityInformationNumber).forEach(r->{
                    search.getCond().excludeSpecialState(Lists.newArrayList(r.getField()), r.getData().toString());
                });
                ew  = service.getNumSql(ew,numList);
            }
            /*List timeList = search.getCond().getRules().stream().filter(item->"gmtRecord".equals(item.getField())).collect(Collectors.toList());
            if(timeList!=null&&timeList.size()>0){
                search.getCond().findRule(IdentityInformation::getGmtRecord).forEach(r->{
                    search.getCond().excludeSpecialState(Lists.newArrayList(r.getField()), r.getData().toString());
                });
                ew = service.getTimeSql(ew,timeList);
            }*/
            if(paraDay!=null){
                ew = service.getTimeSql(ew,paraDay);
            }
            if(ew==null) {
                ew = search.toEntityLambdaWrapper(IdentityInformation.class);
            }

        }
        else{
            ew = new LambdaQueryWrapper<IdentityInformation>();
        }


        return ew;
    }



     /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<IdentityInformation> list){

        return JsonUtil.<IdentityInformation>ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                }
                ,IdentityInformation::getIsLive
                ,IdentityInformation::getId,IdentityInformation::getGmtCreate,IdentityInformation::getGmtModified,IdentityInformation::getSort,IdentityInformation::getNotes,IdentityInformation::getIdentityInformationNumber,IdentityInformation::getName,IdentityInformation::getPhone,IdentityInformation::getPark,IdentityInformation::getFloorNumber,IdentityInformation::getRoomNumber,IdentityInformation::getDepartment,IdentityInformation::getCompanyContacts,IdentityInformation::getCompanyContactsPhone,IdentityInformation::getIsAdmittance,IdentityInformation::getEnterBeiJingTime,IdentityInformation::getPersonType
        );
    }

     /**
     * 将实体 转换为 Map
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(IdentityInformation ent){
        return JsonUtil.<IdentityInformation>ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m,entity);
                }
                ,IdentityInformation::getIsLive
                ,IdentityInformation::getIsAdmittance
                ,IdentityInformation::getId,IdentityInformation::getGmtCreate,IdentityInformation::getGmtModified,IdentityInformation::getIsDeleted,IdentityInformation::getSort,IdentityInformation::getNotes,IdentityInformation::getIdentityInformationNumber,IdentityInformation::getName,IdentityInformation::getPhone,IdentityInformation::getPark,IdentityInformation::getFloorNumber,IdentityInformation::getRoomNumber,IdentityInformation::getDepartment,IdentityInformation::getCompanyContacts,IdentityInformation::getCompanyContactsPhone,IdentityInformation::getIsFlag,IdentityInformation::getPersonType
        );
    }

    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m,IdentityInformation entity){
        m.put("rowNum",rowNum);
        rowNum++;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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

        if(entity.getIsFlag()!=null) {
            m.put("isFlagTitle", FlagEnum.of(entity.getIsFlag()).getName());
        }

        if(entity.getIsAdmittance()!=null){
            m.put("isAdmittanceTitle",FlagEnum.of(entity.getIsAdmittance()).getName());
        }
        if(entity.getEnterBeiJingTime()!=null){
            m.put("enterTimeTitle",sdf.format(entity.getEnterBeiJingTime()));
        }
        m.put("gmtRecord", DateUtils.timeStamp2Date(entity.getGmtRecord()));

    }

    /**
     * 批量删除
     * @param ids id列表用英文逗号分隔
     * @return
     */
    @DeleteMapping("/del/{ids}")
    public Mono<ResponseEntity<SystemResponse<Object>>>  del(@PathVariable("ids") String ids) {
        service.removeByIds(ConvertUtil.arrString2Long(ids.split(ConstString.SPLIT_COMMA)));
        return success(null);
    }


    /**
     * 更改排序
     * @param id
     * @param up 1 下降 0 上升
     * @return
     */
    @PostMapping("/changeOrder/{id}/{up}")
    public Mono<ResponseEntity<SystemResponse<Object>>> changeOrder(@PathVariable Integer id,@PathVariable Integer up){
        IdentityInformation entity = service.getById(id);
        Long sort = entity.getSort();
        LambdaQueryWrapper<IdentityInformation> wrapper = new LambdaQueryWrapper();

        String msg ="";
        if(up==1) {
            wrapper.lt(IdentityInformation::getSort, sort);
            msg ="已经是最后一条数据";
            wrapper.orderByDesc(IdentityInformation::getSort);
        }
        else {
            wrapper.gt(IdentityInformation::getSort, sort);
            msg ="已经是第一条数据";
            wrapper.orderByAsc(IdentityInformation::getSort);
        }


        IdentityInformation entity1 = service.getOne(wrapper);
        if(entity1==null) throw new UnityRuntimeException(msg);

        entity.setSort(entity1.getSort());

        IdentityInformation entityA = new IdentityInformation();
        entityA.setId(entity.getId());
        entityA.setSort(entity1.getSort());
        service.updateById(entityA);

        IdentityInformation entityB = new IdentityInformation();
        entityB.setId(entity1.getId());
        entityB.setSort(sort);
        service.updateById(entityB);

        return success("移动成功");
    }

    /**
     * @param res
     * @param file
     * @return
     */
    @PostMapping("importExcel")
    public Mono<ResponseEntity<SystemResponse<Object>>> importExcel(HttpServletResponse res, @RequestParam(value = "file", required = false) MultipartFile file) {
        /**
         *将excel转换为文件流
         * */
        InputStream is = null;
        try {
            is = file.getInputStream();
        } catch (Exception ex) {
            throw UnityRuntimeException.newInstance().message(ex.getMessage()).build();
        }
        /**
         * 读取文件流信息到集合list
         * */
        List<String> numsInExcel = new ArrayList<>();
        ImportEntity<IdentityInformation> importExcel = ExcelEntity.importEntity(res, IdentityInformation.class, redisTemplate);
        List<IdentityInformation> list = importExcel
                .addColumn(IdentityInformation::getDepartment, "所属公司", true)
                .addColumn(IdentityInformation::getRoomNumber, "楼栋号", false)
                .addColumn(IdentityInformation::getRoomNumber, "房间号", true)
                .addColumn(IdentityInformation::getName, "姓名", true)
                .addColumn(IdentityInformation::getIdentityInformationNumber, "身份证号", true)
                .addColumn(IdentityInformation::getPhone, "手机号", true)
                .addColumn(IdentityInformation::getIsFlagTitle, "高危否", true)
                .addColumn(IdentityInformation::getIsAdmittanceTitle, "是否允许通行", true)
                .addColumn(IdentityInformation::getCompanyContacts, "负责人", false)
                .addColumn(IdentityInformation::getCompanyContactsPhone, "负责人电话", false)
                .addColumn(IdentityInformation::getEnterBeiJingTime, "入京时间", true)
                .addColumn(IdentityInformation::getPersonType, "人员类型", false)
                .addColumn(IdentityInformation::getNotes, "备注", false)
                .verifyHandler((IdentityInformation r) -> {
            ExcelVerifyHandlerResult result = new ExcelVerifyHandlerResult();
                    StringBuilder rowMsg = new StringBuilder();
                    result.setSuccess(true);
                    if (org.springframework.util.StringUtils.isEmpty(r.getDepartment())
                            && org.springframework.util.StringUtils.isEmpty(r.getRoomNumber())
                            && org.springframework.util.StringUtils.isEmpty(r.getName())
                            && org.springframework.util.StringUtils.isEmpty(r.getIdentityInformationNumber())
                            && org.springframework.util.StringUtils.isEmpty(r.getPhone())
                            && org.springframework.util.StringUtils.isEmpty(r.getCompanyContacts())
                            && org.springframework.util.StringUtils.isEmpty(r.getCompanyContactsPhone())
                            && org.springframework.util.StringUtils.isEmpty(r.getIsFlag())) {
                        r = null;
                    }else {
                       /* LambdaQueryWrapper<IdentityInformation> ew = new LambdaQueryWrapper<>();
                        ew.eq(IdentityInformation::getIdentityInformationNumber,r.getIdentityInformationNumber());
                        int count = service.count(ew);
                        if(count>0){
                            rowMsg.append(r.getIdentityInformationNumber()+"该身份证号已存在");
                        }*/
                        if(StringUtils.isEmpty(r.getIdentityInformationNumber())){
                            rowMsg.append("身份证号不能为空 ");
                        }else {
                            if(numsInExcel.contains(r.getIdentityInformationNumber())){
                                rowMsg.append(r.getIdentityInformationNumber()+"身份证号重复 ");
                            }else {
                                numsInExcel.add(r.getIdentityInformationNumber());
                            }
                        }
                        if(StringUtils.isEmpty(r.getIsFlagTitle())||(!"否".equals(r.getIsFlagTitle())&&!"是".equals(r.getIsFlagTitle()))){
                            rowMsg.append("高危否？必须填写：是/否 ");
                        }
                        if(StringUtils.isEmpty(r.getIsAdmittanceTitle())||(!"否".equals(r.getIsAdmittanceTitle())&&!"是".equals(r.getIsAdmittanceTitle()))){
                            rowMsg.append("是否允许通行？必须填写：是/否 ");
                        }

                        if(rowMsg.length()>0){
                            result.setMsg(rowMsg.toString());
                            // log.info("===Excel园区导入错误信息==={}", rowMsg);
//                            System.out.println("===Excel园区导入错误信息==={}"+rowMsg.toString());
                            result.setSuccess(false);
                        }
                        //            StringBuilder msg = new StringBuilder();
                        //            if (msg.length() > 0) {
                        //                result.setMsg(msg.toString());
                        //                result.setSuccess(false);
                        //            }
                    }
                    return result;
        }).Import(is);
        beforeSaveBatch(list);
        //service.saveBatch(list);
        return success("导入成功");
    }

    private void beforeSaveBatch(List<IdentityInformation> list){
        //如果存在则替换
        List<IdentityInformation> identityInformationList = service.list();
        for(IdentityInformation identityInformation:list){
            identityInformationList.forEach(e->{
                if(!StringUtils.isEmpty(e.getIdentityInformationNumber())&&!StringUtils.isEmpty(identityInformation.getIdentityInformationNumber())) {
                    if (e.getIdentityInformationNumber().equals(identityInformation.getIdentityInformationNumber())) {
                        identityInformation.setId(e.getId());

                    }
                }
            });
            if("否".equals(identityInformation.getIsFlagTitle())){
                identityInformation.setIsFlag(0);
            }else{
                identityInformation.setIsFlag(1);
            }
            if("否".equals(identityInformation.getIsAdmittanceTitle())){
                identityInformation.setIsAdmittance(0);
            }else{
                identityInformation.setIsAdmittance(1);
            }
        }
        if(CollectionUtils.isNotEmpty(list)){
            service.saveOrUpdateBatch(list);
        }
    }
    @RequestMapping({"/export/excel"})
    public void exportAllExcel(HttpServletResponse res,String cond) {
        String fileName="身份信息";
        ExportEntity<IdentityInformation> excel =  ExcelEntity.exportEntity(res);

        try {
            SearchElementGrid search = new SearchElementGrid();
            search.setCond(JSON.parseObject(cond, SearchCondition.class));
            LambdaQueryWrapper<IdentityInformation> ew = wrapper(search);
            List<IdentityInformation> list = service.list(ew);

            excel.<IdentityInformation>sheet()
                    .column(IdentityInformation::getIdentityInformationNumber,"身份证号")
                    .column(IdentityInformation::getName,"姓名")
                    .column(IdentityInformation::getPhone,"手机号")
                    .column(IdentityInformation::getRoomNumber,"房间号")
                    .column(IdentityInformation::getDepartment,"所属公司")
                    .column(IdentityInformation::getCompanyContacts,"负责人")
                    .column(IdentityInformation::getCompanyContactsPhone,"负责人电话")
                    .column("isFlagTitle","是否关注")
                    .column("isAdmittanceTitle","是否允许通行")
                    .column(IdentityInformation::getPersonType,"人员类型")
                    .column(IdentityInformation::getNotes,"备注")
                    .entities(convert2List(list))
                    .name(fileName)
                    .export(fileName);
        }
        catch (Exception ex){
            excel.exportError(fileName,ex);
        }
    }




    /**
     * 各公司人员构成
     *
     * @param entity 查询条件
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity   <   com.unity.common.pojos.SystemResponse   <   java.lang.Object>>>
     * @author JH
     * @since 2020/3/3 19:33
     */
    @PostMapping("/deptCons")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> deptCons(@RequestBody IdentityInformation entity) {
        return success(service.deptCons(entity));
    }


    /**
     * 各楼栋人员构成
     *
     * @param entity 查询条件
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity   <   com.unity.common.pojos.SystemResponse   <   java.lang.Object>>>
     * @author JH
     * @since 2020/3/3 19:33
     */
    @PostMapping("/floorCons")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> floorCons(@RequestBody IdentityInformation entity) {
        return success(service.floorCons(entity));
    }


    /**
     * 各省份构成
     *
     * @param entity 查询条件
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity   <   com.unity.common.pojos.SystemResponse   <   java.lang.Object>>>
     * @author JH
     * @since 2020/3/3 19:33
     */
    @PostMapping("/provinceCons")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> provinceCons(@RequestBody IdentityInformation entity) {
        return success(service.provinceCons(entity));
    }

    /**
     * 人员通行情况统计表
     *
     * @param entity 查询条件
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity   <   com.unity.common.pojos.SystemResponse   <   java.lang.Object>>>
     * @author JH
     * @since 2020/3/3 19:33
     */
    @PostMapping("/passInfo")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> passInfo(@RequestBody IdentityInformation entity) {
        return success(service.passInfo(entity));
    }

    /**
     * 楼栋通行情况统计表
     *
     * @param entity 查询条件
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity   <   com.unity.common.pojos.SystemResponse   <   java.lang.Object>>>
     * @author JH
     * @since 2020/3/3 19:33
     */
    @PostMapping("/floorPassInfo")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> floorPassInfo(@RequestBody IdentityInformation entity) {
        return success(service.floorPassInfo(entity));
    }

    /**
     * 人员流量统计
     *
     * @param entity 查询条件
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity   <   com.unity.common.pojos.SystemResponse   <   java.lang.Object>>>
     * @author JH
     * @since 2020/3/3 19:33
     */
    @PostMapping("/reportForm")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> reportForm(@RequestBody IdentityInformation entity) {
        if(entity.getType() == 1) {
            return success(service.dayReportForm(entity));
        }else {
            return success(service.weekReportForm(entity));
        }

    }
    /**
     * 天气查询
     *
     * @author ZC
     * @since 2020/3/23 11:25
     */
    @PostMapping("/weather")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> searchWeather() {
        String catchData = SessionHolder.getCatch().get("weather");
        if (StringUtils.isNotBlank(catchData)) {
            JSONObject weatherData = JSON.parseObject(catchData);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date beforeDate = null;
            try {
                beforeDate = formatter.parse((String) weatherData.get("date"));
            } catch (ParseException e) {
                e.printStackTrace();
                JSONObject jsonObject = this.searchWeatherApi();
                return success(jsonObject);
            }
            String format = formatter.format(new Date());
            int distanceOfTwoDate = (int) DateUtils.getDistanceOfTwoDate(beforeDate, DateUtils.parseDate(format));
            if(distanceOfTwoDate == 0){
                return success(weatherData);
            }
        }
        JSONObject jsonObject = this.searchWeatherApi();
        return success(jsonObject);
    }
    /**
     * 天气接口查询
     */
    private JSONObject searchWeatherApi(){
        SessionHolder.getCatch().remove("weather");
        JSONObject jsonObject = null;
        String url = "https://www.tianqiapi.com/api/?version=v6&cityid=101010100&appid=APPID&appsecret=SECRET";
        url = url.replace("APPID", weatherAppid).replace("SECRET", weatherAppsecret);
        try {
            String data = HttpUtilCover.get(url);
            log.debug("天气接口====查询结果result=:{}",data);
            if (StringUtils.isNotBlank(data)) {
                jsonObject = JSON.parseObject(data);
                SessionHolder.getCatch().put("weather",data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnityRuntimeException("天气接口调用异常！！");
        }
        return jsonObject;
    }

}

