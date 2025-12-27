CREATE OR REPLACE FUNCTION calculate_penalty(
    p_borrowing_id UUID,
    p_daily_fee NUMERIC
)
RETURNS VOID AS $$
DECLARE
v_due_date TIMESTAMP; -- Date yerine Timestamp daha güvenli (Veri kaybı olmasın)
    v_return_date TIMESTAMP;
    v_days_late INT;
BEGIN
    -- DÜZELTME: 'return_at' YERİNE 'return_date' YAZILDI
SELECT due_date, return_date
INTO v_due_date, v_return_date
FROM borrowings
WHERE id = p_borrowing_id;

IF v_return_date IS NULL OR v_return_date <= v_due_date THEN
        RETURN;
END IF;

    -- Gün farkını al (Postgres timestamp farkını interval verir, bunu güne çeviriyoruz)
    v_days_late := EXTRACT(DAY FROM (v_return_date - v_due_date));

INSERT INTO penalties (id, borrowing_id, amount, created_at)
VALUES (gen_random_uuid(), p_borrowing_id, v_days_late * p_daily_fee, CURRENT_TIMESTAMP);
END;
$$ LANGUAGE plpgsql;