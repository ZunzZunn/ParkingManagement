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
public class MonthlyPass {

    private int passId;
    private String customerName;
    private String phoneNumber;
    private int slotId;
    private String licensePlate;
    private int typeId;
    private Date startDate;
    private Date endDate;
    private boolean isActive;

    public MonthlyPass() {
    }

    public MonthlyPass(int passId, String customerName, String phoneNumber, int slotId, String licensePlate, int typeId, Date startDate, Date endDate, boolean isActive) {
        this.passId = passId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.slotId = slotId;
        this.licensePlate = licensePlate;
        this.typeId = typeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }

    // --- GETTERS & SETTERS ---
    public int getPassId() {
        return passId;
    }

    public void setPassId(int passId) {
        this.passId = passId;
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

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
