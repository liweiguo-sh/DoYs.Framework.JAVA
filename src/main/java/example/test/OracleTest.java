package example.test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
public class OracleTest {
    public static void main(String[] args) {
        String sql;
        String url = "jdbc:oracle:thin:@(description=(address=(protocol=tcp)(port=1521)(host=192.168.169.230))(connect_data=(service_name=orcl)))";
        String driverClassName = "oracle.jdbc.driver.OracleDriver";
        String username = "backward", password = "backward";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        // ------------------------------------------------
        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, username, password);

            sql = "SELECT f1 FROM tb1";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println("F1 = " + rs.getString("f1"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}