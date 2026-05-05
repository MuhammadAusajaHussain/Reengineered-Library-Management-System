INSERT INTO app_user (username, password_hash, full_name, role, active)
VALUES
('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'System Admin', 'ADMIN', true),
('librarian1', '2c445e1c04df4e247c2089245b68fc811f728f7d30ff14a6d64a4faac58e6270', 'Main Librarian', 'LIBRARIAN', true),
('clerk1', 'c40dc72b0228e5850d8b173ff861a48acfb4a15b37b2849cbb6584bbadbc7907', 'Checkout Clerk', 'CLERK', true),
('borrower1', 'ab3f5f62466e0dae7031e1f837acd3b1bc99dcc9996e9854614c1e89eca875c8', 'First Borrower', 'BORROWER', true);

INSERT INTO borrower_profile (user_id, address, phone_no)
VALUES (4, 'FAST Library Town', '0300-0000000');

-- Book insertion moved to programmatic bootstrap in LmsApiApplication.java to prevent duplication on restart
-- INSERT INTO book (isbn, title, author, subject, total_copies, available_copies)
-- VALUES
-- ('9780134685991', 'Effective Java', 'Joshua Bloch', 'Programming', 3, 3),
-- ('9780132350884', 'Clean Code', 'Robert C. Martin', 'Programming', 2, 2),
-- ('9781492056270', 'Designing Data-Intensive Applications', 'Martin Kleppmann', 'Systems', 1, 1);
