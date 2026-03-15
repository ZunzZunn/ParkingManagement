/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.UserDAO;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.nio.file.Paths;
import model.User;

/**
 *
 * @author myniy
 */
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 10)
public class ProfileController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ProfileController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ProfileController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/profile.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        User account = (User) session.getAttribute("account");

        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        UserDAO dao = new UserDAO();

        if ("updateInfo".equals(action)) {
            String fullName = request.getParameter("fullName");
            String phoneNumber = request.getParameter("phoneNumber");
            String email = request.getParameter("email");
            String dob = request.getParameter("dateOfBirth");

            // 1. Kiểm tra: Nếu người khác ĐÃ XÁC NHẬN email này -> Chặn tuyệt đối
            if (dao.isEmailVerifiedByOther(email, account.getUserID())) {
                request.setAttribute("msgError", "Email này đã được xác nhận bảo mật bởi một tài khoản khác!");
                request.getRequestDispatcher("/views/profile.jsp").forward(request, response);
                return;
            }

            // 2. Nếu người khác nhập bừa nhưng CHƯA XÁC NHẬN -> Thu hồi email đó
            dao.clearUnverifiedSquatter(email);

            // 3. Nếu người dùng hiện tại đổi sang Email mới -> Tự động hủy trạng thái đã xác minh của chính họ
            boolean currentVerifiedStatus = account.isEmailVerified();
            if (account.getEmail() != null && !account.getEmail().equalsIgnoreCase(email)) {
                currentVerifiedStatus = false;
            }

            // 4. Tiến hành lưu vào CSDL
            if (dao.updateProfile(account.getUserID(), fullName, phoneNumber, email, dob, currentVerifiedStatus)) {
                account.setFullName(fullName);
                account.setPhoneNumber(phoneNumber);
                account.setEmail(email);
                if (dob != null && !dob.isEmpty()) {
                    account.setDateOfBirth(java.sql.Date.valueOf(dob));
                }
                account.setEmailVerified(currentVerifiedStatus);

                session.setAttribute("account", account);
                request.setAttribute("msgSuccess", "Cập nhật thông tin thành công!");
            } else {
                request.setAttribute("msgError", "Cập nhật thông tin thất bại!");
            }
        } else if ("uploadAvatar".equals(action)) {
            try {
                Part filePart = request.getPart("avatarFile");
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

                if (fileName != null && !fileName.isEmpty()) {
                    // Tạo thư mục lưu ảnh nếu chưa có
                    String uploadPath = request.getServletContext().getRealPath("") + File.separator + "images" + File.separator + "avatars";
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }

                    // Lưu file với tên: ID_tênfile để tránh trùng lặp
                    String saveFileName = account.getUserID() + "_" + fileName;
                    filePart.write(uploadPath + File.separator + saveFileName);

                    // Đường dẫn tương đối để lưu vào Database
                    String dbAvatarPath = "images/avatars/" + saveFileName;

                    if (dao.updateAvatar(account.getUserID(), dbAvatarPath)) {
                        account.setAvatar(dbAvatarPath);
                        session.setAttribute("account", account);
                        request.setAttribute("msgSuccess", "Cập nhật ảnh đại diện thành công!");
                    }
                }
            } catch (Exception e) {
                request.setAttribute("msgError", "Lỗi khi tải ảnh lên: " + e.getMessage());
            }
        } else if ("changePassword".equals(action)) {
            String oldPass = request.getParameter("oldPassword");
            String newPass = request.getParameter("newPassword");
            String confirmPass = request.getParameter("confirmPassword");

            if (!newPass.equals(confirmPass)) {
                request.setAttribute("msgError", "Mật khẩu xác nhận không khớp!");
            } else {
                if (dao.changePassword(account.getUserID(), oldPass, newPass)) {
                    request.setAttribute("msgSuccess", "Đổi mật khẩu thành công!");
                    // Cập nhật lại password trong session
                    account.setPasswordHash(newPass);
                } else {
                    request.setAttribute("msgError", "Mật khẩu cũ không chính xác!");
                }
            }
        } else if ("sendVerifyOTP".equals(action)) {
            response.setContentType("text/plain;charset=UTF-8");
            String otp = String.format("%06d", new java.util.Random().nextInt(999999));
            session.setAttribute("verify_otp", otp);
            session.setMaxInactiveInterval(300); // Tồn tại 5 phút

            if (util.EmailUtil.sendVerificationOTP(account.getEmail(), otp)) {
                response.getWriter().write("success");
            } else {
                response.getWriter().write("Lỗi hệ thống khi gửi email!");
            }
            return; // Dừng tại đây, không load trang
        } else if ("confirmVerifyOTP".equals(action)) {
            response.setContentType("text/plain;charset=UTF-8");
            String inputOtp = request.getParameter("otp");
            String sessionOtp = (String) session.getAttribute("verify_otp");

            if (sessionOtp != null && sessionOtp.equals(inputOtp)) {
                if (dao.markEmailVerified(account.getUserID())) {
                    account.setEmailVerified(true);
                    session.setAttribute("account", account);
                    session.removeAttribute("verify_otp");
                    response.getWriter().write("success");
                } else {
                    response.getWriter().write("Lỗi cập nhật CSDL!");
                }
            } else {
                response.getWriter().write("Mã OTP không chính xác hoặc đã hết hạn!");
            }
            return; // Dừng tại đây, không load trang
        }

        request.getRequestDispatcher(
                "/views/profile.jsp").forward(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
