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
                        rs.getString("Avatar")
                );
                return user;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi tại UserDAO: " + e.getMessage());
        }

        return null; // Đăng nhập thất bại (sai user/pass hoặc tài khoản bị khóa)
    }

    // Thêm vào dưới hàm checkLogin trong UserDAO.java
    // 1. Cập nhật thông tin cơ bản
    public boolean updateProfile(int userId, String fullName, String phoneNumber, String email, String dob) {
        String sql = "UPDATE Users SET FullName = ?, PhoneNumber = ?, Email = ?, DateOfBirth = ? WHERE UserID = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, fullName);
            st.setString(2, phoneNumber);
            st.setString(3, email);

            // Xử lý ngày sinh (nếu rỗng thì set NULL vào DB)
            if (dob != null && !dob.isEmpty()) {
                st.setDate(4, java.sql.Date.valueOf(dob));
            } else {
                st.setNull(4, java.sql.Types.DATE);
            }

            st.setInt(5, userId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi tại updateProfile: " + e.getMessage());
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

    // Kiểm tra xem Username và Email có tồn tại và khớp nhau không
    public boolean checkUserEmail(String username, String email) {
        String sql = "SELECT * FROM Users WHERE Username = ? AND Email = ? AND IsActive = 1";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, username);
            st.setString(2, email);
            ResultSet rs = st.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Lỗi tại checkUserEmail: " + e.getMessage());
        }
        return false;
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
}
