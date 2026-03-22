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
public class RevenueDTO {

    private String transactionId;   // Mã giao dịch (VD: TICKET-10, PASS-5)
    private String source;          // Nguồn thu (Vé vãng lai / Gia hạn vé tháng)
    private String description;     // Mô tả (Biển số xe hoặc Tên khách hàng)
    private double amount;          // Số tiền thu được
    private Timestamp date;         // Thời gian thu tiền

    public RevenueDTO() {
    }

    public RevenueDTO(String transactionId, String source, String description, double amount, Timestamp date) {
        this.transactionId = transactionId;
        this.source = source;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    // --- GETTERS & SETTERS ---
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
