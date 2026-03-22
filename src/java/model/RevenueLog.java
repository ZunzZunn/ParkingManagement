/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class RevenueLog {

    private String transactionId; // Mã giao dịch (Có thể là ID vé lượt hoặc ID vé tháng)
    private Timestamp createdAt;  // Thời gian thu tiền
    private String type;          // "Ticket" (Vé lượt) hoặc "Monthly" (Vé tháng)
    private String description;   // Nội dung (VD: "Thu tiền xe 29A-123.45")
    private double amount;        // Số tiền thu được

    public RevenueLog() {
    }

    public RevenueLog(String transactionId, Timestamp createdAt, String type, String description, double amount) {
        this.transactionId = transactionId;
        this.createdAt = createdAt;
        this.type = type;
        this.description = description;
        this.amount = amount;
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    // Format thời gian hiển thị ra JSP
    public String getCreatedAt() {
        if (createdAt != null) {
            return new SimpleDateFormat("HH:mm - dd/MM/yyyy").format(createdAt);
        }
        return "";
    }
}
