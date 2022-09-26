package com.kingstar.milldata.task;

import com.alibaba.fastjson.JSON;
import com.kingstar.milldata.ThreadPool.UserRejectHandle;
import com.kingstar.milldata.ThreadPool.UserThreadFactory;
import com.kingstar.milldata.constant.UrlsConstant;
import com.kingstar.milldata.domain.JdData;
import com.kingstar.milldata.service.IJdDateService;
import com.kingstar.milldata.utils.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.concurrent.*;

@Component
public class JdDataTask {

    private Logger logger = LoggerFactory.getLogger(JdDataTask.class);

    @Autowired
    private HttpUtil httpUtils;
    @Autowired
    private IJdDateService jdDateService;

    @Scheduled(fixedDelay = 100 * 1000)
    public void itemTask() throws Exception {

        //工作队列
        BlockingDeque queue = new LinkedBlockingDeque(1000);
        UserThreadFactory userThreadFactory = new UserThreadFactory("线程池组");
        UserRejectHandle userRejectHandle = new UserRejectHandle();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(50, 80, 60, TimeUnit.SECONDS,
                queue, userThreadFactory, userRejectHandle);

        logger.info("开始爬虫");
        //引入countDownLatch进行线程同步，使主线程等待线程池的所有任务结束
        CountDownLatch countDownLatch = new CountDownLatch(80);

        for (int k = 1;k<=3;k++) {

            File file = new File("C:\\Users\\16377\\Desktop\\list"+k+".xlsx");
            InputStream is = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(is);

            //读取工作簿的第一张表格
            Sheet sheet = workbook.getSheetAt(0);
//            threadPoolExecutor.execute(() -> {
                for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {

                    Row row = sheet.getRow(i);
                    // 读取单元格内容
                    Cell cell = row.getCell(0);
                    //设置单元格类型
                    cell.setCellType(CellType.STRING);
                    //获取单元格内的关键字
                    String value = StringUtils.trimToEmpty(cell.getStringCellValue());
                    // 对字符串进行转码
                    String encode = null;
                    try {
                        encode = URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //拼接url
                    String url = "https://search.jd.com/Search?keyword=" + encode + "&enc=utf-8&s=61&page=";

                    for (int j = 1; j < 101; j += 2) {

                        int finalJ = j;
                        threadPoolExecutor.execute(() -> {
                            String html = httpUtils.doGetHtml(url + finalJ);
                            try {
                                //解析页面，获取商品数据并存储
                                this.parse(html);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            countDownLatch.countDown();
                        });
                    }
                }
//            });

        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadPoolExecutor.shutdown();

        /*for (int j=urls.length-1;j>=0;j--) {

            for (int i = 1; i < 201; i += 2) {

                int finalJ1 = j;
                int finalI1 = i;
                threadPoolExecutor.execute(() -> {
                    int finalJ = finalJ1;
                    int finalI = finalI1;
                    String html = httpUtils.doGetHtml(urls[finalJ] + finalI);
                    try {
                        //解析页面，获取商品数据并存储
                        this.parse(html);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    countDownLatch.countDown();
                });
            }
            threadPoolExecutor.shutdown();
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }*/

        //爬取列表
        //添加链接
        //使用热门关键字搜索

        logger.info("数据抓取完成！");
    }

    private void parse(String html){
        //解析html获取Document
        Document doc = Jsoup.parse(html);

        //获取spu信息
        Elements spuEles = doc.select("div#J_goodsList > ul > li");
        for (Element spuEle : spuEles) {
            //获取spu
            long spu;
            if("".equals(spuEle.attr("data-spu"))){
                spu = 0L;
            }else{
                spu = Long.parseLong(spuEle.attr("data-spu"));
            }

            //获取sku信息
            Elements skuEles = spuEle.select("li.gl-item");

            for (Element skuEle : skuEles) {
                //获取sku
                long sku = Long.parseLong(skuEle.select("[data-sku]").attr("data-sku"));
                //根据sku查询商品数据'
                try {
                    long num = jdDateService.selectBySku(sku);
                    if(num > 0) {
                        //如果商品存在，则跳出
                        continue;
                    }
                } catch (Exception e){
                    logger.debug("查询\t"+sku+"\t超时");
                    continue;
                }

                JdData jdData = new JdData();
                jdData.setSku(sku);

                //设置商品的spu
                jdData.setSpu(spu);

                //获取商品的价格
                String priceJson = skuEle.select(".p-price > strong > i").first().text();
                BigDecimal price = new BigDecimal(priceJson);
                jdData.setPrice(price);

                //获取商品的标题
                String title = skuEle.select(".p-name > a > em").first().text();
                jdData.setTitle(title);

                //获取详情页Url
                String href = "https:"+skuEle.select(".p-name > a").attr("href");
                jdData.setDetailsUrl(href);

                //写入创建时间
                jdData.setCreateTime(new Date());

                //保存商品数据到数据库中
                try {
                    jdDateService.insertJdData(jdData);

                    logger.info("成功爬取\t " + title + "\t的基本信息 ");
                    logger.info("商品信息:\t"+JSON.toJSONString(jdData));
                }catch (Exception e){
                    logger.debug("爬取\t " + title + "\t失败,原因: "+e.getMessage());
                    logger.debug("商品信息:\t"+JSON.toJSONString(jdData));
                }

            }
        }
    }

}
