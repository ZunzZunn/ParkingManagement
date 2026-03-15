<%-- 
    Document   : profile
    Created on : Mar 15, 2026, 6:07:15 PM
    Author     : myniy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    model.User account = (model.User) session.getAttribute("account");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Thông tin cá nhân - iParking</title>
        <link rel="icon" type="image/svg+xml" href="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><rect width='100' height='100' rx='22' fill='%230071e3'/><text x='50' y='55' dominant-baseline='middle' text-anchor='middle' font-family='-apple-system, sans-serif' font-size='65' font-weight='bold' fill='white'>P</text></svg>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/cropperjs/1.5.13/cropper.min.css" rel="stylesheet">
    </head>
    <body>
        <div class="app-container">

            <jsp:include page="sidebar.jsp">
                <jsp:param name="activePage" value="profile" />
            </jsp:include>

            <main class="main-content">
                <jsp:include page="header.jsp">
                    <jsp:param name="title" value="Hồ sơ cá nhân" />
                    <jsp:param name="subtitle" value="Cập nhật thông tin và bảo mật tài khoản" />
                </jsp:include>

                <section class="content-area">
                    <c:if test="${not empty msgSuccess}">
                        <div class="alert alert-success">${msgSuccess}</div>
                    </c:if>
                    <c:if test="${not empty msgError}">
                        <div class="alert alert-error">${msgError}</div>
                    </c:if>

                    <div class="profile-container">
                        <div class="profile-card">
                            <h3><i class="fa-solid fa-address-card"></i> Thông tin cơ bản</h3>

                            <div class="avatar-upload-wrapper">
                                <form action="${pageContext.request.contextPath}/profile" method="POST" enctype="multipart/form-data" id="avatarForm">
                                    <input type="hidden" name="action" value="uploadAvatar">
                                    <div class="avatar-preview" onclick="document.getElementById('avatarInput').click()" title="Nhấn để đổi ảnh">
                                        <c:choose>
                                            <c:when test="${not empty account.avatar}">
                                                <img src="${pageContext.request.contextPath}/${account.avatar}" alt="Avatar">
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fa-solid fa-user"></i>
                                            </c:otherwise>
                                        </c:choose>
                                        <div class="avatar-overlay"><i class="fa-solid fa-camera"></i></div>
                                    </div>
                                    <input type="file" id="avatarInput" name="avatarFile" accept="image/png, image/jpeg" style="display: none;">
                                </form>
                            </div>

                            <form action="${pageContext.request.contextPath}/profile" method="POST">
                                <input type="hidden" name="action" value="updateInfo">

                                <div class="form-group">
                                    <label>Tên đăng nhập</label>
                                    <input type="text" value="<%= account.getUsername() %>" disabled>
                                </div>

                                <div class="form-group">
                                    <label>Họ và Tên <span style="color: var(--apple-error);">*</span></label>
                                    <input type="text" name="fullName" value="<%= account.getFullName() %>" required>
                                </div>

                                <div class="form-group">
                                    <label>Email <span style="color: var(--apple-error);">*</span></label>
                                    <div class="email-input-wrapper">
                                        <input type="email" name="email" value="<%= account.getEmail() != null ? account.getEmail() : "" %>" required>

                                        <c:choose>
                                            <c:when test="${account.emailVerified}">
                                                <div class="badge-verified" title="Email đã xác nhận bảo mật"><i class="fa-solid fa-circle-check"></i></div>
                                                </c:when>
                                                <c:otherwise>
                                                <button type="button" class="btn-verify" id="btnSendVerify">Xác nhận Email</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <c:if test="${not account.emailVerified}">
                                        <small style="color: var(--apple-error); margin-top: 8px; display: block;"><i class="fa-solid fa-triangle-exclamation"></i> Vui lòng xác nhận Email để có thể khôi phục mật khẩu khi cần.</small>
                                    </c:if>
                                </div>

                                <div class="form-group" style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
                                    <div>
                                        <label>Số điện thoại</label>
                                        <input type="text" name="phoneNumber" value="<%= account.getPhoneNumber() %>" required>
                                    </div>
                                    <div>
                                        <label>Ngày sinh</label>
                                        <input type="date" name="dateOfBirth" value="<%= account.getDateOfBirth() != null ? account.getDateOfBirth() : "" %>">
                                    </div>
                                </div>

                                <button type="submit" class="btn-submit">Lưu thay đổi</button>
                            </form>
                        </div>

                        <div class="profile-card">
                            <h3><i class="fa-solid fa-lock"></i> Đổi mật khẩu</h3>
                            <form action="${pageContext.request.contextPath}/profile" method="POST">
                                <input type="hidden" name="action" value="changePassword">

                                <div class="form-group">
                                    <label>Mật khẩu hiện tại</label>
                                    <input type="password" name="oldPassword" required placeholder="Nhập mật khẩu cũ...">
                                </div>

                                <div class="form-group">
                                    <label>Mật khẩu mới</label>
                                    <input type="password" name="newPassword" required placeholder="Nhập mật khẩu mới...">
                                </div>

                                <div class="form-group">
                                    <label>Xác nhận mật khẩu mới</label>
                                    <input type="password" name="confirmPassword" required placeholder="Nhập lại mật khẩu mới...">
                                </div>

                                <button type="submit" class="btn-submit btn-danger">Lưu mật khẩu mới</button>
                            </form>
                        </div>
                    </div>
                </section>
            </main>
        </div>
        <div class="crop-modal" id="cropModal">
            <div class="crop-modal-content profile-card">
                <h3><i class="fa-solid fa-crop-simple"></i> Chỉnh sửa ảnh đại diện</h3>
                <div class="crop-container">
                    <img id="cropImage" src="" alt="Picture">
                </div>
                <div class="crop-actions" style="display: flex; gap: 12px; justify-content: flex-end; margin-top: 20px; align-items: center;">
                    <button type="button" class="btn-submit" id="btnCancelCrop" style="background: var(--apple-border); color: var(--apple-text-dark); width: auto; margin: 0;">Hủy</button>
                    <button type="button" class="btn-submit" id="btnSaveCrop" style="width: auto; margin: 0;">Cắt & Lưu ảnh</button>
                </div>
            </div>
        </div>

        <script src="https://cdnjs.cloudflare.com/ajax/libs/cropperjs/1.5.13/cropper.min.js"></script>
        <script>
                                        const avatarInput = document.getElementById('avatarInput');
                                        const cropModal = document.getElementById('cropModal');
                                        const cropImage = document.getElementById('cropImage');
                                        let cropper;

                                        // Khi người dùng chọn ảnh
                                        avatarInput.addEventListener('change', function (e) {
                                            const files = e.target.files;
                                            if (files && files.length > 0) {
                                                const reader = new FileReader();
                                                reader.onload = function (event) {
                                                    cropImage.src = event.target.result;
                                                    cropModal.classList.add('active'); // Hiện cửa sổ cắt

                                                    // Khởi tạo Cropper (Ép khung hình vuông 1:1)
                                                    if (cropper) {
                                                        cropper.destroy();
                                                    }
                                                    cropper = new Cropper(cropImage, {
                                                        aspectRatio: 1,
                                                        viewMode: 1,
                                                        dragMode: 'move',
                                                        autoCropArea: 0.8,
                                                        restore: false,
                                                        guides: true,
                                                        center: true,
                                                        highlight: false,
                                                        cropBoxMovable: true,
                                                        cropBoxResizable: true,
                                                        toggleDragModeOnDblclick: false,
                                                    });
                                                };
                                                reader.readAsDataURL(files[0]);
                                            }
                                        });

                                        // Khi bấm nút Hủy
                                        document.getElementById('btnCancelCrop').addEventListener('click', () => {
                                            cropModal.classList.remove('active');
                                            avatarInput.value = ''; // Reset input
                                            if (cropper)
                                                cropper.destroy();
                                        });

                                        // Khi bấm nút Cắt & Lưu
                                        document.getElementById('btnSaveCrop').addEventListener('click', () => {
                                            if (!cropper)
                                                return;

                                            const btn = document.getElementById('btnSaveCrop');
                                            btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Đang lưu...';
                                            btn.disabled = true;

                                            // Lấy ảnh đã cắt (Resize về 400x400 cho nhẹ DB)
                                            cropper.getCroppedCanvas({width: 400, height: 400}).toBlob((blob) => {
                                                const formData = new FormData();
                                                formData.append('action', 'uploadAvatar');
                                                // Nén thành file jpeg và gửi lên
                                                formData.append('avatarFile', blob, 'avatar.jpg');

                                                // Gửi ngầm AJAX lên ProfileController
                                                fetch('${pageContext.request.contextPath}/profile', {
                                                    method: 'POST',
                                                    body: formData
                                                }).then(response => {
                                                    window.location.reload(); // Tải lại trang để hiện ảnh mới và thông báo
                                                }).catch(error => {
                                                    alert('Có lỗi xảy ra khi lưu ảnh!');
                                                    btn.innerHTML = 'Cắt & Lưu ảnh';
                                                    btn.disabled = false;
                                                });
                                            }, 'image/jpeg', 0.9);
                                        });
        </script>

        <div class="crop-modal" id="verifyEmailModal">
            <div class="crop-modal-content profile-card" style="max-width: 420px; text-align: center;">
                <h3 style="display: flex; justify-content: center; gap: 10px; align-items: center;"><i class="fa-solid fa-shield-halved" style="color: #34c759;"></i> Xác thực bảo mật</h3>
                <p style="color: var(--apple-text-light); margin-bottom: 24px; line-height: 1.5;">Mã OTP gồm 6 chữ số đã được gửi đến email <br><strong style="color: var(--apple-text-dark);">${account.email}</strong></p>

                <input type="text" id="verifyOtpInput" placeholder="Nhập mã OTP (6 số)" class="form-control" style="text-align: center; margin-bottom: 24px;">
                <p id="verifyMsg" style="color: var(--apple-error); font-size: 14px; margin-bottom: 20px; display: none;"></p>

                <div style="display: flex; gap: 12px; justify-content: center;">
                    <button type="button" class="btn-submit" id="btnCloseVerify" style="background: var(--apple-bg-grey); color: var(--apple-text-dark); flex: 1; margin: 0;">Hủy</button>
                    <button type="button" class="btn-submit" id="btnConfirmVerify" style="flex: 1; margin: 0;">Xác nhận</button>
                </div>
            </div>
        </div>

        <script>
            const btnSendVerify = document.getElementById('btnSendVerify');
            const verifyModal = document.getElementById('verifyEmailModal');

            if (btnSendVerify) {
                btnSendVerify.addEventListener('click', async () => {
                    // Kiểm tra xem user đã lưu email vào CSDL chưa
                    const currentEmail = "${account.email}";
                    if (!currentEmail) {
                        alert("Vui lòng nhập Email và nhấn 'Lưu thay đổi' trước khi xác nhận!");
                        return;
                    }

                    btnSendVerify.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i>';
                    btnSendVerify.disabled = true;

                    try {
                        const params = new URLSearchParams();
                        params.append('action', 'sendVerifyOTP');
                        const res = await fetch('${pageContext.request.contextPath}/profile', {method: 'POST', body: params});
                        const text = await res.text();

                        if (text === 'success') {
                            verifyModal.classList.add('active');
                            document.getElementById('verifyMsg').style.display = 'none';
                            document.getElementById('verifyOtpInput').value = '';
                        } else {
                            alert(text);
                        }
                    } catch (e) {
                        alert("Lỗi kết nối!");
                    }

                    btnSendVerify.innerHTML = 'Xác nhận Email';
                    btnSendVerify.disabled = false;
                });
            }

            document.getElementById('btnCloseVerify').addEventListener('click', () => verifyModal.classList.remove('active'));

            document.getElementById('btnConfirmVerify').addEventListener('click', async () => {
                const btn = document.getElementById('btnConfirmVerify');
                const otp = document.getElementById('verifyOtpInput').value.trim();
                if (!otp)
                    return;

                btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i>';
                btn.disabled = true;

                try {
                    const params = new URLSearchParams();
                    params.append('action', 'confirmVerifyOTP');
                    params.append('otp', otp);

                    const res = await fetch('${pageContext.request.contextPath}/profile', {method: 'POST', body: params});
                    const text = await res.text();

                    if (text === 'success') {
                        window.location.reload(); // Tự động load lại trang để hiện tích xanh
                    } else {
                        const msg = document.getElementById('verifyMsg');
                        msg.style.display = 'block';
                        msg.innerText = text;
                    }
                } catch (e) {
                    alert("Lỗi kết nối!");
                }

                btn.innerHTML = 'Xác nhận';
                btn.disabled = false;
            });
        </script>
    </body>
</html>
