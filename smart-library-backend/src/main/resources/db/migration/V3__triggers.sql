-- ÖDÜNÇ ALINCA STOCK -1
CREATE OR REPLACE FUNCTION decrease_book_stock()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT stock FROM books WHERE id = NEW.book_id) <= 0 THEN
        RAISE EXCEPTION 'Book out of stock';
END IF;

UPDATE books
SET stock = stock - 1
WHERE id = NEW.book_id;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_decrease_stock
    AFTER INSERT ON borrowings
    FOR EACH ROW
    EXECUTE FUNCTION decrease_book_stock();


-- İADE EDİNCE STOCK +1
CREATE OR REPLACE FUNCTION increase_book_stock()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.return_date IS NULL AND NEW.return_date IS NOT NULL THEN
UPDATE books
SET stock = stock + 1
WHERE id = NEW.book_id;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_increase_stock
    AFTER UPDATE ON borrowings
    FOR EACH ROW
    EXECUTE FUNCTION increase_book_stock();

