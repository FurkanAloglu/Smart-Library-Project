import { request } from './api.js';

/**
 * Navbar'ı yükler
 * Auth yoksa login'e atar
 */
export async function loadNavbar() {
    let user = null;

    try {
        user = await request('/auth/me');
    } catch (e) {
        console.error('Auth check failed', e);
    }

    if (!user) {
        window.location.href = 'index.html';
        return null;
    }

    // GÖRÜNEN İSİM
    const displayName =
        user.name ||
        user.firstName ||
        user.email ||
        'Kullanıcı';

    // ROL ROZETİ
    const roleBadge = user.role === 'ADMIN'
        ? `
        <span class="mt-0.5 text-[10px] font-bold uppercase tracking-widest
                     text-red-600 bg-red-50 px-2 py-0.5 rounded-full
                     border border-red-100">
            YÖNETİCİ
        </span>`
        : '';

    // USER MENÜLERİ
    const userLinks = user.role === 'USER'
        ? `
        <a href="my-loans.html"
           class="text-sm font-medium text-gray-600 hover:text-blue-600 transition">
            Ödünç Aldıklarım
        </a>
        <a href="my-penalties.html"
           class="text-sm font-medium text-gray-600 hover:text-rose-600 transition flex items-center gap-1">
            <i class="fa-solid fa-receipt text-xs"></i> Cezalarım
        </a>`
        : '';

    // ADMIN MENÜLERİ (SON HAL)
    const adminLinks = user.role === 'ADMIN'
        ? `
        <a href="admin-users.html"
           class="text-sm font-medium text-gray-600 hover:text-blue-600 transition">
            Üyeler
        </a>
        <a href="admin-loans.html"
           class="text-sm font-medium text-gray-600 hover:text-blue-600 transition">
            Ödünçler
        </a>
        <a href="admin-penalties.html"
           class="text-sm font-medium text-gray-600 hover:text-red-600 transition">
            Cezalar
        </a>`
        : '';

    const navHtml = `
    <nav class="bg-white/80 backdrop-blur-md shadow-sm border-b border-gray-200 sticky top-0 z-40">
        <div class="container mx-auto px-6 py-3 flex justify-between items-center">

            <!-- LOGO -->
            <div class="flex items-center gap-3 cursor-pointer group select-none"
                 onclick="window.location.href='dashboard.html'">
                <div class="bg-blue-600 w-10 h-10 rounded-xl flex items-center justify-center
                            text-white shadow-lg shadow-blue-200 transition-transform
                            duration-300 group-hover:scale-110 group-hover:rotate-3">
                    <i class="fa-solid fa-university text-lg"></i>
                </div>

                <div class="flex flex-col justify-center">
                    <h1 class="text-xl font-black tracking-tighter uppercase leading-none
                               text-slate-800 group-hover:text-blue-700 transition-colors">
                        AKILLI <span class="text-blue-600">KÜTÜPHANE</span>
                    </h1>
                    <p class="text-[10px] font-bold tracking-[0.2em] uppercase
                              text-slate-400 mt-0.5 group-hover:text-blue-400 transition-colors">
                        YÖNETİM SİSTEMİ
                    </p>
                </div>
            </div>

            <!-- MENÜ -->
            <div class="flex items-center gap-8">
                <div class="hidden md:flex items-center gap-6">
                    <a href="dashboard.html"
                       class="text-sm font-medium text-gray-600 hover:text-blue-600 transition">
                        Kitaplar
                    </a>
                    ${userLinks}
                    ${adminLinks}
                </div>

                <div class="h-6 w-px bg-gray-200 hidden md:block"></div>

                <!-- KULLANICI -->
                <div class="flex items-center gap-4">
                    <div class="flex flex-col items-end">
                        <span class="font-bold text-gray-800 text-sm">
                            ${displayName}
                        </span>
                        ${roleBadge}
                    </div>

                    <button id="globalLogoutBtn"
                            class="bg-gray-50 text-gray-500 hover:bg-red-50
                                   hover:text-red-600 p-2.5 rounded-full transition
                                   border border-gray-100 hover:border-red-200">
                        <i class="fa-solid fa-right-from-bracket"></i>
                    </button>
                </div>
            </div>
        </div>
    </nav>
    `;

    document.body.insertAdjacentHTML('afterbegin', navHtml);

    // LOGOUT
    const logoutBtn = document.getElementById('globalLogoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async () => {
            await request('/auth/logout', 'POST');
            window.location.href = 'index.html';
        });
    }

    return user;
}

/**
 * Toast helper
 */
export function showToast(message, type = 'success') {
    let container = document.getElementById('toastContainer');

    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'fixed top-5 right-5 z-[60] flex flex-col gap-2';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');

    const colors = {
        success: 'bg-emerald-500',
        error: 'bg-rose-500',
        info: 'bg-blue-500'
    };

    const icons = {
        success: 'fa-circle-check',
        error: 'fa-circle-exclamation',
        info: 'fa-circle-info'
    };

    toast.className = `
        ${colors[type] || colors.success}
        text-white px-6 py-4 rounded-xl shadow-xl
        flex items-center gap-3 transform translate-x-full
        transition-all duration-500 min-w-[320px]
    `;

    toast.innerHTML = `
        <i class="fa-solid ${icons[type] || icons.success}"></i>
        <span class="font-medium text-sm">${message}</span>
    `;

    container.appendChild(toast);

    requestAnimationFrame(() => {
        toast.classList.remove('translate-x-full');
    });

    setTimeout(() => {
        toast.classList.add('translate-x-full', 'opacity-0');
        setTimeout(() => toast.remove(), 300);
    }, 3000);

}
