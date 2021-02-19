package example.demo.controller;
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
public class HelloWorld {
    @Autowired
    @Qualifier("primaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate1;

    @Autowired
    @Qualifier("secondaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate2;

    @RequestMapping("/hello")
    public String hello() {
        String str;

        str = "Hello world, 111 " + (new java.util.Date()).getTime();

        try {
            JdbcTest1();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private void JdbcTest1() throws SQLException {
        String sql, key, name;
        SqlRowSet rowSet;

        DataSource ds = jdbcTemplate1.getDataSource();
        Connection conn = ds.getConnection();

        sql = "SELECT * FROM tb1";
        rowSet = jdbcTemplate1.queryForRowSet(sql);
        while (rowSet.next()) {
            key = rowSet.getString("key");
            name = rowSet.getString("name");
            System.out.println("key = " + key + ", name = " + name);
        }

        sql = "SELECT * FROM user";
        rowSet = jdbcTemplate2.queryForRowSet(sql);
        while (rowSet.next()) {
            key = rowSet.getString("id");
            name = rowSet.getString("name");
            System.out.println("key = " + key + ", name = " + name);
        }

        //sql = "UPDATE tb1 SET name = CONCAT('ABC', '_', '" + (new java.util.Date()).getTime() + "')";
        //jdbcTemplate1.execute(sql);
    }
}