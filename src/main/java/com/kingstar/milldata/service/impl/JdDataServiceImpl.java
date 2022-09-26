package com.kingstar.milldata.service.impl;

import com.kingstar.milldata.domain.JdData;
import com.kingstar.milldata.mapper.JdDataMapper;
import com.kingstar.milldata.service.IJdDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class JdDataServiceImpl implements IJdDateService {

    @Autowired
    private JdDataMapper jdDataMapper;

    /**
     * 查询，分页根据id进行
     * @param id
     * @param pageSize
     * @return
     */
    @Override
    public List<JdData> selectList(Long id, Long pageSize) {
        return jdDataMapper.selectList(id,pageSize);
    }


    /**
     * 新增商品信息
     * @param jdData
     */
    @Override
    public void insertJdData(JdData jdData) {
        jdDataMapper.insertJdData(jdData);
    }

    /**
     * 根据sku查询是否存在该商品
     * @param sku
     * @return
     */
    @Override
    public long selectBySku(long sku) {
        return jdDataMapper.selectBySku(sku);
    }

}
