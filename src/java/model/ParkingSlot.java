/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ZunzZunn
 */
public class ParkingSlot {

    private int slotID;
    private String slotCode;
    private String zone;
    private int typeID;
    private String status;

    public ParkingSlot() {
    }

    public ParkingSlot(int slotID, String slotCode, String zone, int typeID, String status) {
        this.slotID = slotID;
        this.slotCode = slotCode;
        this.zone = zone;
        this.typeID = typeID;
        this.status = status;
    }

    public int getSlotID() {
        return slotID;
    }

    public void setSlotID(int slotID) {
        this.slotID = slotID;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
