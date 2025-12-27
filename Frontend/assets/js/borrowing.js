import { request } from './api.js';
import { showToast } from './layout.js';

// Kullanıcının Ödünç Aldıklarını Getir
export async function getMyBorrowings() {
    try {
        const data = await request('/borrowings/my');
        return data || []; 
    } catch (error) {
        console.error("❌ borrowing.js içinde hata yakalandı:", error);
        throw error; 
    }
}

// Kitap Ödünç Al
export async function borrowBook(bookId) {
    if (!confirm("Bu kitabı ödünç almak istiyor musunuz?")) return false;
    try {
        await request('/borrowings', 'POST', { bookId: bookId });
        showToast("Kitap ödünç alındı! İyi okumalar.", "success");
        return true;
    } catch (error) {
        showToast(error.message || "Ödünç alma başarısız!", "error");
        // Burada throw yapmıyoruz çünkü false dönerek işlemi iptal ettiğimizi belirtiyoruz.
        return false; 
    }
}

// Kitap İade Et
export async function returnBook(borrowingId) {
    if (!confirm("Kitabı iade ediyor musunuz?")) return false;
    try {
        await request(`/borrowings/${borrowingId}/return`, 'PUT');
        showToast("Kitap iade edildi. Teşekkürler!", "success");
        return true;
    } catch (error) {
        console.error("İade hatası:", error);
        showToast(error.message || "İade başarısız!", "error");
        return false;
    }
}

// Cezaları Getir
export async function getMyPenalties() {
    try {
        return await request('/penalties/my') || [];
    } catch (error) {
        console.error("Cezalar çekilemedi:", error);
        // Cezalar kritik değil, hata olsa bile boş dizi dönebiliriz (Sayfa patlamasın diye)
        return []; 
    }
}