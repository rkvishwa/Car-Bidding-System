-- Migration script for Car Bidding System enhancements

-- 1. Add forgot_password_attempts column to users table
ALTER TABLE users ADD COLUMN forgot_password_attempts INT DEFAULT 0;

-- 2. Add auction columns for admin workflow
ALTER TABLE auctions ADD COLUMN end_time DATETIME NULL;
ALTER TABLE auctions ADD COLUMN duration_minutes INT DEFAULT 5;
ALTER TABLE auctions ADD COLUMN winner_confirmed TINYINT(1) DEFAULT 0;

-- 3. Update existing users to have 'ACTIVE' status if they don't already
-- (Assuming status column already exists based on previous code view)
UPDATE users SET status = 'ACTIVE' WHERE status IS NULL;
