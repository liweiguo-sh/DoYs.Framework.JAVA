package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class DBTransTest1 {
    @Autowired
    @Qualifier("primaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate1;

    @Autowired
    DataSourceTransactionManager dstm;
    @Autowired
    TransactionDefinition tDef;

    @RequestMapping("/dtt1")
    public String hello() {
        String str;

        str = "Hello world, 111 " + (new java.util.Date()).getTime();

        try {
            JdbcTest1();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private void JdbcTest1() throws SQLException {
        String sql;
        TransactionStatus tStatus = null;

        sql = "UPDATE tb1 SET name = CONCAT('ABC', '_', '" + (new java.util.Date()).getTime() + "')";
        tStatus = dstm.getTransaction(tDef);
        jdbcTemplate1.execute(sql);
        dstm.commit(tStatus);
    }
}