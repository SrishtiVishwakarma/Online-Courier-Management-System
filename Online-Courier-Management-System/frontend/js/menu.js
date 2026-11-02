// Dynamic menu rendering based on role
(function() {
    const { getRole, clearAuth } = window.ocmsAPI;

    function renderMenu() {
        const nav = document.getElementById('topnav');
        if (!nav) return;

        const role = getRole();
        let links = [];

        if (!role) {
            // Public menu
            links = [
                { text: 'Home', href: 'index.html' },
                { text: 'Login', href: 'login.html' }
            ];
        } else if (role === 'USER') {
            links = [
                { text: 'User Home', href: 'userhome.html' },
                { text: 'Update Profile', href: 'user-profile.html' },
                { text: 'Send Courier', href: 'send-courier.html' },
                { text: 'Courier Status', href: 'courier-status.html' },
                { text: 'Logout', href: '#', click: handleLogout }
            ];
        } else if (role === 'STAFF') {
            links = [
                { text: 'User Home', href: 'staffhome.html' },
                { text: 'Pickups', href: 'staff-pickups.html' },
                { text: 'Deliveries', href: 'staff-deliveries.html' },
                { text: 'Logs', href: 'staff-logs.html' },
                { text: 'Logout', href: '#', click: handleLogout }
            ];
        } else if (role === 'ADMIN') {
            links = [
                { text: 'Admin Home', href: 'adminhome.html' },
                { text: 'Staff Requests', href: 'admin-staff-requests.html' },
                { text: 'Staff Details', href: 'admin-staff-details.html' },
                { text: 'All Couriers', href: 'admin-couriers.html' },
                { text: 'Total Couriers Status', href: 'admin-total-status.html' },
                { text: 'Track Courier by ID', href: 'admin-track.html' },
                { text: 'Logout', href: '#', click: handleLogout }
            ];
        }

        const navHTML = `
            <div class="container">
                <div class="nav">
                    ${links.map(link => `
                        <a href="${link.href}" ${link.click ? 'data-logout="true"' : ''}>${link.text}</a>
                    `).join('')}
                    <a href="index.html" class="nav-logo-link"><img src="images/logo.png" alt="OCMS Logo" class="nav-logo" /></a>
                </div>
            </div>
        `;

        nav.innerHTML = navHTML;

        // Add logout event listener
        const logoutLink = nav.querySelector('[data-logout="true"]');
        if (logoutLink) {
            logoutLink.addEventListener('click', (e) => {
                e.preventDefault();
                handleLogout();
            });
        }
    }

    function handleLogout() {
        clearAuth();
        window.location.href = 'index.html';
    }

    // Render on load
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', renderMenu);
    } else {
        renderMenu();
    }
})();
