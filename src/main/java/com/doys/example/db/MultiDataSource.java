/**
 * 多数据源测试
 */
package com.doys.example.db;
import com.doys.framework.core.db.DBFactory;
import com.doys.framework.util.UtilDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example/multiDataSource")
public class MultiDataSource {
    //region -- 模块变量定义 --
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdbcTemplate jdbcAprint;
    @Autowired
    // TODO: 研究一下
    @Qualifier("sysDBFactory")
    private DBFactory jdbcDb1;
    @Autowired
    @Qualifier("busDBFactory")
    private DBFactory jdbcDb2;
    //endregion
    @RequestMapping("/")
    public String HelloWorld() {
        showTable_aprint();

        showTable_db1();
        showTable_db2();

        return "ok: " + UtilDate.getDateTimeStr(null, "yyyy-MM-dd HH:mm:ss.SSS");
    }

    private void showTable_aprint() {
        logger.info("\n\nshow aprint.T_LABEL records:");

        String sql = "SELECT * FROM T_LABEL";
        SqlRowSet rowSet = jdbcAprint.queryForRowSet(sql);
        while (rowSet.next()) {
            logger.info("label_id = " + rowSet.getInt("label_id") + ",  label_name = " + rowSet.getString("label_name"));
        }
    }
    private void showTable_db1() {
        logger.info("\n\nshow db1.tb1 records:");

        String sql = "SELECT * FROM tb1";
        SqlRowSet rowSet = jdbcDb1.queryForRowSet(sql);
        while (rowSet.next()) {
            logger.info("field1 = " + rowSet.getInt("id") + ",  field1 = " + rowSet.getString("field1"));
        }
    }
    private void showTable_db2() {
        logger.info("\n\nshow db2.tb2 records:");

        String sql = "SELECT * FROM tb2";
        SqlRowSet rowSet = jdbcDb2.queryForRowSet(sql);
        while (rowSet.next()) {
            logger.info("field1 = " + rowSet.getInt("id") + ",  field2 = " + rowSet.getString("field2"));
        }
    }
}