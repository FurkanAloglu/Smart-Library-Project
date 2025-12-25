const BASE_URL = "http://localhost:8080/api";

/**
 * Backend'e istek atmak için genel fonksiyon
 * @param {string} endpoint - /auth/login gibi
 * @param {string} method - GET, POST, PUT, DELETE
 * @param {object} body - Gönderilecek veri (opsiyonel)
 */
async function request(endpoint, method = "GET", body = null) {
    const options = {
        method: method,
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(`${BASE_URL}${endpoint}`, options);

        if (response.status === 401 && !endpoint.includes("/login")) {
            window.location.href = "/index.html";
            return null;
        }

        const isJson = response.headers.get("content-type")?.includes("application/json");
        const data = isJson ? await response.json() : null;

        if (!response.ok) {
            const error = new Error(data?.message || "Bir hata oluştu");
            error.details = data?.details;
            throw error;
        }

        return data;
    } catch (error) {
        console.error("API Hatası:", error);
        throw error;
    }
}

export { request };