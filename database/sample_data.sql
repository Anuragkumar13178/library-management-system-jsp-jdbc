USE online_library;
-- All six demo accounts use the documented password Library@123.
-- Each hash is PBKDF2-HMAC-SHA256 with 210,000 iterations and its own 16-byte salt.
INSERT INTO users(membership_id,name,email,password_hash,role,status) VALUES
('LIB-001','Asha Rao','librarian@library.test','pbkdf2$210000$ZORZaWHdjM5vF1Ccy/j57A==$DuCVYSBgflQx5oCIbwcjZSN0RXTvw0yA73FWHtItQ2A=','LIBRARIAN','ACTIVE'),
('MEM-1001','Meera Shah','meera@library.test','pbkdf2$210000$2pyCdQbp9nR6OIVuZ5RSRw==$iUwUp1iPDgvpGw4S3j3WLO+6IKF9QCFmPTAlTQms6d0=','MEMBER','ACTIVE'),
('MEM-1002','Kabir Sen','kabir@library.test','pbkdf2$210000$VkVrYzLRBDfa4DEC5UZOUw==$5JRkgN95nmx30AtwUtccOYhMFyXPslPkxS3pxZHR4xw=','MEMBER','ACTIVE'),
('MEM-1003','Nisha Iyer','nisha@library.test','pbkdf2$210000$QFrYjtJ1rVwez/HP9P/izA==$4dgmS479jOf5VlMPxUaNaY36vW8IsbVrnjBBIWvPjMk=','MEMBER','ACTIVE'),
('MEM-1004','Arjun Das','arjun@library.test','pbkdf2$210000$+Xlnxdl086N5F3GAg65bwA==$6g++928yTuNTWWPaECPqQQw0pLKp0L4ARu454qIlNqU=','MEMBER','ACTIVE'),
('MEM-1005','Tara Nair','tara@library.test','pbkdf2$210000$lal2yOVLZNTPcgJimOKezQ==$AEJksNVzGfX7NVK+MBY63EGgM+YqLsRLy3GOSFEqjww=','MEMBER','ACTIVE');
INSERT INTO notification_preferences(member_id) SELECT id FROM users WHERE role='MEMBER';
INSERT INTO books(title,author,isbn,genre,publisher,publication_year,total_quantity,available_quantity) VALUES
('Effective Java','Joshua Bloch','9780134685991','Programming','Addison-Wesley',2018,4,3),
('Clean Code','Robert C. Martin','9780132350884','Programming','Prentice Hall',2008,3,2),
('The Pragmatic Programmer','David Thomas','9780135957059','Programming','Addison-Wesley',2019,3,3),
('Introduction to Algorithms','Cormen et al.','9780262046305','Computer Science','MIT Press',2022,2,2),
('Head First Java','Kathy Sierra','9781491910771','Programming','O’Reilly Media',2022,4,4),
('Design Patterns','Gamma et al.','9780201633610','Computer Science','Addison-Wesley',1994,2,2),
('Database System Concepts','Silberschatz et al.','9780078022159','Databases','McGraw-Hill',2019,3,3),
('Computer Networks','Andrew S. Tanenbaum','9780132126953','Networking','Pearson',2010,2,2),
('Operating System Concepts','Silberschatz et al.','9781119800361','Operating Systems','Wiley',2021,3,3),
('The Hobbit','J. R. R. Tolkien','9780547928227','Fantasy','Mariner Books',2012,5,5),
('Pride and Prejudice','Jane Austen','9780141439518','Classic','Penguin',2002,3,3),
('The Alchemist','Paulo Coelho','9780062315007','Fiction','HarperOne',2014,4,4),
('Wings of Fire','A. P. J. Abdul Kalam','9788173711466','Biography','Universities Press',1999,4,4),
('The Discovery of India','Jawaharlal Nehru','9780195623598','History','Oxford University Press',1989,2,2),
('Atomic Habits','James Clear','9780735211292','Self-help','Avery',2018,3,3);
INSERT INTO transactions(book_id,member_id,borrow_date,due_date,return_date,status)
SELECT 1,id,CURDATE()-INTERVAL 4 DAY,CURDATE()+INTERVAL 10 DAY,NULL,'BORROWED' FROM users WHERE membership_id='MEM-1001';
INSERT INTO transactions(book_id,member_id,borrow_date,due_date,return_date,status)
SELECT 2,id,CURDATE()-INTERVAL 30 DAY,CURDATE()-INTERVAL 16 DAY,NULL,'OVERDUE' FROM users WHERE membership_id='MEM-1002';
INSERT INTO transactions(book_id,member_id,borrow_date,due_date,return_date,status)
SELECT 3,id,CURDATE()-INTERVAL 40 DAY,CURDATE()-INTERVAL 26 DAY,CURDATE()-INTERVAL 28 DAY,'RETURNED' FROM users WHERE membership_id='MEM-1003';
INSERT INTO notifications(member_id,title,message,type,is_read)
SELECT id,'Welcome to the library','Your account is ready. Browse the catalog and review your notification preferences.','ANNOUNCEMENT',FALSE FROM users WHERE role='MEMBER';
INSERT INTO notifications(member_id,title,message,type,is_read)
SELECT id,'Overdue book','Please return Clean Code at your earliest convenience.','OVERDUE',FALSE FROM users WHERE membership_id='MEM-1002';
INSERT INTO notifications(member_id,title,message,type,is_read)
SELECT id,'New arrival: Atomic Habits','Atomic Habits is now available in the library catalog.','NEW_ARRIVAL',TRUE FROM users WHERE membership_id='MEM-1001';
INSERT INTO search_history(member_id,search_text,title_filter,author_filter,genre_filter,searched_at)
SELECT id,'Java','Java','','Programming',NOW()-INTERVAL 2 DAY FROM users WHERE membership_id='MEM-1001';
INSERT INTO search_history(member_id,search_text,title_filter,author_filter,genre_filter,searched_at)
SELECT id,'Tolkien','','Tolkien','',NOW()-INTERVAL 1 DAY FROM users WHERE membership_id='MEM-1002';
