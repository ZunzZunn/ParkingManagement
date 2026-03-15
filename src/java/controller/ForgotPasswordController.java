/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Random;
import util.EmailUtil;

/**
 *
 * @author myniy
 */
public class ForgotPasswordController extends HttpServlet {

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
            out.println("<title>Servlet ForgotPasswordController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ForgotPasswordController at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
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
        response.setContentType("text/plain;charset=UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        UserDAO dao = new UserDAO();

        if ("sendOTP".equals(action)) {
            String username = request.getParameter("username");
            String email = request.getParameter("email");

            if (dao.checkUserEmail(username, email)) {
                // Tạo OTP 6 số ngẫu nhiên
                String otp = String.format("%06d", new Random().nextInt(999999));
                session.setAttribute("reset_otp", otp);
                session.setAttribute("reset_username", username);

                // Set thời gian sống của OTP là 5 phút (300 giây)
                session.setMaxInactiveInterval(300);

                if (EmailUtil.sendOTP(email, otp)) {
                    response.getWriter().write("success");
                } else {
                    response.getWriter().write("Lỗi hệ thống khi gửi mail!");
                }
            } else {
                response.getWriter().write("Tên đăng nhập hoặc Email không đúng!");
            }
        } else if ("verifyAndReset".equals(action)) {
            String inputOtp = request.getParameter("otp");
            String newPass = request.getParameter("newPassword");

            String sessionOtp = (String) session.getAttribute("reset_otp");
            String sessionUsername = (String) session.getAttribute("reset_username");

            if (sessionOtp != null && sessionOtp.equals(inputOtp)) {
                if (dao.resetPassword(sessionUsername, newPass)) {
                    session.invalidate();
                    response.getWriter().write("success");
                } else {
                    response.getWriter().write("Lỗi cập nhật mật khẩu!");
                }
            } else {
                response.getWriter().write("Mã OTP không chính xác hoặc đã hết hạn!");
            }
        }
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
