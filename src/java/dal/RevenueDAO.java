/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import model.RevenueDTO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ZunzZunn
 */
public class RevenueDAO extends DBContext {
// 1. Lấy tổng doanh thu vé vãng lai trong ngày hôm nay

    public double getDailyTicketRevenueToday() {
        String sql = "SELECT ISNULL(SUM(TotalFee), 0) AS Revenue FROM Tickets "
                + "WHERE IsMonthlyPass = 0 AND Status = 'Completed' "
                + "AND CAST(CheckOutTime AS DATE) = CAST(GETDATE() AS DATE)";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getDouble("Revenue");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    // 2. Lấy doanh thu vé tháng trong tháng này (Dựa vào lịch sử gia hạn)
    public double getMonthlyPassRevenueThisMonth() {
        String sql = "SELECT ISNULL(SUM(rh.DurationMonths * vt.PricePerMonth), 0) AS Revenue "
                + "FROM RenewalHistory rh "
                + "JOIN MonthlyPasses mp ON rh.PassID = mp.PassID "
                + "JOIN VehicleTypes vt ON mp.TypeID = vt.TypeID "
                + "WHERE MONTH(rh.RenewDate) = MONTH(GETDATE()) AND YEAR(rh.RenewDate) = YEAR(GETDATE())";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getDouble("Revenue");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    // 3. Lấy tổng doanh thu tất cả (Vé lượt + Vé tháng) trong tháng này
    public double getTotalRevenueThisMonth() {
        String sqlTicket = "SELECT ISNULL(SUM(TotalFee), 0) AS Rev FROM Tickets "
                + "WHERE IsMonthlyPass = 0 AND Status = 'Completed' "
                + "AND MONTH(CheckOutTime) = MONTH(GETDATE()) AND YEAR(CheckOutTime) = YEAR(GETDATE())";
        double ticketRev = 0;
        try {
            PreparedStatement st = connection.prepareStatement(sqlTicket);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                ticketRev = rs.getDouble("Rev");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return ticketRev + getMonthlyPassRevenueThisMonth();
    }

    // 4. Lấy dữ liệu biểu đồ: Doanh thu 7 ngày gần nhất (Vé vãng lai)
    public Map<String, Double> getRevenueLast7Days() {
        Map<String, Double> chartData = new LinkedHashMap<>();
        String sql = "SELECT TOP 7 CAST(CheckOutTime AS DATE) as Date, ISNULL(SUM(TotalFee), 0) as Revenue "
                + "FROM Tickets WHERE IsMonthlyPass = 0 AND Status = 'Completed' "
                + "AND CheckOutTime >= DATEADD(day, -6, CAST(GETDATE() AS DATE)) "
                + "GROUP BY CAST(CheckOutTime AS DATE) ORDER BY Date ASC";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                // Key là ngày (YYYY-MM-DD), Value là doanh thu
                chartData.put(rs.getString("Date"), rs.getDouble("Revenue"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return chartData;
    }

    // 1. Đếm tổng số lượng giao dịch (Có áp dụng bộ lọc)
    public int getTotalRevenueCount(String fromDate, String toDate, String source, String search) {
        String sql = "SELECT COUNT(*) FROM ( "
                + "  SELECT 'TICKET-' + CAST(TicketID AS VARCHAR) as ID, N'Vé vãng lai' as Source, LicensePlate as Description, TotalFee as Amount, CheckOutTime as Date "
                + "  FROM Tickets WHERE Status = 'Completed' AND IsMonthlyPass = 0 "
                + "  UNION ALL "
                + "  SELECT 'PASS-' + CAST(HistoryID AS VARCHAR) as ID, N'Gia hạn vé tháng' as Source, mp.CustomerName as Description, (rh.DurationMonths * vt.PricePerMonth) as Amount, RenewDate as Date "
                + "  FROM RenewalHistory rh JOIN MonthlyPasses mp ON rh.PassID = mp.PassID JOIN VehicleTypes vt ON mp.TypeID = vt.TypeID "
                + ") AS CombinedTable WHERE 1=1 ";

        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql += " AND CAST(Date AS DATE) >= '" + fromDate + "'";
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql += " AND CAST(Date AS DATE) <= '" + toDate + "'";
        }
        if (source != null && !source.trim().isEmpty() && !source.equals("All")) {
            sql += " AND Source = N'" + source + "'";
        }
        if (search != null && !search.trim().isEmpty()) {
            sql += " AND Description LIKE N'%" + search + "%'";
        }

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    // 2. Lấy danh sách phân trang (Có áp dụng bộ lọc)
    public List<RevenueDTO> getRevenuesByPage(int page, int pageSize, String fromDate, String toDate, String source, String search) {
        List<RevenueDTO> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM ( "
                + "  SELECT 'TICKET-' + CAST(TicketID AS VARCHAR) as ID, N'Vé vãng lai' as Source, LicensePlate as Description, TotalFee as Amount, CheckOutTime as Date "
                + "  FROM Tickets WHERE Status = 'Completed' AND IsMonthlyPass = 0 "
                + "  UNION ALL "
                + "  SELECT 'PASS-' + CAST(HistoryID AS VARCHAR) as ID, N'Gia hạn vé tháng' as Source, mp.CustomerName as Description, (rh.DurationMonths * vt.PricePerMonth) as Amount, RenewDate as Date "
                + "  FROM RenewalHistory rh JOIN MonthlyPasses mp ON rh.PassID = mp.PassID JOIN VehicleTypes vt ON mp.TypeID = vt.TypeID "
                + ") AS CombinedTable WHERE 1=1 ";

        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql += " AND CAST(Date AS DATE) >= '" + fromDate + "'";
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql += " AND CAST(Date AS DATE) <= '" + toDate + "'";
        }
        if (source != null && !source.trim().isEmpty() && !source.equals("All")) {
            sql += " AND Source = N'" + source + "'";
        }
        if (search != null && !search.trim().isEmpty()) {
            sql += " AND Description LIKE N'%" + search + "%'";
        }

        sql += " ORDER BY Date DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, offset);
            st.setInt(2, pageSize);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(new RevenueDTO(
                        rs.getString("ID"),
                        rs.getString("Source"),
                        rs.getString("Description"),
                        rs.getDouble("Amount"),
                        rs.getTimestamp("Date")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }
}
