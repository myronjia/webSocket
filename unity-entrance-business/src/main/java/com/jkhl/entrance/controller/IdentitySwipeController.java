
package com.jkhl.entrance.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.exception.UnityRuntimeException;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSON;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.excel.ExcelEntity;
import com.unity.common.ui.excel.ExportEntity;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.ui.SearchCondition;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.constants.ConstString;

import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpServletResponse;



import com.jkhl.entrance.service.IdentitySwipeServiceImpl;
import com.jkhl.entrance.entity.IdentitySwipe;








/**
 * 身份信息
 * @author creator
 * 生成时间 2020-02-02 13:24:33
 */
@Controller
@RequestMapping("/identityswipe")
public class IdentitySwipeController extends BaseWebController {
    @Autowired
    IdentitySwipeServiceImpl service;
    


    /**
     * 模块入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @return 返回视图
     */
    @RequestMapping("/view/moduleEntrance/{iframe}")
    public String moduleEntrance(Model model,@PathVariable("iframe") String iframe,String idIdentityInformation) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("button", "[]");
        model.addAttribute("idIdentityInformation", idIdentityInformation);

        return "IdentitySwipeList";
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
            IdentitySwipe entity = service.getById(id);
            if(entity==null) model.addAttribute("entity", "{}");
            else model.addAttribute("entity", JSON.toJSONString(convert2Map(entity)));
        }
        else{
            model.addAttribute("entity", "{}");
        }
        return "IdentitySwipeEdit";
    }
    
     /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<IdentitySwipe> ew = wrapper(search);

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
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody IdentitySwipe entity) {
        
        service.saveOrUpdate(entity);
        return success(null);
    }
    
    @RequestMapping({"/export/excel"})
    public void exportExcel(HttpServletResponse res,String cond) {
        String fileName="身份信息";
        ExportEntity<IdentitySwipe> excel =  ExcelEntity.exportEntity(res);

        try {
            SearchElementGrid search = new SearchElementGrid();
            search.setCond(JSON.parseObject(cond, SearchCondition.class));
            LambdaQueryWrapper<IdentitySwipe> ew = wrapper(search);
            List<IdentitySwipe> list = service.list(ew);
     
            excel.<IdentitySwipe>sheet()
                .column(IdentitySwipe::getId,"主键id")
                .column(IdentitySwipe::getIdIdentityInformation,"人员id")
                .column(IdentitySwipe::getGmtModified,"操作时间")
                .column(IdentitySwipe::getNotes,"备注")
                .column(IdentitySwipe::getEditor,"修改人")
                .column(IdentitySwipe::getIdentityInformationNumber,"身份证号")
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
    
        LambdaQueryWrapper<IdentitySwipe> ew = wrapper(search);

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
    private LambdaQueryWrapper<IdentitySwipe> wrapper(SearchElementGrid search){
        LambdaQueryWrapper<IdentitySwipe> ew = null;
        if(search!=null){
            if(search.getCond()!=null){
            }
            ew = search.toEntityLambdaWrapper(IdentitySwipe.class);

        }
        else{
            ew = new LambdaQueryWrapper<IdentitySwipe>();
        }

        ew.orderBy(true, false,IdentitySwipe::getSort);
        
        return ew;
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
                ,IdentitySwipe::getId,IdentitySwipe::getIdIdentityInformation,IdentitySwipe::getGmtCreate,IdentitySwipe::getGmtModified,IdentitySwipe::getSort,IdentitySwipe::getNotes,IdentitySwipe::getIdentityInformationNumber
        );
    }
    
     /**
     * 将实体 转换为 Map
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(IdentitySwipe ent){
        return JsonUtil.<IdentitySwipe>ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m,entity);
                }
                ,IdentitySwipe::getId,IdentitySwipe::getIdIdentityInformation,IdentitySwipe::getGmtCreate,IdentitySwipe::getGmtModified,IdentitySwipe::getIsDeleted,IdentitySwipe::getSort,IdentitySwipe::getNotes,IdentitySwipe::getIdentityInformationNumber
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
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
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
        IdentitySwipe entity = service.getById(id);
        Long sort = entity.getSort();
        LambdaQueryWrapper<IdentitySwipe> wrapper = new LambdaQueryWrapper();

        String msg ="";
        if(up==1) {
            wrapper.lt(IdentitySwipe::getSort, sort);
            msg ="已经是最后一条数据";
            wrapper.orderByDesc(IdentitySwipe::getSort);
        }
        else {
            wrapper.gt(IdentitySwipe::getSort, sort);
            msg ="已经是第一条数据";
            wrapper.orderByAsc(IdentitySwipe::getSort);
        }


        IdentitySwipe entity1 = service.getOne(wrapper);
        if(entity1==null) throw new UnityRuntimeException(msg);

        entity.setSort(entity1.getSort());

        IdentitySwipe entityA = new IdentitySwipe();
        entityA.setId(entity.getId());
        entityA.setSort(entity1.getSort());
        service.updateById(entityA);

        IdentitySwipe entityB = new IdentitySwipe();
        entityB.setId(entity1.getId());
        entityB.setSort(sort);
        service.updateById(entityB);

        return success("移动成功");
    }

    /**
     * 保存
     * @param entity 对象
     * @return
     */
    @PostMapping("/saveIdentitySwipe")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  saveIdentitySwipe(@RequestBody IdentitySwipe entity) {
        service.saveIdentitySwipes(entity);
        return success(null);
    }


    /**
     * 查询刷卡记录
     * @param entity 园区
     * @return 刷卡的map
     */
    @PostMapping("/selectIdentitySwipes")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  selectIdentitySwipes(@RequestBody IdentitySwipe entity) {
        if (StringUtils.isBlank(entity.getPark())){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "园区为空");
        }
        return success(service.selectIdentitySwipes(entity.getPark()));
    }

    /**
     * 查询刷卡记录数量
     * @param entity 园区
     * @return 刷卡的map
     */
    @PostMapping("/selectIdentitySwipesCount")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  selectIdentitySwipesCount(@RequestBody IdentitySwipe entity) {
        if (StringUtils.isBlank(entity.getPark())){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "园区为空");
        }
        return success(service.selectIdentitySwipesCount(entity.getPark()));
    }

    /**
     * 统计接口
     * @param entity 实体
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
     * @author JH
     * @since 2020/3/2 14:32
     */
    @PostMapping("/statistics")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  statistics(@RequestBody IdentitySwipe entity) {
        return success(service.statistics(entity));
    }



}

