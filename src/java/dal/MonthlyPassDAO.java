/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import model.MonthlyPass;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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

    // Nâng cấp hàm: Nhận thêm 3 tham số từ bộ lọc
    public java.util.List<model.MonthlyPassDTO> getAllPasses(String licensePlate, String customerInfo, String status) {
        java.util.List<model.MonthlyPassDTO> list = new java.util.ArrayList<>();

        // Dùng StringBuilder để nối câu lệnh SQL động
        StringBuilder sql = new StringBuilder(
                "SELECT m.*, u.FullName, u.PhoneNumber "
                + "FROM MonthlyPasses m "
                + "LEFT JOIN Users u ON m.UserID = u.UserID "
                + "WHERE 1=1 "
        );

        // 1. Lọc theo biển số
        if (licensePlate != null && !licensePlate.trim().isEmpty()) {
            sql.append(" AND m.LicensePlate LIKE ? ");
        }
        // 2. Lọc theo Tên hoặc SĐT
        if (customerInfo != null && !customerInfo.trim().isEmpty()) {
            sql.append(" AND (u.FullName LIKE ? OR u.PhoneNumber LIKE ?) ");
        }
        // 3. Lọc theo Trạng thái (Dùng hàm DATEDIFF chuẩn của SQL Server)
        if ("Expired".equals(status)) {
            sql.append(" AND (m.IsActive = 0 OR DATEDIFF(day, GETDATE(), m.EndDate) < 0) ");
        } else if ("ExpiringSoon".equals(status)) {
            sql.append(" AND m.IsActive = 1 AND DATEDIFF(day, GETDATE(), m.EndDate) BETWEEN 0 AND 5 ");
        } else if ("Active".equals(status)) {
            sql.append(" AND m.IsActive = 1 AND DATEDIFF(day, GETDATE(), m.EndDate) > 5 ");
        }

        sql.append(" ORDER BY m.EndDate DESC");

        try {
            java.sql.PreparedStatement st = connection.prepareStatement(sql.toString());
            int paramIndex = 1;

            // Nạp giá trị cho các dấu ? trong SQL
            if (licensePlate != null && !licensePlate.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + licensePlate.trim() + "%");
            }
            if (customerInfo != null && !customerInfo.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + customerInfo.trim() + "%");
                st.setString(paramIndex++, "%" + customerInfo.trim() + "%");
            }

            java.sql.ResultSet rs = st.executeQuery();
            while (rs.next()) {
                model.MonthlyPassDTO dto = new model.MonthlyPassDTO();
                dto.setPassID(rs.getInt("PassID"));
                dto.setUserID(rs.getInt("UserID"));
                dto.setSlotID(rs.getInt("SlotID"));
                dto.setLicensePlate(rs.getString("LicensePlate"));
                dto.setTypeID(rs.getInt("TypeID"));
                dto.setStartDate(rs.getDate("StartDate"));
                dto.setEndDate(rs.getDate("EndDate"));
                dto.setIsActive(rs.getBoolean("IsActive"));
                dto.setCustomerName(rs.getString("FullName"));
                dto.setPhoneNumber(rs.getString("PhoneNumber"));

                // Tính toán lại huy hiệu (Badge) cho giao diện
                java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
                long diff = rs.getDate("EndDate").getTime() - today.getTime();
                long daysLeft = diff / (1000 * 60 * 60 * 24);

                if (!rs.getBoolean("IsActive") || daysLeft < 0) {
                    dto.setStatusBadge("Expired");
                } else if (daysLeft <= 5) {
                    dto.setStatusBadge("ExpiringSoon");
                } else {
                    dto.setStatusBadge("Active");
                }

                list.add(dto);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}
