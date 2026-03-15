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

    private String customerName;
    private String phoneNumber;
    private String statusBadge; // Lưu trạng thái: Active, ExpiringSoon, Expired

    public MonthlyPassDTO() {
    }

    public MonthlyPassDTO(String customerName, String phoneNumber, String statusBadge) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.statusBadge = statusBadge;
    }

    public MonthlyPassDTO(String customerName, String phoneNumber, String statusBadge, int passID, int userID, int slotID, String licensePlate, int typeID, Date startDate, Date endDate, boolean isActive) {
        super(passID, userID, slotID, licensePlate, typeID, startDate, endDate, isActive);
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.statusBadge = statusBadge;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatusBadge() {
        return statusBadge;
    }

    public void setStatusBadge(String statusBadge) {
        this.statusBadge = statusBadge;
    }
}
