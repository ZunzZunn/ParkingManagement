/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * @author ZunzZunn
 */
public class RenewalHistory {

    private int historyId;
    private int passId;
    private Timestamp renewDate;
    private int durationMonths;
    private Date newEndDate;
    private int renewedBy;
    private String renewedByName;

    public RenewalHistory() {
    }

    public RenewalHistory(int historyId, int passId, Timestamp renewDate, int durationMonths, Date newEndDate, int renewedBy, String renewedByName) {
        this.historyId = historyId;
        this.passId = passId;
        this.renewDate = renewDate;
        this.durationMonths = durationMonths;
        this.newEndDate = newEndDate;
        this.renewedBy = renewedBy;
        this.renewedByName = renewedByName;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getPassId() {
        return passId;
    }

    public void setPassId(int passId) {
        this.passId = passId;
    }

    public Timestamp getRenewDate() {
        return renewDate;
    }

    public void setRenewDate(Timestamp renewDate) {
        this.renewDate = renewDate;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public Date getNewEndDate() {
        return newEndDate;
    }

    public void setNewEndDate(Date newEndDate) {
        this.newEndDate = newEndDate;
    }

    public int getRenewedBy() {
        return renewedBy;
    }

    public void setRenewedBy(int renewedBy) {
        this.renewedBy = renewedBy;
    }

    public String getRenewedByName() {
        return renewedByName;
    }

    public void setRenewedByName(String renewedByName) {
        this.renewedByName = renewedByName;
    }
}
