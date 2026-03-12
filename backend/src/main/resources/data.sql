-- Insert students with balances reflecting their payment history
-- and set next_due_date to the most recent transaction's next due date
-- STU001: payment on 2026-02-15 (Sunday) → 90 days later = 2026-05-16 (Saturday) → adjusted to Monday 2026-05-18
-- STU002: payment on 2026-03-01 (Sunday) → 90 days later = 2026-05-30 (Saturday) → adjusted to Monday 2026-06-01

INSERT INTO STUDENT_ACCOUNT (student_number, balance, next_due_date, password) VALUES 
('STU001', 275000, '2026-05-18', '$2a$10$upKRlhkyljPEKj1i4Bk1wOqZK2gJHCjP3XNjEFLoT9c872e4RkEsO'),
('STU002', 294000, '2026-06-01', '$2a$10$IrBJiByt4qgb3565vEckS.kN0z3KZIMaL4akDaSlD6KbyRIdKUaUi');

-- Insert sample payment transactions (with corrected next_due_date)
INSERT INTO PAYMENT_TRANSACTION (student_number, payment_amount, incentive_amount, incentive_rate, payment_date, next_due_date)
VALUES ('STU001', 500000, 25000, 0.05, '2026-02-15', '2026-05-18');
INSERT INTO PAYMENT_TRANSACTION (student_number, payment_amount, incentive_amount, incentive_rate, payment_date, next_due_date)
VALUES ('STU002', 200000, 6000, 0.03, '2026-03-01', '2026-06-01');