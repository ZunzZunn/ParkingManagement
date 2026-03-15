<%-- 
    Document   : header
    Created on : Mar 15, 2026, 6:37:13 PM
    Author     : myniy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<header class="top-header">
    <div class="greeting">
        <h1>${param.title}</h1>
        <p>${param.subtitle}</p>
    </div>

    <div class="header-actions">
        <label class="theme-switch" for="checkbox" title="Đổi giao diện">
            <input type="checkbox" id="checkbox" />
            <div class="slider">
                <i class="fa-solid fa-moon"></i>
                <i class="fa-solid fa-sun"></i>
            </div>
        </label>
    </div>
</header>

<script>
    const toggleSwitch = document.querySelector('.theme-switch input[type="checkbox"]');

    function syncTheme() {
        const currentTheme = localStorage.getItem('theme');
        if (currentTheme === 'dark') {
            document.documentElement.setAttribute('data-theme', 'dark');
            if (toggleSwitch)
                toggleSwitch.checked = true;
        } else {
            document.documentElement.removeAttribute('data-theme');
            if (toggleSwitch)
                toggleSwitch.checked = false;
        }
    }

    syncTheme();

    window.addEventListener('pageshow', function (event) {
        syncTheme();
    });

    if (toggleSwitch) {
        toggleSwitch.addEventListener('change', function (e) {
            if (e.target.checked) {
                document.documentElement.setAttribute('data-theme', 'dark');
                localStorage.setItem('theme', 'dark');
            } else {
                document.documentElement.removeAttribute('data-theme');
                localStorage.setItem('theme', 'light');
            }
        });
    }
</script>
