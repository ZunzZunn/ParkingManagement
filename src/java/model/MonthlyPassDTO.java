/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;

/**
 *
 * @author ZunzZunn
 */
public class MonthlyPassDTO extends MonthlyPass {

    private String statusBadge; // Lưu trạng thái: Active, ExpiringSoon, Expired
    private String slotCode;    // Mã chỗ đỗ (VD: A-01) - Lấy từ lệnh JOIN
    private String typeName;    // Tên loại xe (VD: Xe máy) - Lấy từ lệnh JOIN

    public MonthlyPassDTO() {
        super();
    }

    // Constructor chỉ chứa các biến mở rộng
    public MonthlyPassDTO(String statusBadge, String slotCode, String typeName) {
        this.statusBadge = statusBadge;
        this.slotCode = slotCode;
        this.typeName = typeName;
    }

    // Constructor đầy đủ gọi cả super() từ MonthlyPass
    public MonthlyPassDTO(int passId, String customerName, String phoneNumber, int slotId, String licensePlate, int typeId, Date startDate, Date endDate, boolean isActive, String statusBadge, String slotCode, String typeName) {
        // Truyền các biến gốc lên class cha (MonthlyPass)
        super(passId, customerName, phoneNumber, slotId, licensePlate, typeId, startDate, endDate, isActive);

        // Gán các biến mở rộng của DTO
        this.statusBadge = statusBadge;
        this.slotCode = slotCode;
        this.typeName = typeName;
    }

    public String getStatusBadge() {
        return statusBadge;
    }

    public void setStatusBadge(String statusBadge) {
        this.statusBadge = statusBadge;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
    
}
