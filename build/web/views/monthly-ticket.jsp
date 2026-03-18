<%-- 
    Document   : monthly-ticket
    Created on : Mar 16, 2026, 1:55:44 AM
    Author     : myniy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% model.User account = (model.User) session.getAttribute("account"); %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Vé tháng - iParking</title>
        <link rel="icon" type="image/svg+xml" href="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><rect width='100' height='100' rx='22' fill='%230071e3'/><text x='50' y='55' dominant-baseline='middle' text-anchor='middle' font-family='-apple-system, sans-serif' font-size='65' font-weight='bold' fill='white'>P</text></svg>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="sidebar.jsp">
                <jsp:param name="activePage" value="monthly_ticket" />
            </jsp:include>

            <main class="main-content">
                <jsp:include page="header.jsp">
                    <jsp:param name="title" value="Quản lý Vé tháng" />
                    <jsp:param name="subtitle" value="Quản lý thông tin và gia hạn khách hàng đăng ký đỗ xe cố định." />
                </jsp:include>

                <section class="content-area">
                    <div class="filter-card">
                        <form action="monthly-ticket" method="GET" class="filter-grid" id="filterForm">
                            <div class="form-group" style="margin: 0;">
                                <label>Biển số xe</label>
                                <input type="text" name="licensePlate" value="${param.licensePlate}" class="form-control auto-format-plate" placeholder="Nhập biển số...">
                            </div>
                            <div class="form-group" style="margin: 0;">
                                <label>Khách hàng / SĐT</label>
                                <input type="text" name="customerInfo" value="${param.customerInfo}" class="form-control" placeholder="Tên hoặc SĐT...">
                            </div>
                            <div class="form-group" style="margin: 0;">
                                <label>Trạng thái</label>
                                <select name="status" class="form-control">
                                    <option value="">-- Tất cả --</option>
                                    <option value="Active" ${param.status == 'Active' ? 'selected' : ''}>Đang hoạt động</option>
                                    <option value="ExpiringSoon" ${param.status == 'ExpiringSoon' ? 'selected' : ''}>Sắp hết hạn</option>
                                    <option value="Expired" ${param.status == 'Expired' ? 'selected' : ''}>Đã hết hạn</option>
                                </select>
                            </div>

                            <a href="monthly-ticket" class="btn-filter" style="background: var(--apple-border); color: var(--apple-text-dark); text-align: center; text-decoration: none; line-height: 40px;" title="Xóa bộ lọc">
                                <i class="fa-solid fa-rotate-right"></i>
                            </a>
                        </form>
                    </div>

                    <div class="content-card">
                        <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
                            <h2>Danh sách Vé tháng</h2>
                            <button id="btnOpenModal" class="btn-add"><i class="fa-solid fa-plus"></i> Đăng ký vé mới</button>
                        </div>

                        <table class="apple-table">
                            <thead>
                                <tr>
                                    <th>Mã vé</th>
                                    <th>Khách hàng</th>
                                    <th>SĐT</th>
                                    <th>Biển số</th>
                                    <th>Ngày đăng ký</th>
                                    <th>Ngày hết hạn</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody id="tableBody">
                                <c:forEach items="${passes}" var="p">
                                    <tr>
                                        <td><strong>#${p.passID}</strong></td>
                                        <td>${p.customerName}</td>
                                        <td>${p.phoneNumber}</td>
                                        <td><strong>${p.licensePlate}</strong></td>
                                        <td><fmt:formatDate value="${p.startDate}" pattern="dd/MM/yyyy"/></td>
                                        <td><strong style="color: var(--apple-text-dark);"><fmt:formatDate value="${p.endDate}" pattern="dd/MM/yyyy"/></strong></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${p.statusBadge == 'Active'}">
                                                    <span class="badge" style="background: rgba(52, 199, 89, 0.1); color: #34c759;"><i class="fa-solid fa-check-circle"></i> Hoạt động</span>
                                                </c:when>
                                                <c:when test="${p.statusBadge == 'ExpiringSoon'}">
                                                    <span class="badge" style="background: rgba(255, 149, 0, 0.1); color: #ff9500;"><i class="fa-solid fa-clock"></i> Sắp hết hạn</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge" style="background: rgba(255, 59, 48, 0.1); color: #ff3b30;"><i class="fa-solid fa-circle-xmark"></i> Hết hạn</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <button class="btn-text btn-renew" data-id="${p.passID}" data-plate="${p.licensePlate}" data-name="${p.customerName}">
                                                <i class="fa-solid fa-rotate"></i> Gia hạn
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </section>
            </main>
        </div>

        <div class="modal-overlay" id="addModal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Đăng ký Vé tháng mới</h2>
                    <button class="btn-close" id="btnCloseModal"><i class="fa-solid fa-xmark"></i></button>
                </div>
                <form action="${pageContext.request.contextPath}/monthly-ticket" method="POST">
                    <input type="hidden" name="action" value="register">

                    <div class="form-group">
                        <label>Tên khách hàng</label>
                        <input type="text" name="customerName" class="form-control" placeholder="Nhập họ và tên..." required>
                    </div>
                    <div class="form-group">
                        <label>Số điện thoại</label>
                        <input type="tel" name="phoneNumber" class="form-control" placeholder="Nhập SĐT liên hệ..." required>
                    </div>
                    <div class="form-group">
                        <label>Biển số xe</label>
                        <input type="text" name="licensePlate" class="form-control auto-format-plate" placeholder="VD: 29A-123.45" required>
                    </div>
                    <div class="form-group">
                        <label>Gói gia hạn</label>
                        <select name="duration" class="form-control" required>
                            <option value="1">1 Tháng</option>
                            <option value="3">3 Tháng (Giảm 5%)</option>
                            <option value="6">6 Tháng (Giảm 10%)</option>
                        </select>
                    </div>

                    <button type="submit" class="btn-add" style="width: 100%; justify-content: center; margin-top: 20px; font-size: 16px; padding: 12px;">Đăng ký ngay</button>
                </form>
            </div>
        </div>

        <div class="modal-overlay" id="renewModal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Gia hạn Vé tháng</h2>
                    <button class="btn-close" id="btnCloseRenewModal"><i class="fa-solid fa-xmark"></i></button>
                </div>
                <form action="${pageContext.request.contextPath}/monthly-ticket" method="POST">
                    <input type="hidden" name="action" value="renew">
                    <input type="hidden" name="passID" id="renewPassID">

                    <div class="form-group">
                        <label>Khách hàng</label>
                        <input type="text" id="renewCustomerName" class="form-control" disabled style="background: var(--apple-bg); color: var(--apple-text-light);">
                    </div>
                    <div class="form-group">
                        <label>Biển số xe</label>
                        <input type="text" id="renewLicensePlate" class="form-control" disabled style="background: var(--apple-bg); color: var(--apple-text-light); font-weight: bold; letter-spacing: 1px;">
                    </div>
                    <div class="form-group">
                        <label>Gói gia hạn mới</label>
                        <select name="duration" class="form-control" required>
                            <option value="1">1 Tháng</option>
                            <option value="3">3 Tháng (Giảm 5%)</option>
                            <option value="6">6 Tháng (Giảm 10%)</option>
                        </select>
                    </div>

                    <button type="submit" class="btn-add" style="width: 100%; justify-content: center; margin-top: 20px; font-size: 16px; padding: 12px; background-color: #34c759; box-shadow: 0 4px 12px rgba(52, 199, 89, 0.3);">
                        <i class="fa-solid fa-rotate"></i> Xác nhận gia hạn
                    </button>
                </form>
            </div>
        </div>

        <script>
            // --- XỬ LÝ ĐÓNG MỞ CÁC MODAL ---
            const addModal = document.getElementById('addModal');
            const renewModal = document.getElementById('renewModal');

            // 1. Mở Modal Đăng ký mới
            document.getElementById('btnOpenModal').addEventListener('click', () => {
                addModal.classList.add('active');
            });

            // 2. Đóng Modal Đăng ký mới
            document.getElementById('btnCloseModal').addEventListener('click', () => {
                addModal.classList.remove('active');
            });

            // 3. Đóng Modal Gia hạn
            document.getElementById('btnCloseRenewModal').addEventListener('click', () => {
                renewModal.classList.remove('active');
            });

            // 4. Lắng nghe click toàn trang (Mở gia hạn + Đóng khi nhấn ra ngoài)
            document.addEventListener('click', function (e) {
                // Nhấn ra vùng đen để đóng
                if (e.target === addModal)
                    addModal.classList.remove('active');
                if (e.target === renewModal)
                    renewModal.classList.remove('active');

                // Nếu nhấn vào nút Gia hạn trong bảng
                const btnRenew = e.target.closest('.btn-renew');
                if (btnRenew) {
                    e.preventDefault();

                    // Lấy dữ liệu gán vào form
                    document.getElementById('renewPassID').value = btnRenew.getAttribute('data-id');
                    document.getElementById('renewLicensePlate').value = btnRenew.getAttribute('data-plate');
                    document.getElementById('renewCustomerName').value = btnRenew.getAttribute('data-name');

                    // Bật Modal
                    renewModal.classList.add('active');
                }
            });

            // Auto-format Biển số xe
            document.addEventListener('input', function (e) {
                if (e.target && e.target.classList.contains('auto-format-plate')) {
                    let val = e.target.value.toUpperCase();
                    let clean = val.replace(/[^A-Z0-9]/g, '');
                    let formatted = clean;

                    if (clean.length >= 7) {
                        let tail5 = clean.slice(-5);
                        let tail4 = clean.slice(-4);
                        if (/^\d{5}$/.test(tail5)) {
                            formatted = clean.slice(0, -5) + '-' + tail5.slice(0, 3) + '.' + tail5.slice(3);
                        } else if (/^\d{4}$/.test(tail4)) {
                            formatted = clean.slice(0, -4) + '-' + tail4;
                        }
                    }

                    if (e.target.value !== formatted) {
                        e.target.value = formatted;
                    }
                }
            });
        </script>

        <script>
            const filterForm = document.getElementById('filterForm');
            const tableBody = document.getElementById('tableBody');
            let debounceTimer;

            // Hàm lấy dữ liệu với hiệu ứng trượt mượt mà (FLIP Animation)
            async function fetchTableData(urlParams) {
                // 1. Ghi lại vị trí các dòng cũ trước khi dữ liệu thay đổi
                const positions = new Map();
                tableBody.querySelectorAll('tr').forEach(row => {
                    // Dùng thẻ strong đầu tiên (chứa Mã vé, VD: #123) làm ID định danh
                    const id = row.querySelector('strong')?.innerText;
                    if (id) {
                        positions.set(id, row.getBoundingClientRect().top);
                    }
                });

                try {
                    const response = await fetch('monthly-ticket?' + urlParams);
                    const html = await response.text();
                    const doc = new DOMParser().parseFromString(html, 'text/html');

                    // 2. Cập nhật DOM nhanh
                    tableBody.innerHTML = doc.getElementById('tableBody').innerHTML;

                    // 3. Kích hoạt hiệu ứng trượt (FLIP)
                    requestAnimationFrame(() => {
                        tableBody.querySelectorAll('tr').forEach(row => {
                            const id = row.querySelector('strong')?.innerText;
                            const oldTop = positions.get(id);

                            if (oldTop) {
                                const delta = oldTop - row.getBoundingClientRect().top;
                                if (delta !== 0) {
                                    row.style.transition = 'none';
                                    row.style.transform = `translateY(${delta}px)`;

                                    requestAnimationFrame(() => {
                                        row.style.transition = 'transform 0.5s cubic-bezier(0.2, 1, 0.2, 1)';
                                        row.style.transform = '';
                                    });
                                }
                            } else {
                                // Dòng dữ liệu mới xuất hiện (chưa có vị trí cũ) -> Áp dụng fade-in
                                row.style.opacity = '0';
                                setTimeout(() => {
                                    row.style.transition = 'opacity 0.4s';
                                    row.style.opacity = '1';
                                }, 50);
                            }
                        });
                    });

                    // 4. Đồng bộ URL để không bị mất bộ lọc khi người dùng F5
                    window.history.pushState({}, '', 'monthly-ticket?' + urlParams);
                } catch (e) {
                    console.error("Lỗi tải bảng:", e);
                }
            }

            // Bắt sự kiện gõ phím hoặc chọn select
            filterForm.addEventListener('input', (e) => {
                clearTimeout(debounceTimer);
                // Gõ chữ thì đợi 250ms mới lọc, chọn select thì lọc luôn
                const delay = (e.target.type === 'text') ? 250 : 0;

                debounceTimer = setTimeout(() => {
                    const params = new URLSearchParams(new FormData(filterForm));
                    fetchTableData(params.toString());
                }, delay);
            });
        </script>

        <script>
            // Xử lý Modal Gia hạn
            const renewModal = document.getElementById('renewModal');
            const btnCloseRenewModal = document.getElementById('btnCloseRenewModal');

            // Lắng nghe sự kiện click trên toàn bộ Document (Hỗ trợ tốt cho bảng dùng AJAX)
            document.addEventListener('click', function (e) {
                // Nếu click trúng nút Gia Hạn
                const btnRenew = e.target.closest('.btn-renew');
                if (btnRenew) {
                    e.preventDefault();

                    // Lấy dữ liệu từ các thuộc tính data-
                    const passID = btnRenew.getAttribute('data-id');
                    const plate = btnRenew.getAttribute('data-plate');
                    const name = btnRenew.getAttribute('data-name');

                    // Đổ dữ liệu vào Form
                    document.getElementById('renewPassID').value = passID;
                    document.getElementById('renewLicensePlate').value = plate;
                    document.getElementById('renewCustomerName').value = name;

                    // Hiển thị Modal
                    renewModal.classList.add('active');
                }
            });

            // Đóng Modal khi bấm X
            btnCloseRenewModal.addEventListener('click', () => {
                renewModal.classList.remove('active');
            });

            // Đóng Modal khi bấm ra ngoài khoảng đen
            window.addEventListener('click', (e) => {
                if (e.target === renewModal) {
                    renewModal.classList.remove('active');
                }
            });
        </script>
    </body>
</html>
