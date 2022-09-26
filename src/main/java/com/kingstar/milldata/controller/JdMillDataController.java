package com.kingstar.milldata.controller;

import com.kingstar.milldata.config.AjaxResult;
import com.kingstar.milldata.domain.JdData;
import com.kingstar.milldata.model.JdReq;
import com.kingstar.milldata.service.IJdDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/millData")
public class JdMillDataController {

    @Autowired
    private IJdDateService jdDateService;

    /**
     * 分页查询
     * @param jdReq
     * @return
     */
    @PostMapping("/list")
    public AjaxResult selectList1(@RequestBody JdReq jdReq){

        Long id = jdReq.getId();
        //如果id为空，则请求第一页
        if (id == null){
            id = 0L;
        }
        Long pageSize = jdReq.getPageSize();

        List<JdData> jdData = jdDateService.selectList(id, pageSize);

        return AjaxResult.success(jdData);
    }



}
