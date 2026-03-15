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
public class ParkingSlotDAO extends DBContext {

    public int getAvailableSpots() {
        String sql = "SELECT COUNT(*) FROM ParkingSlots WHERE Status = 'Available'";
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

    // Bạn dán hàm này vào trong ParkingSlotDAO nhé
    public java.util.List<model.ParkingSlot> getSlotsByType(int typeId) {
        java.util.List<model.ParkingSlot> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM ParkingSlots WHERE TypeID = ?";
        try {
            java.sql.PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, typeId);
            java.sql.ResultSet rs = st.executeQuery();
            while (rs.next()) {
                model.ParkingSlot slot = new model.ParkingSlot();
                slot.setSlotID(rs.getInt("SlotID"));
                slot.setSlotCode(rs.getString("SlotCode"));
                slot.setZone(rs.getString("Zone"));
                slot.setTypeID(rs.getInt("TypeID"));
                slot.setStatus(rs.getString("Status"));
                list.add(slot);
            }
        } catch (java.sql.SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    // 1. Lấy chi tiết ô đỗ (Trả về chuỗi JSON để gọi bằng AJAX cho mượt)
    public String getSlotDetailJson(int slotId) {
        String json = "{}";
        String sql = "SELECT s.SlotID, s.SlotCode, s.Status, s.TypeID, t.TicketID, t.LicensePlate, FORMAT(t.CheckInTime, 'HH:mm - dd/MM/yyyy') as CheckInTime "
                + "FROM ParkingSlots s LEFT JOIN Tickets t ON s.SlotID = t.SlotID AND t.Status = 'Active' "
                + "WHERE s.SlotID = ?";
        try {
            java.sql.PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, slotId);
            java.sql.ResultSet rs = st.executeQuery();
            if (rs.next()) {
                json = "{"
                        + "\"slotId\":" + rs.getInt("SlotID") + ","
                        + "\"slotCode\":\"" + rs.getString("SlotCode") + "\","
                        + "\"status\":\"" + rs.getString("Status") + "\","
                        + "\"typeId\":" + rs.getInt("TypeID") + ","
                        + "\"licensePlate\":\"" + (rs.getString("LicensePlate") == null ? "" : rs.getString("LicensePlate")) + "\","
                        + "\"checkInTime\":\"" + (rs.getString("CheckInTime") == null ? "" : rs.getString("CheckInTime")) + "\""
                        + "}";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return json;
    }

    // 2. Thao tác NHẬN XE VÀO BÃI
    public boolean quickCheckIn(int slotId, String licensePlate, int typeId, int staffId) {
        try {
            connection.setAutoCommit(false); // Bật chế độ Transaction an toàn

            // 1. Tạo vé mới (Trạng thái Active)
            String sqlTicket = "INSERT INTO Tickets (LicensePlate, SlotID, TypeID, StaffInID, Status, CheckInTime) VALUES (?, ?, ?, ?, 'Active', GETDATE())";
            java.sql.PreparedStatement st1 = connection.prepareStatement(sqlTicket);
            st1.setString(1, licensePlate);
            st1.setInt(2, slotId);
            st1.setInt(3, typeId);
            st1.setInt(4, staffId);
            st1.executeUpdate();

            // 2. Đổi trạng thái ô đỗ thành Occupied
            String sqlSlot = "UPDATE ParkingSlots SET Status = 'Occupied' WHERE SlotID = ?";
            java.sql.PreparedStatement st2 = connection.prepareStatement(sqlSlot);
            st2.setInt(1, slotId);
            st2.executeUpdate();

            connection.commit(); // Lưu thành công cả 2 bảng
            return true;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
            } // Lỗi thì hủy bỏ toàn bộ
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {
            }
        }
        return false;
    }

    // 3. Thao tác CHO XE RA BÃI
    public boolean quickCheckOut(int slotId, int staffId) {
        try {
            connection.setAutoCommit(false);

            // 1. Chốt vé (Đổi thành Completed)
            String sqlTicket = "UPDATE Tickets SET CheckOutTime = GETDATE(), Status = 'Completed', StaffOutID = ? WHERE SlotID = ? AND Status = 'Active'";
            java.sql.PreparedStatement st1 = connection.prepareStatement(sqlTicket);
            st1.setInt(1, staffId);
            st1.setInt(2, slotId);
            st1.executeUpdate();

            // 2. Giải phóng ô đỗ
            String sqlSlot = "UPDATE ParkingSlots SET Status = 'Available' WHERE SlotID = ?";
            java.sql.PreparedStatement st2 = connection.prepareStatement(sqlSlot);
            st2.setInt(1, slotId);
            st2.executeUpdate();

            connection.commit();
            return true;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {
            }
        }
        return false;
    }
    
    // Đổi trạng thái ô đỗ (Dùng để chuyển sang Bảo trì hoặc Mở lại)
    public boolean updateSlotStatus(int slotId, String status) {
        String sql = "UPDATE ParkingSlots SET Status = ? WHERE SlotID = ?";
        try {
            java.sql.PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, status);
            st.setInt(2, slotId);
            return st.executeUpdate() > 0;
        } catch (Exception e) { System.out.println(e.getMessage()); }
        return false;
    }
}
