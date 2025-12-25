-- Önce eski manuel fonksiyon varsa temizleyelim (Çakışma olmasın)
DROP FUNCTION IF EXISTS calculate_penalty;

-- Yeni Trigger Fonksiyonu (Otomatik Ceza Hesaplayıcı)
CREATE OR REPLACE FUNCTION calculate_penalty_trigger_fn()
RETURNS TRIGGER AS $$
DECLARE
v_days_late INT;
    v_penalty_amount DECIMAL(10,2);
    v_daily_fee DECIMAL(10,2) := 5.0; -- Günlük Ceza Tutarı (Sabit)
BEGIN
    -- Sadece iade işlemi yapıldığında (return_date NULL'dan doluya döndüğünde) çalışır
    IF OLD.return_date IS NULL AND NEW.return_date IS NOT NULL THEN
        
        -- Gecikme kontrolü: İade Tarihi > Son Teslim Tarihi ise
        IF NEW.return_date > NEW.due_date THEN
            -- Gün farkını al
            v_days_late := NEW.return_date - NEW.due_date;
            
            -- Ceza varsa hesapla ve penalties tablosuna yaz
            IF v_days_late > 0 THEN
                v_penalty_amount := v_days_late * v_daily_fee;

INSERT INTO penalties (id, borrowing_id, amount, created_at)
VALUES (gen_random_uuid(), NEW.id, v_penalty_amount, CURRENT_TIMESTAMP);
END IF;
END IF;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger'ı 'borrowings' tablosuna bağla
DROP TRIGGER IF EXISTS trg_calculate_penalty_on_return ON borrowings;

CREATE TRIGGER trg_calculate_penalty_on_return
    AFTER UPDATE ON borrowings
    FOR EACH ROW
    EXECUTE FUNCTION calculate_penalty_trigger_fn();