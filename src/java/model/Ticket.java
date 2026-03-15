/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 *
 * @author ZunzZunn
 */
public class Ticket {

    private int ticketID;
    private String licensePlate;
    private int slotID;
    private int typeID;
    private boolean isMonthlyPass;
    private Timestamp checkInTime;
    private Timestamp checkOutTime;
    private double totalFee;
    private int staffInID;
    private int staffOutID;
    private String status;

    public Ticket() {
    }

    public Ticket(int ticketID, String licensePlate, int slotID, int typeID, boolean isMonthlyPass, Timestamp checkInTime, Timestamp checkOutTime, double totalFee, int staffInID, int staffOutID, String status) {
        this.ticketID = ticketID;
        this.licensePlate = licensePlate;
        this.slotID = slotID;
        this.typeID = typeID;
        this.isMonthlyPass = isMonthlyPass;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.totalFee = totalFee;
        this.staffInID = staffInID;
        this.staffOutID = staffOutID;
        this.status = status;
    }

    public int getTicketID() {
        return ticketID;
    }

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getSlotID() {
        return slotID;
    }

    public void setSlotID(int slotID) {
        this.slotID = slotID;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public boolean isIsMonthlyPass() {
        return isMonthlyPass;
    }

    public void setIsMonthlyPass(boolean isMonthlyPass) {
        this.isMonthlyPass = isMonthlyPass;
    }

    public Timestamp getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Timestamp checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Timestamp getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Timestamp checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(double totalFee) {
        this.totalFee = totalFee;
    }

    public int getStaffInID() {
        return staffInID;
    }

    public void setStaffInID(int staffInID) {
        this.staffInID = staffInID;
    }

    public int getStaffOutID() {
        return staffOutID;
    }

    public void setStaffOutID(int staffOutID) {
        this.staffOutID = staffOutID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
