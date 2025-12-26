CREATE OR REPLACE FUNCTION calculate_penalty_trigger_fn()
RETURNS TRIGGER AS $$
DECLARE
v_minutes_late BIGINT;
    v_penalty_amount DECIMAL(10,2);
    v_minute_fee DECIMAL(10,2) := 0.50;
BEGIN
    IF OLD.return_date IS NULL AND NEW.return_date IS NOT NULL THEN
        IF NEW.return_date > NEW.due_date THEN
            -- Dakika farkını al ve yukarı yuvarla
            v_minutes_late := CEIL(EXTRACT(EPOCH FROM (NEW.return_date - NEW.due_date)) / 60);

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

DROP TRIGGER IF EXISTS trg_calculate_penalty ON borrowings;

CREATE TRIGGER trg_calculate_penalty
    AFTER UPDATE ON borrowings
    FOR EACH ROW
    EXECUTE FUNCTION calculate_penalty_trigger_fn();