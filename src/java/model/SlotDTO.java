/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ZunzZunn
 */
public class SlotDTO {

    private int slotId;
    private String slotCode;
    private int typeId;
    private String zone;

    public SlotDTO() {
    }

    public SlotDTO(int slotId, String slotCode, int typeId, String zone) {
        this.slotId = slotId;
        this.slotCode = slotCode;
        this.typeId = typeId;
        this.zone = zone;
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
}
