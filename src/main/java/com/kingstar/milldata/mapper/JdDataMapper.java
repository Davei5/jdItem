package com.kingstar.milldata.mapper;

import com.kingstar.milldata.domain.JdData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JdDataMapper {

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


    /**
     * 查询分页，根据id进行
     * @param id
     * @param pageSize
     * @return
     */
    List<JdData> selectList(@Param("id") Long id, @Param("pageSize") Long pageSize);

}
