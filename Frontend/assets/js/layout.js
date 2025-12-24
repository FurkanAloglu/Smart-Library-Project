import { request } from './api.js';

export async function loadNavbar() {
    let user = null;
    try {
        user = await request('/auth/me');
    } catch (e) {
        console.error("Auth check failed", e);
    }

    if (!user) {
        window.location.href = 'index.html';
        return null;
    }

    // GÜVENLİK KONTROLÜ: İsim yoksa 'Kullanıcı' yaz
    // Backend 'name' dönmüyorsa 'firstName' + 'lastName' deneyebiliriz
    const displayName = user.name || user.firstName || user.email || 'Kullanıcı';
    
    // ROZET MANTIĞI
    const roleBadge = user.role === 'ADMIN' 
        ? `<span class="mt-0.5 text-[10px] font-bold uppercase tracking-widest text-red-600 bg-red-50 px-2 py-0.5 rounded-full border border-red-100">YÖNETİCİ</span>` 
        : '';

    const navHtml = `
    <nav class="bg-white/80 backdrop-blur-md shadow-sm border-b border-gray-200 sticky top-0 z-40">
        <div class="container mx-auto px-6 py-3 flex justify-between items-center">
            <div class="flex items-center gap-3 cursor-pointer group" onclick="window.location.href='dashboard.html'">
                <div class="bg-gradient-to-tr from-blue-600 to-indigo-600 text-white p-2.5 rounded-xl shadow-lg shadow-blue-200">
                    <i class="fa-solid fa-book-open"></i>
                </div>
                <h1 class="text-xl font-bold text-gray-800">Smart<span class="text-blue-600">Library</span></h1>
            </div>
            
            <div class="flex items-center gap-8">
                <div class="hidden md:flex items-center gap-6">
                    <a href="dashboard.html" class="text-sm font-medium text-gray-600 hover:text-blue-600 transition">Kitaplar</a>
                    ${user.role === 'USER' ? '<a href="my-loans.html" class="text-sm font-medium text-gray-600 hover:text-blue-600 transition">Ödünç Aldıklarım</a>' : ''}
                </div>
                
                <div class="h-6 w-px bg-gray-200 hidden md:block"></div>

                <div class="flex items-center gap-4">
                    <div class="flex flex-col items-end">
                        <span class="font-bold text-gray-800 text-sm">${displayName}</span>
                        ${roleBadge}
                    </div>
                    <button id="globalLogoutBtn" class="bg-gray-50 text-gray-500 hover:bg-red-50 hover:text-red-600 p-2.5 rounded-full transition border border-gray-100">
                        <i class="fa-solid fa-right-from-bracket"></i>
                    </button>
                </div>
            </div>
        </div>
    </nav>
    `;

    document.body.insertAdjacentHTML('afterbegin', navHtml);

    const logoutBtn = document.getElementById('globalLogoutBtn');
    if(logoutBtn){
        logoutBtn.addEventListener('click', async () => {
            await request('/auth/logout', 'POST');
            window.location.href = 'index.html';
        });
    }

    return user;
}
// showToast fonksiyonu aynı kalabilir...
export function showToast(message, type = 'success') {
    let container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div'); container.id = 'toastContainer'; container.className = 'fixed top-5 right-5 z-[60] flex flex-col gap-2'; document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    const colors = type === 'success' ? 'bg-emerald-500' : (type === 'error' ? 'bg-rose-500' : 'bg-blue-500');
    const icon = type === 'success' ? 'fa-circle-check' : (type === 'error' ? 'fa-circle-exclamation' : 'fa-circle-info');
    toast.className = `${colors} text-white px-6 py-4 rounded-xl shadow-xl flex items-center gap-3 transform translate-x-full transition-all duration-500 min-w-[320px]`;
    toast.innerHTML = `<i class="fa-solid ${icon}"></i> <span class="font-medium text-sm">${message}</span>`;
    container.appendChild(toast);
    requestAnimationFrame(() => toast.classList.remove('translate-x-full'));
    setTimeout(() => { toast.classList.add('translate-x-full', 'opacity-0'); setTimeout(() => toast.remove(), 300); }, 3000);
}