/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.ParkingSlotDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author myniy
 */
public class ParkingMapController extends HttpServlet {

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
            out.println("<title>Servlet ParkingMapController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ParkingMapController at " + request.getContextPath() + "</h1>");
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
        ParkingSlotDAO dao = new ParkingSlotDAO();

        // Lấy danh sách chia theo 3 khu vực thẳng luôn (Filter đã bảo kê vòng ngoài rồi)
        request.setAttribute("motorSlots", dao.getSlotsByType(1));
        request.setAttribute("carSlots", dao.getSlotsByType(2));
        request.setAttribute("bikeSlots", dao.getSlotsByType(3));

        request.getRequestDispatcher("/views/parking-map.jsp").forward(request, response);
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
        response.setContentType("application/json;charset=UTF-8");
        String action = request.getParameter("action");
        model.User account = (model.User) request.getSession().getAttribute("account");

        ParkingSlotDAO dao = new ParkingSlotDAO();

        // Xử lý 3 hành động từ Giao diện gửi lên
        if ("getDetail".equals(action)) {
            int slotId = Integer.parseInt(request.getParameter("slotId"));
            response.getWriter().write(dao.getSlotDetailJson(slotId));
        } else if ("checkIn".equals(action)) {
            int slotId = Integer.parseInt(request.getParameter("slotId"));
            int typeId = Integer.parseInt(request.getParameter("typeId"));
            String licensePlate = request.getParameter("licensePlate").trim().toUpperCase();

            if (dao.quickCheckIn(slotId, licensePlate, typeId, account.getUserID())) {
                response.getWriter().write("{\"status\":\"success\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\"}");
            }
        } else if ("checkOut".equals(action)) {
            int slotId = Integer.parseInt(request.getParameter("slotId"));
            if (dao.quickCheckOut(slotId, account.getUserID())) {
                response.getWriter().write("{\"status\":\"success\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\"}");
            }
        } else if ("toggleMaintenance".equals(action)) {
            int slotId = Integer.parseInt(request.getParameter("slotId"));
            String status = request.getParameter("status"); // Sẽ nhận giá trị "Maintenance" hoặc "Available"

            if (dao.updateSlotStatus(slotId, status)) {
                response.getWriter().write("{\"status\":\"success\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\"}");
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
