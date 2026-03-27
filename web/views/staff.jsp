<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% model.User account = (model.User) session.getAttribute("account"); %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Nhân viên - iParking</title>
        <link rel="icon" type="image/svg+xml" href="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><rect width='100' height='100' rx='22' fill='%230071e3'/><text x='50' y='55' dominant-baseline='middle' text-anchor='middle' font-family='-apple-system, sans-serif' font-size='65' font-weight='bold' fill='white'>P</text></svg>">
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
                    <jsp:param name="subtitle" value="Thêm mới, cập nhật và phân quyền cho nhân sự bãi xe." />
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
                                    <th>STT</th>
                                    <th>Tên nhân viên</th>
                                    <th>Tài khoản</th>
                                    <th>SĐT</th>
                                    <th>Vai trò</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${staffList}" var="staff" varStatus="loop">
                                    <tr>
                                        <td><strong>#${loop.count}</strong></td>

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
                                        <td style="display: flex; gap: 15px; align-items: center;">
                                            <button class="btn-text btn-edit" style="color: #0071e3;" 
                                                    data-id="${staff.userID}" 
                                                    data-name="${staff.fullName}" 
                                                    data-phone="${staff.phoneNumber}" 
                                                    data-email="${staff.email}" 
                                                    data-role="${staff.roleID}">
                                                <i class="fa-solid fa-pen-to-square"></i> Sửa
                                            </button>

                                            <form action="${pageContext.request.contextPath}/staff" method="POST" style="margin: 0;">
                                                <input type="hidden" name="action" value="toggleStatus">
                                                <input type="hidden" name="staffId" value="${staff.userID}">
                                                <input type="hidden" name="currentStatus" value="${staff.isActive}">
                                                <button type="submit" class="btn-text" style="color: ${staff.isActive ? '#ff9500' : '#34c759'};" 
                                                        onclick="return confirm('Đổi trạng thái tài khoản này?')">
                                                    <i class="fa-solid ${staff.isActive ? 'fa-lock' : 'fa-unlock'}"></i> 
                                                    ${staff.isActive ? 'Khóa' : 'Mở'}
                                                </button>
                                            </form>

                                            <form action="${pageContext.request.contextPath}/staff" method="POST" style="margin: 0;">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="staffId" value="${staff.userID}">
                                                <button type="submit" class="btn-text" style="color: #ff3b30;" 
                                                        onclick="return confirm('CẢNH BÁO: Bạn có chắc chắn muốn XÓA VĨNH VIỄN nhân viên này? Hành động này không thể hoàn tác!')">
                                                    <i class="fa-solid fa-trash"></i> Xóa
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
                    <h2>Thêm Tài khoản Mới</h2>
                    <button class="btn-close" id="btnCloseAddModal"><i class="fa-solid fa-xmark"></i></button>
                </div>
                <form action="${pageContext.request.contextPath}/staff" method="POST">
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
                        <label>Email</label>
                        <input type="email" name="email" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Vai trò</label>
                        <select name="roleId" class="form-control">
                            <option value="2">Nhân viên bãi xe</option>
                            <option value="1">Quản trị viên</option>
                        </select>
                    </div>
                    <button type="submit" class="btn-add" style="width: 100%; justify-content: center; margin-top: 20px; font-size: 16px; padding: 12px;">Tạo tài khoản</button>
                </form>
            </div>
        </div>

        <div class="modal-overlay" id="editStaffModal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Cập nhật Thông tin</h2>
                    <button class="btn-close" id="btnCloseEditModal"><i class="fa-solid fa-xmark"></i></button>
                </div>
                <form action="${pageContext.request.contextPath}/staff" method="POST">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="staffId" id="editStaffId">

                    <div class="form-group">
                        <label>Họ và tên</label>
                        <input type="text" name="fullName" id="editFullName" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Số điện thoại</label>
                        <input type="text" name="phoneNumber" id="editPhone" class="form-control">
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" name="email" id="editEmail" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Vai trò</label>
                        <select name="roleId" id="editRole" class="form-control">
                            <option value="2">Nhân viên bãi xe</option>
                            <option value="1">Quản trị viên</option>
                        </select>
                    </div>
                    <button type="submit" class="btn-add" style="width: 100%; justify-content: center; margin-top: 20px; font-size: 16px; padding: 12px; background-color: #0071e3;">Lưu Thay Đổi</button>
                </form>
            </div>
        </div>

        <script>
            const addModal = document.getElementById('addStaffModal');
            document.getElementById('btnOpenAddModal').addEventListener('click', () => addModal.classList.add('active'));
            document.getElementById('btnCloseAddModal').addEventListener('click', () => addModal.classList.remove('active'));

            const editModal = document.getElementById('editStaffModal');
            document.getElementById('btnCloseEditModal').addEventListener('click', () => editModal.classList.remove('active'));

            document.addEventListener('click', function (e) {
                if (e.target === addModal)
                    addModal.classList.remove('active');
                if (e.target === editModal)
                    editModal.classList.remove('active');

                const btnEdit = e.target.closest('.btn-edit');
                if (btnEdit) {
                    e.preventDefault();
                    document.getElementById('editStaffId').value = btnEdit.getAttribute('data-id');
                    document.getElementById('editFullName').value = btnEdit.getAttribute('data-name');
                    document.getElementById('editPhone').value = btnEdit.getAttribute('data-phone');
                    document.getElementById('editEmail').value = btnEdit.getAttribute('data-email');
                    document.getElementById('editRole').value = btnEdit.getAttribute('data-role');
                    editModal.classList.add('active');
                }
            });
        </script>

        <div id="appleToast" class="apple-notification">
            <div class="notif-icon" id="toastIcon">
                <i class="fa-solid fa-info"></i>
            </div>
            <div class="notif-content">
                <h4 id="toastTitle">Thông báo</h4>
                <p id="toastMessage">Nội dung thông báo</p>
            </div>
            <button class="notif-close" onclick="closeToast()">
                <i class="fa-solid fa-xmark"></i>
            </button>
        </div>

        <script>
            function showToast(title, message, type = 'error') {
                const toast = document.getElementById('appleToast');
                const iconDiv = document.getElementById('toastIcon');

                document.getElementById('toastTitle').innerText = title;
                document.getElementById('toastMessage').innerText = message;

                if (type === 'error') {
                    iconDiv.style.background = 'rgba(255, 59, 48, 0.1)';
                    iconDiv.style.color = '#ff3b30';
                    iconDiv.innerHTML = '<i class="fa-solid fa-triangle-exclamation"></i>';
                } else if (type === 'success') {
                    iconDiv.style.background = 'rgba(52, 199, 89, 0.1)';
                    iconDiv.style.color = '#34c759';
                    iconDiv.innerHTML = '<i class="fa-solid fa-check"></i>';
                }

                toast.classList.add('show');
                setTimeout(closeToast, 4000);
            }

            function closeToast() {
                document.getElementById('appleToast').classList.remove('show');
            }
        </script>

        <c:if test="${not empty sessionScope.errorMessage}">
            <script>
                window.addEventListener('DOMContentLoaded', () => {
                    showToast('Cảnh báo bảo mật', '${sessionScope.errorMessage}', 'error');
                });
            </script>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.successMessage}">
            <script>
                window.addEventListener('DOMContentLoaded', () => {
                    showToast('Thành công', '${sessionScope.successMessage}', 'success');
                });
            </script>
            <c:remove var="successMessage" scope="session"/>
        </c:if>

        <div id="appleToast" class="apple-notification">
            <div class="notif-icon" id="toastIcon">
                <i class="fa-solid fa-info"></i>
            </div>
            <div class="notif-content">
                <h4 id="toastTitle">Thông báo</h4>
                <p id="toastMessage">Nội dung thông báo</p>
            </div>
            <button class="notif-close" onclick="closeToast()">
                <i class="fa-solid fa-xmark"></i>
            </button>
        </div>

        <script>
            function showToast(title, message, type = 'error') {
                const toast = document.getElementById('appleToast');
                const iconDiv = document.getElementById('toastIcon');

                document.getElementById('toastTitle').innerText = title;
                document.getElementById('toastMessage').innerText = message;

                if (type === 'error') {
                    iconDiv.style.background = 'rgba(255, 59, 48, 0.1)';
                    iconDiv.style.color = '#ff3b30';
                    iconDiv.innerHTML = '<i class="fa-solid fa-triangle-exclamation"></i>';
                } else if (type === 'success') {
                    iconDiv.style.background = 'rgba(52, 199, 89, 0.1)';
                    iconDiv.style.color = '#34c759';
                    iconDiv.innerHTML = '<i class="fa-solid fa-check"></i>';
                } else {
                    iconDiv.style.background = 'rgba(0, 113, 227, 0.1)';
                    iconDiv.style.color = '#0071e3';
                    iconDiv.innerHTML = '<i class="fa-solid fa-info"></i>';
                }

                toast.classList.add('show');
                setTimeout(closeToast, 4000);
            }

            function closeToast() {
                document.getElementById('appleToast').classList.remove('show');
            }
        </script>

        <c:if test="${not empty sessionScope.errorMessage}">
            <script>
                window.addEventListener('DOMContentLoaded', () => {
                    showToast('Cảnh báo', '${sessionScope.errorMessage}', 'error');
                });
            </script>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.successMessage}">
            <script>
                window.addEventListener('DOMContentLoaded', () => {
                    showToast('Thành công', '${sessionScope.successMessage}', 'success');
                });
            </script>
            <c:remove var="successMessage" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.dbMessage}">
            <script>
                window.addEventListener('DOMContentLoaded', () => {
                    let msg = '${sessionScope.dbMessage}';
                    // Tự động bắt từ khóa để đoán xem đây là thông báo Lỗi hay Thành công
                    let type = (msg.includes('LỖI') || msg.includes('Không thể')) ? 'error' : 'success';
                    let title = type === 'error' ? 'Cảnh báo thao tác' : 'Thành công';

                    showToast(title, msg, type);
                });
            </script>
            <c:remove var="dbMessage" scope="session"/>
        </c:if>
    </body>
</html>