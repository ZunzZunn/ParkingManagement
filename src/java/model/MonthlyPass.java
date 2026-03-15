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

    private int passID;
    private int userID;
    private int slotID;
    private String licensePlate;
    private int typeID;
    private Date startDate;
    private Date endDate;
    private boolean isActive;

    public MonthlyPass() {
    }

    public MonthlyPass(int passID, int userID, int slotID, String licensePlate, int typeID, Date startDate, Date endDate, boolean isActive) {
        this.passID = passID;
        this.userID = userID;
        this.slotID = slotID;
        this.licensePlate = licensePlate;
        this.typeID = typeID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }

    public int getPassID() {
        return passID;
    }

    public void setPassID(int passID) {
        this.passID = passID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getSlotID() {
        return slotID;
    }

    public void setSlotID(int slotID) {
        this.slotID = slotID;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
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
