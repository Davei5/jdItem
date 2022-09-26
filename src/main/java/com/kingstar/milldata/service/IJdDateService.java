package com.kingstar.milldata.service;

import com.kingstar.milldata.domain.JdData;

import java.util.List;

public interface IJdDateService {


    /**
     * 查询数据
     * @param id
     * @param pageSize
     * @return
     */
    public List<JdData> selectList(Long id, Long pageSize);

    /**
     * 新增
     * @param jdData
     */
    public void insertJdData(JdData jdData);

    /**
     * 根据sku查询数据库中是否存在该商品
     * @param sku
     * @return
     */
    public long selectBySku(long sku);

}
