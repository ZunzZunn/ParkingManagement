/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.SlotDTO;
import model.TransactionDTO;

/**
 *
 * @author ZunzZunn
 */
public class TicketDAO extends DBContext {

    public int getTotalParkedVehicles() {
        String sql = "SELECT COUNT(*) FROM Tickets WHERE Status = 'Active'";
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

    public double getTodayRevenue() {
        String sql = "SELECT SUM(TotalFee) FROM Tickets WHERE CAST(CheckOutTime AS DATE) = CAST(GETDATE() AS DATE)";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy 5 giao dịch gần nhất (Cập nhật: Tính cả xe mới vào HOẶC xe mới ra)
    public List<TransactionDTO> getRecentTransactions() {
        List<TransactionDTO> list = new ArrayList<>();
        // Dùng COALESCE: Ưu tiên sắp xếp theo Thời gian ra (nếu có), nếu chưa ra thì lấy Thời gian vào
        String sql = "SELECT TOP 5 t.LicensePlate, vt.TypeName, ps.SlotCode, t.CheckInTime, t.CheckOutTime, t.Status "
                + "FROM Tickets t "
                + "JOIN VehicleTypes vt ON t.TypeID = vt.TypeID "
                + "JOIN ParkingSlots ps ON t.SlotID = ps.SlotID "
                + "ORDER BY COALESCE(t.CheckOutTime, t.CheckInTime) DESC";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                TransactionDTO tx = new TransactionDTO();
                tx.setLicensePlate(rs.getString("LicensePlate"));
                tx.setTypeName(rs.getString("TypeName"));
                tx.setSlotCode(rs.getString("SlotCode"));
                tx.setCheckInTime(rs.getTimestamp("CheckInTime"));

                // --- THÊM DÒNG LẤY GIỜ RA NÀY ---
                tx.setCheckOutTime(rs.getTimestamp("CheckOutTime"));

                tx.setStatus(rs.getString("Status"));

                list.add(tx);
            }
        } catch (Exception e) {
            System.out.println("❌ LỖI SQL TẠI getRecentTransactions:");
            e.printStackTrace();
        }
        return list;
    }

