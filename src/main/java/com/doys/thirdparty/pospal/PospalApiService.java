/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-11-19
 * 绿客门POS(银豹收银)系统接口服务类
 *****************************************************************************/
package com.doys.thirdparty.pospal;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.core.ex.UnexpectedException;
import com.doys.framework.database.DBFactory;
import com.doys.framework.database.dtb.DataTable;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
public class PospalApiService extends BaseService {
    public static void pullProduct(DBFactory dbBus) throws Exception {
        String content, dataSignature;

        HashMap<String, String> post = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        PolpalApiStructure categoryPages;
        // -- 1. 调用POS 接口 ---------------------------------
        post.put("appId", PospalUtil.appId);
        content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(post);
        dataSignature = PospalUtil.encryptToMd5String(content, PospalUtil.appKey);

        WebClient webClient = WebClient.create();
        Mono<PolpalApiStructure> mono = webClient.post()
            .uri(PospalUtil.urlQueryProductCategoryPages)
            .header("time-stamp", PospalUtil.getTimeStamp())
            .header("data-signature", dataSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(content)
            .retrieve()
            .bodyToMono(PolpalApiStructure.class);
        categoryPages = mono.block();
        if (!categoryPages.status.equals("success")) {
            throw new UnexpectedException("接口 queryProductCategoryPages 调用失败, " + Arrays.toString(categoryPages.messages));
        }

        // -- 2. 插入接口数据 -----------------------------------
        int nFind, nInsert = 0, nUpdate = 0;

        String sql, uid, name;
        String[] arrFind = new String[1];
        ArrayList<String> listNew = new ArrayList<>();

        DataTable dtbProduct;
        DataTable.DataRow drNew;

        sql = "SELECT ref_uk, id, name FROM base_product ORDER BY ref_uk";
        dtbProduct = dbBus.getDataTable(sql);
        dtbProduct.Sort("ref_uk");

        HashMap<String, String> mapCategory;
        ArrayList<LinkedHashMap<String, String>> list = (ArrayList<LinkedHashMap<String, String>>) categoryPages.data.get("result");
        for (int i = 0; i < list.size(); i++) {
            mapCategory = list.get(i);
            uid = String.valueOf(mapCategory.get("uid"));
            name = String.valueOf(mapCategory.get("name"));

            arrFind[0] = uid;
            nFind = dtbProduct.Find(arrFind);
            if (nFind >= 0) {
                if (!dtbProduct.DataCell(nFind, "name").equals(name)) {
                    dtbProduct.setDataCell(nFind, "name", name);
                    nUpdate++;
                }
            }
            else {
                drNew = dtbProduct.NewRow();
                drNew.setDataCell("ref_uk", uid);
                drNew.setDataCell("name", name);
                dtbProduct.AddRow(drNew);
                nInsert++;
                listNew.add(uid);
            }
        }
        dtbProduct.Update(dbBus, "base_product", "id");
        logger.info("接口queryProductCategoryPages拉取数据成功，新增" + nInsert + "条记录，更新" + nUpdate + "条记录。");

        // -- 3. 自动初始化产品参数 --------------------------------
        if (listNew.size() > 0) {
            addProductPara(dbBus, listNew);
        }
    }
    private static void addProductPara(DBFactory dbBus, ArrayList<String> listNew) throws Exception {
        int productId;

        String sql;
        String[][] arrPara = new String[2][2];

        StringBuilder builder = new StringBuilder();
        SqlRowSet rsProduct;
        // ------------------------------------------------
        arrPara[0][0] = "name";
        arrPara[0][1] = "中文名称";
        arrPara[1][0] = "pinyin";
        arrPara[1][1] = "英文名称";

        for (int i = 0; i < listNew.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append("'" + listNew.get(i) + "'");
        }

        sql = "SELECT id FROM base_product WHERE ref_uk IN (" + builder.toString() + ")";
        rsProduct = dbBus.getRowSet(sql);
        while (rsProduct.next()) {
            productId = rsProduct.getInt("id");

            for (int i = 0; i < arrPara.length; i++) {
                sql = "INSERT INTO base_product_para (product_id, code, name) VALUES (?, ?, ?)";
                dbBus.exec(sql, productId, arrPara[i][0], arrPara[i][1]);
            }
        }
    }

    public static void pullProductPns(DBFactory dbBus) throws Exception {
        int nFind, productId, productParaId, productPnId, nInsert, nUpdate;

        String sql, uid, refUk, name;
        String productParaCode, productParaName, productParaValue;
        String[] arrFind = new String[1];

        SqlRowSet rsProduct;
        ArrayList<LinkedHashMap<String, String>> list;
        HashMap<String, String> mapProduct;

        DataTable dtbProductPn;
        SqlRowSet rsProductPara;
        // ------------------------------------------------
        sql = "SELECT ref_uk, id, name FROM base_product WHERE ref_uk IS NOT NULL ORDER BY ref_uk";
        rsProduct = dbBus.getRowSet(sql);
        while (rsProduct.next()) {
            productId = rsProduct.getInt("id");
            refUk = rsProduct.getString("ref_uk");
            list = pullProductPn(refUk);
            if (list.size() > 0) {
                nInsert = 0;
                nUpdate = 0;
                sql = "SELECT * FROM base_product_para WHERE product_id = ?";
                rsProductPara = dbBus.getRowSet(sql, productId);

                sql = "SELECT ref_uk, product_id, id, pn FROM base_product_pn WHERE product_id = ? ORDER BY ref_uk";
                dtbProductPn = dbBus.getDataTable(sql, productId);
                dtbProductPn.Sort("ref_uk");
                for (int i = 0; i < list.size(); i++) {
                    mapProduct = list.get(i);
                    uid = String.valueOf(mapProduct.get("uid"));
                    name = String.valueOf(mapProduct.get("name"));

                    arrFind[0] = uid;
                    nFind = dtbProductPn.Find(arrFind);
                    if (nFind >= 0) {
                        if (!dtbProductPn.DataCell(nFind, "pn").equals(name)) {
                            sql = "UPDATE base_product_pn SET pn = ? WHERE id = ?";
                            dbBus.exec(sql, name, dtbProductPn.DataCell(nFind, "id"));
                            nUpdate++;
                        }
                    }
                    else {
                        sql = "INSERT INTO base_product_pn (ref_uk, product_id, pn) VALUES (?, ?, ?)";
                        dbBus.exec(sql, uid, productId, name);
                        productPnId = dbBus.getInt("SELECT @@identity");
                        nInsert++;

                        // -- 产品料号参数赋值 --
                        rsProductPara.beforeFirst();
                        while (rsProductPara.next()) {
                            productParaId = rsProductPara.getInt("id");
                            productParaCode = rsProductPara.getString("code");
                            productParaName = rsProductPara.getString("name");
                            productParaValue = String.valueOf(mapProduct.get(productParaCode));
                            sql = "INSERT INTO base_product_pn_para (product_id, product_para_id, product_pn_id, para_code, para_name, para_value)" +
                                "VALUES (?, ?, ?, ?, ?, ?)";
                            dbBus.exec(sql, productId, productParaId, productPnId, productParaCode, productParaName, productParaValue);
                        }
                    }
                }
                logger.info("接口queryProductPages拉取数据成功，categoryUid = " + refUk + ", 新增" + nInsert + "条记录，更新" + nUpdate + "条记录。");
            }

            if (true) {
                break;  // -- 仅供测试用 --
            }
        }
    }
    private static ArrayList<LinkedHashMap<String, String>> pullProductPn(String catagoryUid) throws Exception {
        String content, dataSignature;

        HashMap<String, String> post = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        PolpalApiStructure productPages;
        // ------------------------------------------------
        post.put("appId", PospalUtil.appId);
        post.put("categoryUid", catagoryUid);

        content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(post);
        dataSignature = PospalUtil.encryptToMd5String(content, PospalUtil.appKey);

        WebClient webClient = WebClient.create();
        Mono<PolpalApiStructure> mono = webClient.post()
            .uri(PospalUtil.urlQueryProductPages)
            .header("time-stamp", PospalUtil.getTimeStamp())
            .header("data-signature", dataSignature)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(content)
            .retrieve()
            .bodyToMono(PolpalApiStructure.class);
        productPages = mono.block();
        if (!productPages.status.equals("success")) {
            throw new UnexpectedException("接口 queryProductPages 调用失败, " + Arrays.toString(productPages.messages));
        }

        // ------------------------------------------------
        ArrayList<LinkedHashMap<String, String>> list = (ArrayList<LinkedHashMap<String, String>>) productPages.data.get("result");
        return list;
    }

    // -- 测试维护用 ---------------------------------------------------------------
    public static void clearInvalidData(DBFactory dbBus) throws Exception {
        String sql;

        sql = "DELETE FROM base_product_pn_para WHERE product_id NOT IN (SELECT id FROM base_product) " +
            "OR product_para_id NOT IN (SELECT id FROM base_product_para) " +
            "OR product_pn_id NOT IN (SELECT id FROM base_product_pn)";
        dbBus.exec(sql);

        sql = "DELETE FROM base_product_para WHERE product_id NOT IN (SELECT id FROM base_product)";
        dbBus.exec(sql);

        sql = "DELETE FROM base_product_pn WHERE product_id NOT IN (SELECT id FROM base_product)";
        dbBus.exec(sql);
    }
}