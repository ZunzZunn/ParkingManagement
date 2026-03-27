/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import model.MonthlyPass;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        // Đã bỏ LEFT JOIN với bảng Users, lấy trực tiếp từ bảng MonthlyPasses
        StringBuilder sql = new StringBuilder(
                "SELECT m.*, p.SlotCode FROM MonthlyPasses m "
                + "LEFT JOIN ParkingSlots p ON m.SlotID = p.SlotID "
                + "WHERE 1=1 "
        );

        // 1. Lọc theo biển số
        if (licensePlate != null && !licensePlate.trim().isEmpty()) {
            sql.append(" AND m.LicensePlate LIKE ? ");
        }
        // 2. Lọc theo Tên hoặc SĐT (Sử dụng trực tiếp CustomerName và PhoneNumber trong bảng m)
        if (customerInfo != null && !customerInfo.trim().isEmpty()) {
            sql.append(" AND (m.CustomerName LIKE ? OR m.PhoneNumber LIKE ?) ");
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
                dto.setPassId(rs.getInt("PassID"));

                // Đã xóa dòng dto.setUserID(...) vì database không còn UserID nữa
                dto.setSlotId(rs.getInt("SlotID"));
                String slotCode = rs.getString("SlotCode");
                dto.setSlotCode(slotCode != null ? slotCode : "Chưa xếp");
                dto.setLicensePlate(rs.getString("LicensePlate"));
                dto.setTypeId(rs.getInt("TypeID"));
                dto.setStartDate(rs.getDate("StartDate"));
                dto.setEndDate(rs.getDate("EndDate"));
                dto.setIsActive(rs.getBoolean("IsActive"));

                // Thay đổi "FullName" thành "CustomerName" cho khớp với cột trong Database
                dto.setCustomerName(rs.getString("CustomerName"));
                dto.setPhoneNumber(rs.getString("PhoneNumber"));

                // Tính toán lại huy hiệu (Badge) cho giao diện (Giữ nguyên logic của bạn)
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

    // Cập nhật hàm addMonthlyPass: Thêm tham số staffId và ghi vào RenewalHistory
    public boolean addMonthlyPass(String customerName, String phoneNumber, int slotId, String licensePlate, int typeId, int durationMonths, int staffId) {
        String insertPassSql = "INSERT INTO MonthlyPasses (CustomerName, PhoneNumber, SlotID, LicensePlate, TypeID, StartDate, EndDate, IsActive) "
                + "VALUES (?, ?, ?, ?, ?, GETDATE(), DATEADD(month, ?, GETDATE()), 1)";
        try {
            // Dùng RETURN_GENERATED_KEYS để lấy ID của vé vừa được tạo
            java.sql.PreparedStatement st = connection.prepareStatement(insertPassSql, java.sql.Statement.RETURN_GENERATED_KEYS);
            st.setNString(1, customerName);
            st.setString(2, phoneNumber);
            st.setInt(3, slotId);
            st.setString(4, licensePlate);
            st.setInt(5, typeId);
            st.setInt(6, durationMonths);

            int affectedRows = st.executeUpdate();

            if (affectedRows > 0) {
                // Lấy PassID tự tăng vừa được Database tạo ra
                java.sql.ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int newPassId = rs.getInt(1);

                    // Lấy Ngày hết hạn (EndDate) vừa được tính toán trong DB
                    String getEndSql = "SELECT EndDate FROM MonthlyPasses WHERE PassID = ?";
                    java.sql.PreparedStatement stEnd = connection.prepareStatement(getEndSql);
                    stEnd.setInt(1, newPassId);
                    java.sql.ResultSet rsEnd = stEnd.executeQuery();

                    if (rsEnd.next()) {
                        java.sql.Date newEnd = rsEnd.getDate("EndDate");

                        // GHI NHẬN DOANH THU LẦN ĐẦU VÀO RENEWAL HISTORY
                        String insertHistorySql = "INSERT INTO RenewalHistory (PassID, DurationMonths, NewEndDate, RenewedBy) VALUES (?, ?, ?, ?)";
                        java.sql.PreparedStatement stHist = connection.prepareStatement(insertHistorySql);
                        stHist.setInt(1, newPassId);
                        stHist.setInt(2, durationMonths);
                        stHist.setDate(3, newEnd);
                        stHist.setInt(4, staffId); // ID của nhân viên lập vé
                        stHist.executeUpdate();
                    }
                }
                return true;
            }
        } catch (Exception e) {
            System.out.println("Lỗi addMonthlyPass: " + e.getMessage());
        }
        return false;
    }

    // 2. Gia hạn vé tháng
    public boolean renewMonthlyPass(int passId, int durationMonths, int renewedBy) {
        String updateSql = "UPDATE MonthlyPasses SET "
                + "EndDate = CASE "
                + "  WHEN EndDate < GETDATE() THEN DATEADD(month, ?, GETDATE()) "
                + "  ELSE DATEADD(month, ?, EndDate) "
                + "END, IsActive = 1 "
                + "WHERE PassID = ?";
        try {
            java.sql.PreparedStatement st1 = connection.prepareStatement(updateSql);
            st1.setInt(1, durationMonths);
            st1.setInt(2, durationMonths);
            st1.setInt(3, passId);
            int rows = st1.executeUpdate();

            if (rows > 0) {
                String getEndSql = "SELECT EndDate FROM MonthlyPasses WHERE PassID = ?";
                java.sql.PreparedStatement st2 = connection.prepareStatement(getEndSql);
                st2.setInt(1, passId);
                java.sql.ResultSet rs = st2.executeQuery();
                if (rs.next()) {
                    java.sql.Date newEnd = rs.getDate("EndDate");

                    // Thêm RenewedBy vào câu lệnh INSERT
                    String insertHistorySql = "INSERT INTO RenewalHistory (PassID, DurationMonths, NewEndDate, RenewedBy) VALUES (?, ?, ?, ?)";
                    java.sql.PreparedStatement st3 = connection.prepareStatement(insertHistorySql);
                    st3.setInt(1, passId);
                    st3.setInt(2, durationMonths);
                    st3.setDate(3, newEnd);
                    st3.setInt(4, renewedBy); // Truyền ID nhân viên vào
                    st3.executeUpdate();
                }
                return true;
            }
        } catch (Exception e) {
            System.out.println("Lỗi renewMonthlyPass: " + e.getMessage());
        }
        return false;
    }

    // 2. THÊM HÀM MỚI: Lấy danh sách ô đỗ còn trống để hiển thị ở Form
    public java.util.List<java.util.Map<String, Object>> getAvailableSlots() {
        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
        String sql = "SELECT SlotID, SlotCode, Zone, TypeID FROM ParkingSlots WHERE Status = 'Available'";
        try {
            java.sql.PreparedStatement st = connection.prepareStatement(sql);
            java.sql.ResultSet rs = st.executeQuery();
            while (rs.next()) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("slotId", rs.getInt("SlotID"));
                map.put("slotCode", rs.getString("SlotCode"));
                map.put("zone", rs.getNString("Zone"));
                map.put("typeId", rs.getInt("TypeID"));
                list.add(map);
            }
        } catch (Exception e) {
        }
        return list;
    }

    // 3. THÊM HÀM MỚI: Đổi trạng thái ô đỗ thành Reserved sau khi khách đăng ký
    public void updateSlotStatus(int slotId, String status) {
        String sql = "UPDATE ParkingSlots SET Status = ? WHERE SlotID = ?";
        try {
            java.sql.PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, status);
            st.setInt(2, slotId);
            st.executeUpdate();
        } catch (Exception e) {
        }
    }

    // 2. Thêm hàm mới để lấy danh sách lịch sử
    public java.util.List<model.RenewalHistory> getRenewalHistory(int passId) {
        java.util.List<model.RenewalHistory> list = new java.util.ArrayList<>();
        // JOIN để lấy FullName của người thao tác
        String sql = "SELECT r.*, u.FullName FROM RenewalHistory r "
                + "LEFT JOIN Users u ON r.RenewedBy = u.UserID "
                + "WHERE r.PassID = ? ORDER BY r.RenewDate DESC";
        try {
            java.sql.PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, passId);
            java.sql.ResultSet rs = st.executeQuery();
            while (rs.next()) {
                model.RenewalHistory h = new model.RenewalHistory();
                h.setHistoryId(rs.getInt("HistoryID"));
                h.setPassId(rs.getInt("PassID"));
                h.setRenewDate(rs.getTimestamp("RenewDate"));
                h.setDurationMonths(rs.getInt("DurationMonths"));
                h.setNewEndDate(rs.getDate("NewEndDate"));
                h.setRenewedBy(rs.getInt("RenewedBy"));
                h.setRenewedByName(rs.getString("FullName")); // Lấy tên nhân viên
                list.add(h);
            }
        } catch (Exception e) {
        }
        return list;
    }

    // 1. Đếm tổng số lượng vé tháng (Có áp dụng bộ lọc)
    public int getTotalPassesCount(String search, String status, String typeId) {
        String sql = "SELECT COUNT(*) FROM MonthlyPasses mp WHERE 1=1 ";

        // Lọc theo từ khóa (Biển số, Tên khách, SĐT)
        if (search != null && !search.trim().isEmpty()) {
            sql += " AND (mp.LicensePlate LIKE N'%" + search + "%' OR mp.CustomerName LIKE N'%" + search + "%' OR mp.PhoneNumber LIKE '%" + search + "%') ";
        }
        // Lọc theo Trạng thái (Còn hạn / Hết hạn)
        if (status != null && !status.trim().isEmpty()) {
            if (status.equals("Active")) {
                sql += " AND mp.EndDate >= CAST(GETDATE() AS DATE) AND mp.IsActive = 1 ";
            } else if (status.equals("Expired")) {
                sql += " AND (mp.EndDate < CAST(GETDATE() AS DATE) OR mp.IsActive = 0) ";
            }
        }
        // Lọc theo Loại xe
        if (typeId != null && !typeId.trim().isEmpty()) {
            sql += " AND mp.TypeID = " + typeId;
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

    // 2. Lấy danh sách vé tháng phân trang (Có áp dụng bộ lọc)
    public List<model.MonthlyPassDTO> getPassesByPage(int page, int pageSize, String search, String status, String typeId) {
        List<model.MonthlyPassDTO> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = "SELECT mp.*, vt.TypeName, ps.SlotCode, ps.Zone "
                + "FROM MonthlyPasses mp "
                + "JOIN VehicleTypes vt ON mp.TypeID = vt.TypeID "
                + "LEFT JOIN ParkingSlots ps ON mp.SlotID = ps.SlotID "
                + "WHERE 1=1 ";

        if (search != null && !search.trim().isEmpty()) {
            sql += " AND (mp.LicensePlate LIKE N'%" + search + "%' OR mp.CustomerName LIKE N'%" + search + "%' OR mp.PhoneNumber LIKE '%" + search + "%') ";
        }
        if (status != null && !status.trim().isEmpty()) {
            if (status.equals("Active")) {
                sql += " AND mp.EndDate >= CAST(GETDATE() AS DATE) AND mp.IsActive = 1 ";
            } else if (status.equals("Expired")) {
                sql += " AND (mp.EndDate < CAST(GETDATE() AS DATE) OR mp.IsActive = 0) ";
            }
        }
        if (typeId != null && !typeId.trim().isEmpty()) {
            sql += " AND mp.TypeID = " + typeId;
        }

        sql += " ORDER BY mp.PassID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, offset);
            st.setInt(2, pageSize);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                // Tùy vào cách bạn định nghĩa MonthlyPassDTO, code Map dữ liệu sẽ tương tự thế này:
                // list.add(new MonthlyPassDTO(rs.getInt("PassID"), rs.getString("CustomerName"), ...));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    // Kiểm tra xem biển số xe có vé tháng đang còn hoạt động không
    public boolean hasActiveMonthlyPass(String licensePlate) {
        // Kiểm tra IsActive = 1 và EndDate vẫn còn hạn (>= ngày hiện tại)
        String sql = "SELECT 1 FROM MonthlyPasses WHERE LicensePlate = ? AND IsActive = 1 AND EndDate >= CAST(GETDATE() AS DATE)";
        try {
            java.sql.PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, licensePlate);
            java.sql.ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return true; // Xe này ĐÃ CÓ vé tháng
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Xe không có vé tháng hoặc vé đã hết hạn
    }
}
