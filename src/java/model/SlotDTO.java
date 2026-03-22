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
public class SlotDTO {

    private int slotId;
    private String slotCode;
    private int typeId;
    private String zone;
    private String customerName;
    private String customerPhone;
    private String monthlyPlate;
    private Date passEndDate;

    public SlotDTO() {
    }

    public SlotDTO(int slotId, String slotCode, int typeId, String zone, String customerName, String customerPhone, String monthlyPlate, Date passEndDate) {
        this.slotId = slotId;
        this.slotCode = slotCode;
        this.typeId = typeId;
        this.zone = zone;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.monthlyPlate = monthlyPlate;
        this.passEndDate = passEndDate;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getMonthlyPlate() {
        return monthlyPlate;
    }

    public void setMonthlyPlate(String monthlyPlate) {
        this.monthlyPlate = monthlyPlate;
    }

    public Date getPassEndDate() {
        return passEndDate;
    }

    public void setPassEndDate(Date passEndDate) {
        this.passEndDate = passEndDate;
    }

}
