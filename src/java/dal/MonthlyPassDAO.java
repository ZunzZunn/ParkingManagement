/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author ZunzZunn
 */
public class MonthlyPassDAO extends DBContext {

    public int getExpiringMonthlyPasses() {
        String sql = "SELECT COUNT(*) FROM MonthlyPasses WHERE DATEDIFF(day, GETDATE(), EndDate) <= 7";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