    // Lấy TẤT CẢ giao dịch (Lịch sử đầy đủ)
    public List<TransactionDTO> getAllTransactions() {
        List<TransactionDTO> list = new ArrayList<>();
        // Bỏ TOP 5 đi để lấy toàn bộ lịch sử, vẫn sắp xếp mới nhất lên đầu
        String sql = "SELECT t.LicensePlate, vt.TypeName, ps.SlotCode, t.CheckInTime, t.CheckOutTime, t.Status "
                + "FROM Tickets t "
                + "JOIN VehicleTypes vt ON t.TypeID = vt.TypeID "
                + "JOIN ParkingSlots ps ON t.SlotID = ps.SlotID "
                + "ORDER BY t.CheckInTime DESC";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                TransactionDTO tx = new TransactionDTO();
                tx.setLicensePlate(rs.getString("LicensePlate"));
                tx.setTypeName(rs.getString("TypeName"));
                tx.setSlotCode(rs.getString("SlotCode"));
                tx.setCheckInTime(rs.getTimestamp("CheckInTime"));
                tx.setCheckOutTime(rs.getTimestamp("CheckOutTime"));
                tx.setStatus(rs.getString("Status"));

                list.add(tx);
            }
        } catch (Exception e) {
            System.out.println("❌ LỖI SQL TẠI getAllTransactions:");
            e.printStackTrace();
        }
        return list;
    }

    // 1. Lấy danh sách TẤT CẢ CHỖ ĐANG TRỐNG
    public List<SlotDTO> getAvailableSlots() {
        List<SlotDTO> list = new ArrayList<>();
        String sql = "SELECT SlotID, SlotCode, TypeID, Zone FROM ParkingSlots WHERE Status = 'Available'";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                SlotDTO s = new SlotDTO();
                s.setSlotId(rs.getInt("SlotID"));
                s.setSlotCode(rs.getString("SlotCode"));
                s.setTypeId(rs.getInt("TypeID"));
                s.setZone(rs.getString("Zone"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Lưu giao dịch mới & Cập nhật chỗ đỗ thành 'Occupied'
    public boolean checkInVehicle(String licensePlate, int typeId, int slotId, int staffId) {
        String sqlInsertTicket = "INSERT INTO Tickets (LicensePlate, SlotID, TypeID, IsMonthlyPass, StaffInID, Status) VALUES (?, ?, ?, 0, ?, 'Active')";
        String sqlUpdateSlot = "UPDATE ParkingSlots SET Status = 'Occupied' WHERE SlotID = ?";
        try {
            connection.setAutoCommit(false); // Bắt đầu Transaction (Khóa an toàn)

            // Lệnh 1: Thêm vé xe
            PreparedStatement st1 = connection.prepareStatement(sqlInsertTicket);
            st1.setString(1, licensePlate);
            st1.setInt(2, slotId);
            st1.setInt(3, typeId);
            st1.setInt(4, staffId);
            st1.executeUpdate();

            // Lệnh 2: Đổi trạng thái chỗ đỗ
            PreparedStatement st2 = connection.prepareStatement(sqlUpdateSlot);
            st2.setInt(1, slotId);
            st2.executeUpdate();

            connection.commit(); // Chạy thành công cả 2 lệnh mới lưu thật vào DB
            return true;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
            } // Lỗi thì hoàn tác
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception ex) {
            }
        }
        return false;
    }

    // 3. Xử lý XE RA (Check-out) - Tự động tính tiền và giải phóng chỗ
    public boolean checkOutVehicle(String licensePlate, int staffOutId) {
        // Lấy thông tin vé hiện tại để tính tiền
        String sqlSelect = "SELECT t.TicketID, t.SlotID, t.CheckInTime, t.IsMonthlyPass, v.PricePerHour "
                + "FROM Tickets t JOIN VehicleTypes v ON t.TypeID = v.TypeID "
                + "WHERE t.LicensePlate = ? AND t.Status = 'Active'";

        String sqlUpdateTicket = "UPDATE Tickets SET CheckOutTime = GETDATE(), TotalFee = ?, StaffOutID = ?, Status = 'Completed' WHERE TicketID = ?";
        String sqlUpdateSlot = "UPDATE ParkingSlots SET Status = 'Available' WHERE SlotID = ?";

        try {
            connection.setAutoCommit(false); // Khóa an toàn giao dịch
            PreparedStatement psSelect = connection.prepareStatement(sqlSelect);
            psSelect.setString(1, licensePlate);
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
                int ticketId = rs.getInt("TicketID");
                int slotId = rs.getInt("SlotID");
                java.sql.Timestamp checkInTime = rs.getTimestamp("CheckInTime");
                boolean isMonthlyPass = rs.getBoolean("IsMonthlyPass");
                double pricePerHour = rs.getDouble("PricePerHour");

                // Tính toán tiền phí (Bằng Java)
                double fee = 0;
                if (!isMonthlyPass) {
                    // Lấy giờ hiện tại trừ giờ vào
                    long diffInMillies = Math.abs(System.currentTimeMillis() - checkInTime.getTime());
                    long diffHours = (long) Math.ceil((double) diffInMillies / (1000 * 60 * 60)); // Làm tròn lên (vd 1.2h -> 2h)
                    if (diffHours < 1) {
                        diffHours = 1; // Đỗ dưới 1 tiếng vẫn tính 1 tiếng
                    }
                    fee = diffHours * pricePerHour;
                }

                // Cập nhật tiền và giờ ra vào Tickets
                PreparedStatement psUpdateTicket = connection.prepareStatement(sqlUpdateTicket);
                psUpdateTicket.setDouble(1, fee);
                psUpdateTicket.setInt(2, staffOutId);
                psUpdateTicket.setInt(3, ticketId);
                psUpdateTicket.executeUpdate();

                // Trả lại chỗ đỗ cho bảng ParkingSlots
                PreparedStatement psUpdateSlot = connection.prepareStatement(sqlUpdateSlot);
                psUpdateSlot.setInt(1, slotId);
                psUpdateSlot.executeUpdate();

                connection.commit();
                return true;
            }
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception ex) {
            }
        }
        return false;
    }

    // 1. Hàm phụ trợ: Tự động ghép nối các điều kiện lọc thành câu lệnh SQL (WHERE ...)
    private String buildWhereClause(String lp, String type, String zone, String st, String fDate, String tDate, List<Object> params) {
        StringBuilder sql = new StringBuilder(" WHERE 1=1 ");
        if (lp != null && !lp.trim().isEmpty()) {
            sql.append(" AND t.LicensePlate LIKE ? ");
            params.add("%" + lp.trim() + "%");
        }
        if (type != null && !type.trim().isEmpty()) {
            sql.append(" AND t.TypeID = ? ");
            params.add(type);
        }
        if (zone != null && !zone.trim().isEmpty()) {
            sql.append(" AND ps.Zone = ? ");
            params.add(zone.trim());
        }
        if (st != null && !st.trim().isEmpty()) {
            sql.append(" AND t.Status = ? ");
            params.add(st);
        }
        // Lọc theo ngày (Bao gồm cả xe mới vào hoặc xe mới ra trong khoảng ngày đó)
        if (fDate != null && !fDate.trim().isEmpty()) {
            sql.append(" AND CAST(COALESCE(t.CheckOutTime, t.CheckInTime) AS DATE) >= ? ");
            params.add(fDate);
        }
        if (tDate != null && !tDate.trim().isEmpty()) {
            sql.append(" AND CAST(COALESCE(t.CheckOutTime, t.CheckInTime) AS DATE) <= ? ");
            params.add(tDate);
        }
        return sql.toString();
    }

    // 2. Đếm tổng số lượng Giao dịch (ĐÃ ÁP DỤNG BỘ LỌC)
    public int getTotalTransactionsCount(String lp, String type, String zone, String st, String fDate, String tDate) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT COUNT(*) FROM Tickets t JOIN ParkingSlots ps ON t.SlotID = ps.SlotID "
                + buildWhereClause(lp, type, zone, st, fDate, tDate, params);
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 3. Lấy dữ liệu Phân trang (ĐÃ ÁP DỤNG BỘ LỌC)
    public List<TransactionDTO> getTransactionsByPage(int page, int pageSize, String lp, String type, String zone, String st, String fDate, String tDate) {
        List<TransactionDTO> list = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        String sql = "SELECT t.LicensePlate, vt.TypeName, ps.SlotCode, t.CheckInTime, t.CheckOutTime, t.Status "
                + "FROM Tickets t JOIN VehicleTypes vt ON t.TypeID = vt.TypeID JOIN ParkingSlots ps ON t.SlotID = ps.SlotID "
                + buildWhereClause(lp, type, zone, st, fDate, tDate, params)
                + "ORDER BY COALESCE(t.CheckOutTime, t.CheckInTime) DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            int idx = 1;
            // Nạp các biến lọc vào SQL
            for (Object p : params) {
                pst.setObject(idx++, p);
            }
            // Nạp biến phân trang
            pst.setInt(idx++, offset);
            pst.setInt(idx, pageSize);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                TransactionDTO tx = new TransactionDTO();
                tx.setLicensePlate(rs.getString("LicensePlate"));
                tx.setTypeName(rs.getString("TypeName"));
                tx.setSlotCode(rs.getString("SlotCode"));
                tx.setCheckInTime(rs.getTimestamp("CheckInTime"));
                tx.setCheckOutTime(rs.getTimestamp("CheckOutTime"));
                tx.setStatus(rs.getString("Status"));
                list.add(tx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
