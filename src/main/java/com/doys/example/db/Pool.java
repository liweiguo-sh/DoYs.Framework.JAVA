/**
 * 数据库连接池测试
 */
package com.doys.example.db;
import com.doys.framework.common.UtilDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example/dbpool")
public class Pool {
    //region -- 模块变量定义 --
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdbcTemplate jdbcAprint;
    //endregion
    @RequestMapping("/")
    public String HelloWorld() {
        showTable_aprint();

        return "dbpool: " + UtilDate.getDateTimeStr(null, "yyyy-MM-dd HH:mm:ss.SSS");
    }

    private void showTable_aprint() {
        int nMax = 100;
        String sql = "SELECT * FROM T_LABEL";

        logger.info("\n\nshow aprint.T_LABEL records:");
        for (int i = 0; i < nMax; i++) {
            SqlRowSet rowSet = jdbcAprint.queryForRowSet(sql);
            while (rowSet.next()) {
                logger.info("test");
            }
            logger.info("第 " + i + "次：rowSet ok" + UtilDate.getDateTimeStr(null, null));
        }
    }
}