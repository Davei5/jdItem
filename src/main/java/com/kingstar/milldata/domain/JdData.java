package com.kingstar.milldata.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类
 */
@Data
@Accessors(chain = true)
public class JdData {

    /** id */
    private long id;

    /** 商品集合id */
    private long spu;

    /** 商品单元id */
    private long sku;

    /** 商品标题 */
    private String title;

    /** 商品价格 */
    private BigDecimal price;

    /** 详情页url */
    private String detailsUrl;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;


}
