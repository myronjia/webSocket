package com.jkhl.entrance.controller.view;

import com.alibaba.druid.util.Base64;
import com.unity.common.base.SessionHolder;
import com.unity.common.constants.ConstString;
import com.unity.common.ui.excel.ExcelEntity;
import com.unity.common.ui.excel.ExportEntity;
import com.unity.common.util.Encodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("importErr")
public class ImportErrController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("/excel")
    public String moduleEntrance(Model model, HttpServletResponse res) {
        String name = "导入数据校验失败";
        //响应头信息
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/ms-excel; charset=UTF-8");
        res.setHeader("Content-disposition", "attachment; filename=" +  Encodes.urlEncode(name) + ".xls");

        String key = ConstString.IMPORT_EXCEL_ERR+"_"+ SessionHolder.getToken();
        byte[] b = Base64.altBase64ToByteArray((String)SessionHolder.getSession().getAttribute(ConstString.IMPORT_EXCEL_ERR));
        /*redisTemplate.delete(key);*/
        //byte[] b = SessionHolder.getSession().getAttribute(ConstString.IMPORT_EXCEL_ERR);
        try{
//            ByteArrayOutputStream os = new ByteArrayOutputStream();
//        BufferedOutputStream bos = new BufferedOutputStream(os);
//        bos.write(b);
//            os.write(b);
            res.getOutputStream().write(b);
            res.getOutputStream().close();
            //res.flushBuffer();
        }
        catch(Exception ex){
            String fileName="出错了";
            ExportEntity excel =  ExcelEntity.exportEntity(res);
            excel.exportError(fileName,ex);
        }


//        Workbook wb = (Workbook)SessionHolder.getSession().getAttribute(ConstString.IMPORT_EXCEL_ERR);
//        try{
//            wb.write(res.getOutputStream());
//        }
//        catch (Exception ex){
//            String fileName="出错了";
//            ExportEntity excel =  ExcelEntity.exportEntity(res);
//            excel.exportError(fileName,ex);
//        }
        return "";
    }
}
