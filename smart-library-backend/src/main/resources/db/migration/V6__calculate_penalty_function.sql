CREATE OR REPLACE FUNCTION project1.calculate_penalty(
    p_borrowing_id UUID,
    p_daily_fee NUMERIC
)
RETURNS VOID AS $$
DECLARE
v_due_date DATE;
    v_returned_date DATE;
    v_days_late INT;
BEGIN
SELECT due_date, returned_at
INTO v_due_date, v_returned_date
FROM project1.borrowings
WHERE id = p_borrowing_id;

IF v_returned_date IS NULL OR v_returned_date <= v_due_date THEN
        RETURN;
END IF;

    v_days_late := v_returned_date - v_due_date;

INSERT INTO project1.penalties (id, borrowing_id, amount)
VALUES (gen_random_uuid(), p_borrowing_id, v_days_late * p_daily_fee);
END;
$$ LANGUAGE plpgsql;
