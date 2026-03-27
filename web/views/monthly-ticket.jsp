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
                            <h2 id="passCount">Danh sách Vé tháng (${totalPasses != null ? totalPasses : 0} vé)</h2>
                            <button id="btnOpenModal" class="btn-add"><i class="fa-solid fa-plus"></i> Đăng ký vé mới</button>
                        </div>

                        <table class="apple-table">
                            <thead>
                                <tr>
                                    <th>Mã vé</th>
                                    <th>Khách hàng</th>
                                    <th>SĐT</th>
                                    <th>Biển số</th>
                                    <th>Khu vực (Ô đỗ)</th>
                                    <th>Ngày đăng ký</th>
                                    <th>Ngày hết hạn</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody id="tableBody">
                                <c:forEach items="${passes}" var="p">
                                    <tr>
                                        <td><strong>#${p.passId}</strong></td>
                                        <td>${p.customerName}</td>
                                        <td>${p.phoneNumber}</td>
                                        <td><strong>${p.licensePlate}</strong></td>

                                        <td>${p.slotCode}</td>

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
                                            <button class="btn-text btn-renew" data-id="${p.passId}" data-plate="${p.licensePlate}" data-name="${p.customerName}">
                                                <i class="fa-solid fa-rotate"></i> Gia hạn
                                            </button>
                                            <button class="btn-text btn-history" data-id="${p.passId}" style="color: #5856d6;">
                                                <i class="fa-solid fa-clock-rotate-left"></i> Lịch sử
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>

                                <c:if test="${empty passes}">
                                    <tr class="empty-row">
                                        <td colspan="9" style="text-align: center; padding: 30px; color: var(--apple-text-light);">
                                            Không tìm thấy vé tháng nào phù hợp với bộ lọc.
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>

                        <c:if test="${totalPages > 1}">
                            <c:set var="q" value="&licensePlate=${param.licensePlate}&customerInfo=${param.customerInfo}&status=${param.status}" />
                            <div class="pagination">
                                <c:if test="${currentPage > 1}">
                                    <a href="monthly-ticket?page=${currentPage - 1}${q}" class="page-btn"><i class="fa-solid fa-chevron-left"></i></a>
                                    </c:if>

                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <a href="monthly-ticket?page=${i}${q}" class="page-btn ${currentPage == i ? 'active' : ''}">${i}</a>
                                </c:forEach>

                                <c:if test="${currentPage < totalPages}">
                                    <a href="monthly-ticket?page=${currentPage + 1}${q}" class="page-btn"><i class="fa-solid fa-chevron-right"></i></a>
                                    </c:if>
                            </div>
                        </c:if>

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
                        <label>Loại phương tiện</label>
                        <select name="typeId" id="vehicleTypeSelect" class="form-control" required>
                            <option value="1">Xe máy</option>
                            <option value="2">Ô tô 4-5 chỗ</option>
                            <option value="3">Xe đạp / Xe điện</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Khu vực (Ô đỗ)</label>
                        <select name="slotId" id="slotSelect" class="form-control" required>
                            <option value="">-- Chọn ô đỗ trống --</option>
                            <c:forEach items="${emptySlots}" var="s">
                                <option value="${s.slotId}" data-type="${s.typeId}">[${s.zone}] - ${s.slotCode}</option>
                            </c:forEach>
                        </select>
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

        <div class="modal-overlay" id="historyModal">
            <div class="modal-content" style="width: 800px; max-width: 95%;">
                <div class="modal-header">
                    <h2>Lịch sử Gia hạn</h2>
                    <button class="btn-close" id="btnCloseHistoryModal"><i class="fa-solid fa-xmark"></i></button>
                </div>
                <div class="modal-body" style="padding: 20px 0;">
                    <table class="apple-table" style="margin: 0;">
                        <thead>
                            <tr>
                                <th>Thời gian giao dịch</th>
                                <th>Gói gia hạn</th>
                                <th>Hạn mới sau giao dịch</th>
                                <th>Người thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="historyTableBody">
                        </tbody>
                    </table>
                </div>
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

            // Hàm lấy dữ liệu với hiệu ứng Mờ đi (Dim) + Trượt mượt mà (FLIP) ĐÃ CẬP NHẬT
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

                // --- Hiệu ứng phản hồi tức thì (Dim Effect) ---
                tableBody.style.transition = 'opacity 0.2s ease';
                tableBody.style.opacity = '0.4';

                try {
                    const response = await fetch('monthly-ticket?' + urlParams);
                    const html = await response.text();
                    const doc = new DOMParser().parseFromString(html, 'text/html');

                    // 2. Cập nhật DOM nhanh
                    tableBody.innerHTML = doc.getElementById('tableBody').innerHTML;

                    // Cập nhật số lượng vé trên tiêu đề
                    const newCount = doc.getElementById('passCount');
                    if (newCount)
                        document.getElementById('passCount').innerHTML = newCount.innerHTML;

                    // Cập nhật thanh phân trang
                    const newPagination = doc.querySelector('.pagination');
                    const currentPagination = document.querySelector('.pagination');
                    if (newPagination && currentPagination) {
                        currentPagination.innerHTML = newPagination.innerHTML;
                    } else if (newPagination && !currentPagination) {
                        document.querySelector('.apple-table').insertAdjacentHTML('afterend', newPagination.outerHTML);
                    } else if (!newPagination && currentPagination) {
                        currentPagination.remove();
                    }

                    // 3. Kích hoạt hiệu ứng trượt (FLIP) và khôi phục độ sáng
                    requestAnimationFrame(() => {
                        tableBody.style.opacity = '1';

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
                    tableBody.style.opacity = '1'; // Phục hồi hiển thị nếu lỗi
                }
            }

            // Bắt sự kiện gõ phím hoặc chọn select
            filterForm.addEventListener('input', (e) => {
                clearTimeout(debounceTimer);
                const delay = (e.target.type === 'text') ? 250 : 0;

                debounceTimer = setTimeout(() => {
                    const params = new URLSearchParams(new FormData(filterForm));
                    params.delete('page'); // Bắt buộc về trang 1 khi đổi bộ lọc
                    fetchTableData(params.toString());
                }, delay);
            });

            // Xử lý sự kiện khi bấm chuyển trang (Phân trang AJAX)
            document.addEventListener('click', (e) => {
                const btn = e.target.closest('.page-btn');
                if (btn) {
                    e.preventDefault();
                    const url = new URL(btn.href);
                    fetchTableData(url.searchParams.toString());
                }
            });
        </script>

        <script>
            // Xử lý Modal Gia hạn (Đã gộp listener bên trên, giữ nguyên phòng trường hợp thừa)
            const btnCloseRenewModal = document.getElementById('btnCloseRenewModal');

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

        <script>
            // XỬ LÝ LỌC Ô ĐỖ THEO LOẠI XE
            document.addEventListener("DOMContentLoaded", function () {
                const vehicleTypeSelect = document.getElementById('vehicleTypeSelect');
                const slotSelect = document.getElementById('slotSelect');

                function filterSlots() {
                    const selectedType = vehicleTypeSelect.value;
                    const options = slotSelect.querySelectorAll('option');

                    // Reset lại lựa chọn khi đổi loại xe
                    slotSelect.value = "";

                    options.forEach(option => {
                        // Bỏ qua tùy chọn mặc định đầu tiên
                        if (option.value === "")
                            return;

                        // Ẩn/hiện dựa trên data-type có khớp với loại xe được chọn hay không
                        if (option.getAttribute('data-type') === selectedType) {
                            option.hidden = false;
                        } else {
                            option.hidden = true;
                        }
                    });
                }

                // Gọi hàm lọc ngay khi form vừa mở lên (để lọc theo "Xe máy" là giá trị mặc định)
                filterSlots();

                // Lắng nghe sự kiện mỗi khi người dùng đổi loại xe
                vehicleTypeSelect.addEventListener('change', filterSlots);

                // (Tùy chọn) Khi mở Modal thêm mới, chạy lại bộ lọc cho chắc chắn
                document.getElementById('btnOpenModal').addEventListener('click', filterSlots);
            });
        </script>

        <script>
            const historyModal = document.getElementById('historyModal');

            document.addEventListener('click', async function (e) {
                // 1. Mở Modal Lịch sử
                const btnHistory = e.target.closest('.btn-history');
                if (btnHistory) {
                    e.preventDefault();
                    const passId = btnHistory.getAttribute('data-id');
                    const tbody = document.getElementById('historyTableBody');

                    // Hiện trạng thái đang tải
                    tbody.innerHTML = "<tr><td colspan='3' style='text-align:center;'>Đang tải dữ liệu...</td></tr>";
                    historyModal.classList.add('active');

                    // Gọi AJAX lấy lịch sử từ Controller
                    try {
                        const response = await fetch('monthly-ticket?action=getHistory&passId=' + passId);
                        const htmlText = await response.text();
                        tbody.innerHTML = htmlText; // Đổ kết quả vào bảng
                    } catch (error) {
                        tbody.innerHTML = "<tr><td colspan='3' style='text-align:center; color:red;'>Lỗi khi tải dữ liệu</td></tr>";
                    }
                }

                // 2. Đóng Modal
                if (e.target === historyModal || e.target.closest('#btnCloseHistoryModal')) {
                    historyModal.classList.remove('active');
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
                    // Đóng tất cả modal nếu đang mở
                    document.getElementById('addModal').classList.remove('active');
                    document.getElementById('renewModal').classList.remove('active');
                    // Hiện thông báo lỗi
                    showToast('Lỗi Đăng ký', '${sessionScope.errorMessage}', 'error');
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
    </body>
</html>
