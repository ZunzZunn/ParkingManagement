/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 *
 * @author ZunzZunn
 */
public class EmailUtil {

    public static boolean sendOTP(String toEmail, String otpCode) {
        // Thay bằng Gmail và Mật khẩu ứng dụng của bạn
        final String fromEmail = "myniyong.tg@gmail.com";
        final String password = "yaeh uacr pjvf xmkp"; // 16 ký tự

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail, "iParking Security", "UTF-8")); // Tiện thể thêm UTF-8 cho tên người gửi luôn
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject("Mã xác nhận đổi mật khẩu - iParking", "UTF-8"); // Sẽ không còn lỗi nữa

            String htmlContent = "<h2 style='color: #0071e3;'>Yêu cầu đặt lại mật khẩu</h2>"
                    + "<p>Mã OTP của bạn là: <b style='font-size: 24px; color: #ff3b30;'>" + otpCode + "</b></p>"
                    + "<p>Mã này có hiệu lực trong 5 phút. Vui lòng không chia sẻ cho bất kỳ ai.</p>";

            msg.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
