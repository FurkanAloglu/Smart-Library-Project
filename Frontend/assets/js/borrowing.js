import { request } from './api.js';
import { showToast } from './layout.js';

// Kitap Ödünç Al
export async function borrowBook(bookId) {
    if (!confirm("Bu kitabı ödünç almak istiyor musunuz?")) return false;

    try {
        // Backend DTO: BorrowingRequest { bookId: UUID }
        await request('/borrowings', 'POST', { bookId: bookId });
        showToast("Kitap ödünç alındı! İyi okumalar.", "success");
        return true;
    } catch (error) {
        showToast(error.message || "Ödünç alma başarısız!", "error");
        return false;
    }
}

// Kullanıcının Ödünç Aldıklarını Getir
export async function getMyBorrowings() {
    try {
        // endpoint: /api/borrowings/my
        return await request('/borrowings/my') || [];
    } catch (error) {
        console.error("Geçmiş çekilemedi", error);
        return [];
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
        showToast("İade başarısız!", "error");
        return false;
    }
}