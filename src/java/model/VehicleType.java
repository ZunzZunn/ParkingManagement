/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ZunzZunn
 */
public class VehicleType {

    private int typeID;
    private String typeName;
    private double pricePerHour;
    private double pricePerMonth;

    public VehicleType() {
    }

    public VehicleType(int typeID, String typeName, double pricePerHour, double pricePerMonth) {
        this.typeID = typeID;
        this.typeName = typeName;
        this.pricePerHour = pricePerHour;
        this.pricePerMonth = pricePerMonth;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public double getPricePerMonth() {
        return pricePerMonth;
    }

    public void setPricePerMonth(double pricePerMonth) {
        this.pricePerMonth = pricePerMonth;
    }
}
