export async function fetchBookDetails(isbn) {
    if (!isbn) return null;
    const cleanIsbn = isbn.replace(/-/g, '');

    try {
        const response = await fetch(`https://www.googleapis.com/books/v1/volumes?q=isbn:${cleanIsbn}`);
        const data = await response.json();

        if (data.totalItems > 0) {
            const info = data.items[0].volumeInfo;
            
        
            return {
                title: info.title,
                description: info.description || "",
                pageCount: info.pageCount || 0,
                authors: info.authors || [], 
                categories: info.categories || [],
                image: info.imageLinks?.medium?.replace('http:', 'https:') || 
                       info.imageLinks?.thumbnail?.replace('http:', 'https:') || 
                       info.imageLinks?.smallThumbnail?.replace('http:', 'https:') || null,
                publishedDate: info.publishedDate
            };
        }
        return null;
    } catch (error) {
        console.error("Google API Hatası:", error);
        return null;
    }
}