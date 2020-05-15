package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
public class PrimaryDataSourceExample {
    @Autowired
    //@Qualifier("primaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/primaryDsExample")
    public String hello() {
        String str;

        try {
            str = (new java.util.Date()).getTime() + ": this is com.example.demo.controller.PrimaryDataSourceExample.hello() print out";
            str += JdbcTest1();
        } catch (Exception e) {
            str = e.getMessage();
            e.printStackTrace();
        }
        return str;
    }

    private String JdbcTest1() throws SQLException {
        String sql, key, name;
        StringBuilder sb = new StringBuilder();

        SqlRowSet rowSet;

        sql = "SELECT * FROM tb1";
        rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()) {
            key = rowSet.getString("key");
            name = rowSet.getString("name");
            sb.append("\r\nkey = " + key + ", name = " + name);
        }

        return sb.toString();
    }
}