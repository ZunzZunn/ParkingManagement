/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;

/**
 *
 * @author ZunzZunn
 */
public class UserDAO extends DBContext {

    public User checkLogin(String username, String password) {
        // Chỉ cho phép tài khoản có trạng thái IsActive = 1 đăng nhập
        String sql = "SELECT * FROM Users WHERE Username = ? AND PasswordHash = ? AND IsActive = 1";

        try {
            // Biến connection được kế thừa từ DBContext
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, username);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();

            // Nếu có kết quả trả về -> Đăng nhập thành công
            if (rs.next()) {
                User user = new User(
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("PasswordHash"),
                        rs.getString("FullName"),
                        rs.getString("PhoneNumber"),
                        rs.getInt("RoleID"),
                        rs.getBoolean("IsActive"),
                        rs.getString("Email"),
                        rs.getDate("DateOfBirth"),
                        rs.getString("Avatar"),
                        rs.getBoolean("IsEmailVerified")
                );
                return user;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tại UserDAO: " + e.getMessage());
        }

        return null; // Đăng nhập thất bại (sai user/pass hoặc tài khoản bị khóa)
    }

    // Thêm tham số isEmailVerified vào hàm
    public boolean updateProfile(int userId, String fullName, String phoneNumber, String email, String dob, boolean isEmailVerified) {
        String sql = "UPDATE Users SET FullName = ?, PhoneNumber = ?, Email = ?, DateOfBirth = ?, IsEmailVerified = ? WHERE UserID = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, fullName);
            st.setString(2, phoneNumber);
            st.setString(3, email);
            if (dob != null && !dob.isEmpty()) {
                st.setDate(4, java.sql.Date.valueOf(dob));
            } else {
                st.setNull(4, java.sql.Types.DATE);
            }
            st.setBoolean(5, isEmailVerified); // Lưu trạng thái
            st.setInt(6, userId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // 2. Đổi mật khẩu (Chỉ thành công khi mật khẩu cũ khớp)
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        String sql = "UPDATE Users SET PasswordHash = ? WHERE UserID = ? AND PasswordHash = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, newPassword);
            st.setInt(2, userId);
            st.setString(3, oldPassword); // Điều kiện WHERE để check mật khẩu cũ
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi tại changePassword: " + e.getMessage());
        }
        return false;
    }

    public boolean updateAvatar(int userId, String avatarPath) {
        String sql = "UPDATE Users SET Avatar = ? WHERE UserID = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, avatarPath);
            st.setInt(2, userId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi tại updateAvatar: " + e.getMessage());
        }
        return false;
    }

    // Kiểm tra trạng thái tài khoản để khôi phục mật khẩu
    public int checkUserEmail(String username, String email) {
        String sql = "SELECT IsEmailVerified FROM Users WHERE Username = ? AND Email = ? AND IsActive = 1";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, username);
            st.setString(2, email);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                // Trả về 1 nếu ĐÃ xác nhận, 0 nếu CHƯA xác nhận
                return rs.getBoolean("IsEmailVerified") ? 1 : 0;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tại checkUserEmail: " + e.getMessage());
        }
        return -1; // -1 nghĩa là sai Tên đăng nhập hoặc sai Email
    }

    // Đặt lại mật khẩu mới (Quên mật khẩu)
    public boolean resetPassword(String username, String newPassword) {
        String sql = "UPDATE Users SET PasswordHash = ? WHERE Username = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, newPassword);
            st.setString(2, username);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi tại resetPassword: " + e.getMessage());
        }
        return false;
    }

    // 1. Kiểm tra xem Email đã bị người khác XÁC NHẬN chưa
    public boolean isEmailVerifiedByOther(String email, int currentUserId) {
        // Chỉ trả về true nếu kẻ chiếm dụng đã có IsEmailVerified = 1
        String sql = "SELECT UserID FROM Users WHERE Email = ? AND UserID != ? AND IsEmailVerified = 1";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, email);
            st.setInt(2, currentUserId);
            return st.executeQuery().next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // 2. Thu hồi Email từ kẻ chiếm dụng CHƯA xác nhận
    public void clearUnverifiedSquatter(String email) {
        // Vì cột Email là NOT NULL và UNIQUE, ta sẽ đổi email của kẻ đó thành email rác
        String sql = "UPDATE Users SET Email = CAST(UserID AS VARCHAR) + '_revoked@iparking.local' WHERE Email = ? AND IsEmailVerified = 0";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, email);
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Đánh dấu Email đã xác minh thành công
    public boolean markEmailVerified(int userId) {
        String sql = "UPDATE Users SET IsEmailVerified = 1 WHERE UserID = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, userId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
