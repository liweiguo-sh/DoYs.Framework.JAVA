package com.doys.example;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.system.service.ViewService;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/once")
public class JustDo extends BaseController {
    @RequestMapping("/a")
    public RestResult JustDoIt(@RequestBody Map<String, String> req) {
        int nMax = 1;
        try {
            nMax = Integer.parseInt(req.get("nMaxCount"));
            for (int i = 1; i <= nMax; i++) {
                logger.info("第 " + i + " 次执行开始 ...");

                ViewService.refreshViewField(dbSys, "label");

                logger.info("第 " + i + " 次执行完毕.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultErr(e);
        }
        return ResultOk();
    }
    @RequestMapping("/b")
    public RestResult b(@RequestBody Map<String, String> req) {

        int nMax = 1;
        try {
            nMax = Integer.parseInt(req.get("nMaxCount"));
            dbSys.exec("DELETE FROM customer");

            int[] result;
            String sql = "INSERT INTO customer (id, name, age) VALUES (?, ?, ?)";
            ArrayList<Object[]> list = new ArrayList<>();
            for (int i = 0; i < nMax; i++) {
                Object[] obj = new Object[3];
                obj[0] = i;
                obj[1] = "张三_" + i;
                obj[2] = i;
                list.add(obj);
            }
            logger.info("begin");
            result = dbSys.batchUpdate(sql, list);
            logger.info("end");
            if (nMax > 0) {
                return ResultOk();
            }


            for (int i = 0; i < nMax; i++) {
                dbSys.exec("DELETE FROM customer");

                ArrayList<Customer> customers = new ArrayList<Customer>();
                nMax = Integer.parseInt(req.get("nMaxCount"));
                for (int j = 1; j <= 30000; j++) {
                    Customer c = new Customer();
                    c.setCustId(i * 10000 + j);
                    c.setAge(25);
                    c.setName("张三_" + j);
                    customers.add(c);
                }
                logger.info("第 " + (i + 1) + " 开始执行");
                insertBatch(dbSys, customers);
                logger.info("第 " + (i + 1) + " 次执行完毕");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultErr(e);
        }
        return ResultOk();
    }

    public void insertBatch(JdbcTemplate jt, final List<Customer> customers) {
        String sql = "INSERT INTO customer (id, name, age) VALUES (?, ?, ?)";
        jt.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Customer customer = customers.get(i);
                ps.setLong(1, customer.getCustId());
                ps.setString(2, customer.getName());
                ps.setInt(3, customer.getAge());
            }

            @Override
            public int getBatchSize() {
                return customers.size();
            }
        });
    }
}