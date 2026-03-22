<%-- 
    Document   : staff
    Created on : Mar 22, 2026, 10:31:57 AM
    Author     : Dell
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% model.User account = (model.User) session.getAttribute("account"); %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Nhân viên - iParking</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="sidebar.jsp">
                <jsp:param name="activePage" value="staff" />
            </jsp:include>

            <main class="main-content">
                <jsp:include page="header.jsp">
                    <jsp:param name="title" value="Quản lý Nhân viên" />
                    <jsp:param name="subtitle" value="Quản lý tài khoản và phân quyền cho nhân viên bãi xe." />
                </jsp:include>

                <section class="content-area">
                    <div class="content-card">
                        <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
                            <h2>Danh sách Nhân viên</h2>
                            <button id="btnOpenAddModal" class="btn-add"><i class="fa-solid fa-user-plus"></i> Thêm nhân viên</button>
                        </div>

                        <table class="apple-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Tên nhân viên</th>
                                    <th>Tài khoản</th>
                                    <th>Số điện thoại</th>
                                    <th>Vai trò</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${staffList}" var="staff">
                                    <tr>
                                        <td><strong>#${staff.userID}</strong></td>
                                        <td>${staff.fullName}</td>
                                        <td>${staff.username}</td>
                                        <td>${staff.phoneNumber}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${staff.roleID == 1}">
                                                    <span style="color: var(--apple-blue); font-weight: bold;">Quản trị viên</span>
                                                </c:when>
                                                <c:otherwise>Nhân viên</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${staff.isActive}">
                                                    <span class="badge" style="background: rgba(52, 199, 89, 0.1); color: #34c759;">Hoạt động</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge" style="background: rgba(255, 59, 48, 0.1); color: #ff3b30;">Đã khóa</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <form action="staff" method="POST" style="margin: 0;">
                                                <input type="hidden" name="action" value="toggleStatus">
                                                <input type="hidden" name="staffId" value="${staff.userID}">
                                                <input type="hidden" name="currentStatus" value="${staff.isActive}">
                                                <button type="submit" class="btn-text" style="color: ${staff.isActive ? '#ff3b30' : '#34c759'};" onclick="return confirm('Bạn có chắc muốn đổi trạng thái tài khoản này?')">
                                                    <i class="fa-solid ${staff.isActive ? 'fa-lock' : 'fa-unlock'}"></i> 
                                                    ${staff.isActive ? 'Khóa' : 'Mở khóa'}
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </section>
            </main>
        </div>

        <div class="modal-overlay" id="addStaffModal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Thêm Tài khoản Nhân viên</h2>
                    <button class="btn-close" id="btnCloseAddModal"><i class="fa-solid fa-xmark"></i></button>
                </div>
                <form action="${pageContext.request.contextPath}/staff" method="POST">
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" name="email" class="form-control" required>
                    </div>
                    <input type="hidden" name="action" value="add">
                    <div class="form-group">
                        <label>Họ và tên</label>
                        <input type="text" name="fullName" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Tên đăng nhập</label>
                        <input type="text" name="username" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Mật khẩu khởi tạo</label>
                        <input type="password" name="password" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Số điện thoại</label>
                        <input type="text" name="phoneNumber" class="form-control">
                    </div>
                    <div class="form-group">
                        <label>Vai trò</label>
                        <select name="role" class="form-control">
                            <option value="Staff">Nhân viên bãi xe</option>
                            <option value="Admin">Quản trị viên</option>
                        </select>
                    </div>
                    <button type="submit" class="btn-add" style="width: 100%; justify-content: center; margin-top: 20px; font-size: 16px; padding: 12px;">Tạo tài khoản</button>
                </form>
            </div>
        </div>

        <script>
            const addModal = document.getElementById('addStaffModal');
            document.getElementById('btnOpenAddModal').addEventListener('click', () => addModal.classList.add('active'));
            document.getElementById('btnCloseAddModal').addEventListener('click', () => addModal.classList.remove('active'));
            window.addEventListener('click', (e) => {
                if (e.target === addModal)
                    addModal.classList.remove('active');
            });
        </script>
    </body>
</html>