-- Önce eski fonksiyonu temizle
DROP FUNCTION IF EXISTS calculate_penalty;

-- Yeni Trigger Fonksiyonu (Dakika Bazlı Ceza Hesaplayıcı)
CREATE OR REPLACE FUNCTION calculate_penalty_trigger_fn()
RETURNS TRIGGER AS $$
DECLARE
    -- v_days_late INT; -- ESKİ: Gün bazlı değişken
v_minutes_late BIGINT; -- YENİ: Dakika bazlı değişken (Integer taşmasın diye BIGINT)

    v_penalty_amount DECIMAL(10,2);

    -- v_daily_fee DECIMAL(10,2) := 5.0; -- ESKİ: Günlük Ceza
    v_minute_fee DECIMAL(10,2) := 0.50; -- YENİ: Dakika Başına 50 Kuruş (Test için)
BEGIN
    -- Sadece iade işlemi yapıldığında (return_date NULL'dan doluya döndüğünde) çalışır
    IF OLD.return_date IS NULL AND NEW.return_date IS NOT NULL THEN

        -- Gecikme kontrolü: İade Tarihi > Son Teslim Tarihi ise
        IF NEW.return_date > NEW.due_date THEN

            -- YÖNTEM: İki tarih farkını saniye cinsinden al (EPOCH), 60'a böl = Dakika
            v_minutes_late := EXTRACT(EPOCH FROM (NEW.return_date - NEW.due_date)) / 60;

            -- Ceza varsa hesapla (En az 1 dakika gecikme varsa)
            IF v_minutes_late > 0 THEN
                v_penalty_amount := v_minutes_late * v_minute_fee;

INSERT INTO penalties (id, borrowing_id, amount, created_at)
VALUES (gen_random_uuid(), NEW.id, v_penalty_amount, CURRENT_TIMESTAMP);
END IF;
END IF;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger'ı yeniden oluştur
DROP TRIGGER IF EXISTS trg_calculate_penalty_on_return ON borrowings;

CREATE TRIGGER trg_calculate_penalty_on_return
    AFTER UPDATE ON borrowings
    FOR EACH ROW
    EXECUTE FUNCTION calculate_penalty_trigger_fn();