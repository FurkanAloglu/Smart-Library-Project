import { request } from './api.js';

// 1. Kitapları Getir
export async function getAllBooks() {
    try {
        const books = await request('/books'); 
        return books || [];
    } catch (error) {
        console.error("Kitaplar çekilemedi:", error);
        return [];
    }
}

// 2. Kitap Sil
export async function deleteBook(id) {
    if (!confirm("Bu kitabı silmek istediğine emin misin Reis?")) return false;
    try {
        await request(`/books/${id}`, 'DELETE');
        return true;
    } catch (error) {
        alert("Hata: " + error.message);
        return false;
    }
}

// 3. Yeni Kitap Ekle (Backend muhtemelen authorIds ve categoryIds bekliyor)
export async function createBook(bookData) {
    try {
        await request('/books', 'POST', bookData);
        return true;
    } catch (error) {
        alert("Kitap eklenemedi: " + error.message);
        return false;
    }
}

// 4. Yazarları ve Kategorileri Getir (Dropdown için)
export async function getAuthors() {
    try {
        return await request('/authors') || [];
    } catch (e) { return []; }
}

export async function getCategories() {
    try {
        return await request('/categories') || [];
    } catch (e) { return []; }
}