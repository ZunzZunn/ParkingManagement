/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.RevenueLog;

public class RevenueDAO extends DBContext {

    // 1. Tính tổng doanh thu theo loại (Ticket hoặc Monthly) trong khoảng thời gian
    public double getRevenueByType(String type, String fromDate, String toDate) {
        double total = 0;
        String sql = "";

        // Giả định bạn có bảng Transactions (Vé lượt) và bảng MonthlyPasses (Vé tháng)
        // Cột Amount/Price lưu số tiền, CheckOutTime lưu giờ ra, CreatedAt lưu giờ đăng ký vé tháng
        if ("Ticket".equals(type)) {
            sql = "SELECT SUM(Amount) AS Total FROM Transactions WHERE Status = 'Completed' ";
            if (fromDate != null && !fromDate.isEmpty()) {
                sql += " AND CAST(CheckOutTime AS DATE) >= ? ";
            }
            if (toDate != null && !toDate.isEmpty()) {
                sql += " AND CAST(CheckOutTime AS DATE) <= ? ";
            }
        } else {
            sql = "SELECT SUM(Price) AS Total FROM MonthlyPasses WHERE 1=1 ";
            if (fromDate != null && !fromDate.isEmpty()) {
                sql += " AND CAST(CreatedAt AS DATE) >= ? ";
            }
            if (toDate != null && !toDate.isEmpty()) {
                sql += " AND CAST(CreatedAt AS DATE) <= ? ";
            }
        }

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            int paramIndex = 1;
            if (fromDate != null && !fromDate.isEmpty()) {
                st.setString(paramIndex++, fromDate);
            }
            if (toDate != null && !toDate.isEmpty()) {
                st.setString(paramIndex, toDate);
            }

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("Total");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tính doanh thu " + type + ": " + e.getMessage());
        }
        return total;
    }

    // 2. Lấy danh sách lịch sử dòng tiền (Gộp cả vé tháng và vé lượt lại)
    public List<RevenueLog> getRevenueLogs(String fromDate, String toDate) {
        List<RevenueLog> logs = new ArrayList<>();

        // Dùng UNION ALL để gộp 2 bảng dữ liệu lại với nhau, sắp xếp theo thời gian mới nhất
        String sql = "SELECT CAST(TransactionID AS VARCHAR) AS ID, CheckOutTime AS Time, 'Ticket' AS Type, 'Thu phí đỗ xe: ' + LicensePlate AS DescText, Amount "
                + "FROM Transactions WHERE Status = 'Completed' "
                + "UNION ALL "
                + "SELECT CAST(PassID AS VARCHAR) AS ID, CreatedAt AS Time, 'Monthly' AS Type, 'Đăng ký vé tháng: ' + LicensePlate AS DescText, Price AS Amount "
                + "FROM MonthlyPasses "
                + "ORDER BY Time DESC";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                // Lọc bằng code Java nếu có fromDate/toDate (Hoặc bạn có thể đẩy thẳng điều kiện WHERE vào câu UNION ở trên)
                logs.add(new RevenueLog(
                        rs.getString("ID"),
                        rs.getTimestamp("Time"),
                        rs.getString("Type"),
                        rs.getString("DescText"),
                        rs.getDouble("Amount")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getRevenueLogs: " + e.getMessage());
        }

        return logs;
    }
}
