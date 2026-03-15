/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 *
 * @author ZunzZunn
 */
public class TransactionDTO {

    private String licensePlate;
    private String typeName;
    private String slotCode;
    private Timestamp checkInTime;
    private Timestamp checkOutTime;
    private String status;

    public TransactionDTO() {
    }

    public TransactionDTO(String licensePlate, String typeName, String slotCode, Timestamp checkInTime, Timestamp checkOutTime, String status) {
        this.licensePlate = licensePlate;
        this.typeName = typeName;
        this.slotCode = slotCode;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.status = status;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public Timestamp getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Timestamp checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Hàm tiện ích: Tự động đổi thời gian (Timestamp) ra chữ đẹp dễ đọc trên JSP
    public String getFormattedTime() {
        if (checkInTime != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy");
            return sdf.format(checkInTime);
        }
        return "";
    }

    // 2. Hàm Getter & Setter
    public Timestamp getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Timestamp checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    // 3. Hàm tự động format ngày giờ (Hiển thị lên JSP)
    public String getFormattedOutTime() {
        if (checkOutTime != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy");
            return sdf.format(checkOutTime);
        }
        // Nếu checkOutTime bị null (xe chưa ra), hiển thị dấu gạch ngang hoặc chữ Đang đỗ
        return "<span style='color: var(--apple-text-light);'>- Đang đỗ -</span>";
    }
}
