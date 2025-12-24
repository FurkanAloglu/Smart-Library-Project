export async function fetchBookCover(isbn) {
    if (!isbn) return null;
    
    // ISBN temizliği (tireleri kaldır)
    const cleanIsbn = isbn.replace(/-/g, '');
    
    try {
        const response = await fetch(`https://www.googleapis.com/books/v1/volumes?q=isbn:${cleanIsbn}`);
        const data = await response.json();
        
        if (data.totalItems > 0 && data.items[0].volumeInfo.imageLinks) {
            // Küçük resim yerine daha büyük olanı tercih edelim
            return data.items[0].volumeInfo.imageLinks.thumbnail || 
                   data.items[0].volumeInfo.imageLinks.smallThumbnail;
        }
        return null;
    } catch (error) {
        console.error("Google Kitap Kapağı Bulunamadı:", error);
        return null;
    }
}