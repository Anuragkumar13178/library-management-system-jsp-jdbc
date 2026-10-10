# Online Library Management System

A Java Web Review 1 project for two roles: librarian and member. The application uses JSP and JSTL for presentation, Jakarta Servlets for HTTP handling, service classes for business rules, DAO interfaces for persistence boundaries, and raw JDBC against MySQL 8.

## Problem statement and objectives

Libraries need one place to maintain a searchable catalog, member accounts, loans and returns, notifications, and inventory reports. This project demonstrates those workflows with server-side role checks, transaction-safe inventory updates, salted password hashes, search history, and database-backed notifications.

## User types and implemented features

**Librarian:** manage catalog records; search, create, update, and deactivate members; inspect loans and overdue status; send announcements to one or all members; view summary, genre, borrowing, return, and most-borrowed reports.

**Member:** search by title, author, and genre; borrow available books; return only their own active loans; view loan and search history; update profile/password; view, read, and count notifications; change due, overdue, and new-arrival preferences.

## Technology stack

- Java 17, Maven WAR packaging
- Apache Tomcat 10.1 (Jakarta Servlet 6.0)
- Jakarta Servlets, JSP, JSTL 3
- MySQL 8 and JDBC (`DriverManager`, `Connection`, `PreparedStatement`, `ResultSet`)
- HTML5, CSS3, vanilla JavaScript
- JUnit 5 integration tests

No Spring, ORM, or JavaScript application framework is used.

## Architecture and request flow

```text
Browser → JSP/JSTL view → HttpServlet → LibraryService → DAO interface/implementation → JDBC → MySQL
```

JSP pages contain presentation markup and EL/JSTL only. Servlets validate request data and set request attributes. `AuthenticationFilter` and `AuthorizationFilter` enforce session and role checks server-side. POST actions redirect after success. DAO methods use prepared statements and try-with-resources. Loan issue and return use explicit JDBC transactions, row locks, commit, and rollback.

Main packages are `controller`, `dao`, `dao.impl`, `model`, `service`, `service.impl`, `util`, `exception`, `filter`, and `thread` under `com.onlinelibrary`.

## Database design

`database/schema.sql` creates `online_library` and the normalized `users`, `books`, `transactions`, `notifications`, `notification_preferences`, and `search_history` tables. Foreign keys protect related rows. Unique constraints protect membership IDs, emails, and ISBNs. A MySQL `CHECK` constraint and guarded update statements keep available copies between zero and total quantity. Search history is member-owned. Preference rows are one-to-one with members.

## JDBC and borrowing consistency

`DBConnection` uses `DriverManager`. Each DAO closes connections, statements, and result sets with try-with-resources. The borrowing transaction locks the active member row, checks the member's open-loan count, locks the selected book row with `SELECT ... FOR UPDATE`, inserts the loan, decrements inventory, and commits. Any SQL failure rolls back. Return locks and verifies the member-owned active transaction before setting its return date/status and restoring a copy in the same transaction. `LibraryServiceImpl.borrow` also synchronizes calls within the web application's shared service instance; database row locks remain the protection across application instances.

## Core Java concepts

Models encapsulate state; DAO and service interfaces provide abstraction and polymorphism; custom exceptions represent domain failures; Java collections carry result sets; streams count active loans; `LocalDate` handles issue/due/return dates; and a `ScheduledExecutorService` runs the notification worker. `NotificationWorker` periodically marks past-due loans and creates preference-aware database notifications. It stops when the web application shuts down.

## Notifications, search, and reports

Member searches are stored after a successful catalog query. A new catalog record creates database-backed new-arrival notices for members who enabled them. The background worker creates due-soon and overdue notices according to preferences. Librarians can send general announcements to selected or all active members. Members can mark notifications read and see unread count. Reports use SQL aggregation and joins for summary cards, most-borrowed titles, genre distribution, and monthly borrowing/return trends. Tables and CSS bars provide visual summaries; there is no React dependency.

## Setup

Prerequisites: JDK 17, Maven 3.8+, MySQL 8+, and Tomcat 10.1.

1. Create a MySQL user with rights on `online_library` (or use a local development account).
2. Run `database/schema.sql`, then `database/sample_data.sql` once against a fresh schema.
3. Set `OLMS_DB_URL`, `OLMS_DB_USERNAME`, and `OLMS_DB_PASSWORD` in your environment (recommended). For local-only development, create `src/main/resources/db-local.properties` using the same keys; that file is gitignored. `db.properties` contains no real password. A copy-ready template is provided at `src/main/resources/db-local.properties.example`; copy it to `db-local.properties` and edit the password for local development.
4. Build with `mvn clean test package` from the project root. Integration tests use JDBC and need a disposable schema loaded from `schema.sql`; they create and remove uniquely named test members/books. Set `OLMS_DB_URL`, `OLMS_DB_USERNAME`, and `OLMS_DB_PASSWORD` to point tests at a separate test database.
5. Deploy `target/online-library.war` to Tomcat 10.1's `webapps` directory and start Tomcat.
6. Open `http://localhost:8080/online-library/`.

Use JDK 17 and Maven 3.8+ for compilation. Run `mvn clean test package` after configuring the database; the build must pass before presenting.

For a local MySQL server, the application account can be created after the database schema exists:

```sql
CREATE USER 'library_app'@'localhost' IDENTIFIED BY 'choose-a-local-password';
GRANT SELECT, INSERT, UPDATE, DELETE ON online_library.* TO 'library_app'@'localhost';
```

Run the schema and sample-data scripts using an administrator account; the application account only needs data access.

## Demo accounts

All sample accounts use password `Library@123`:

| Role | Email | Membership ID |
|---|---|---|
| Librarian | `librarian@library.test` | `LIB-001` |
| Member | `meera@library.test` | `MEM-1001` |
| Member | `kabir@library.test` | `MEM-1002` |
| Member | `nisha@library.test` | `MEM-1003` |
| Member | `arjun@library.test` | `MEM-1004` |
| Member | `tara@library.test` | `MEM-1005` |

These are classroom fixtures, not production credentials. Passwords are stored as salted PBKDF2-HMAC-SHA256 hashes, never as plain text.

## Tests

`src/test/java/com/onlinelibrary/LibraryIntegrationTest.java` contains 15 real JDBC-backed JUnit tests for login, invalid login, duplicate ISBN, unavailable and successful borrowing, borrowing limit, successful/duplicate/unauthorized return, role authorization, preference persistence, inventory bounds, concurrent last-copy borrowing, and custom exception translation. The tests require a running MySQL instance and a disposable schema; they were not executed in the available Java 8/no-Maven environment.

## Screenshots

No screenshots are included yet. Capture the deployed login, member dashboard, librarian dashboard, and reports pages during the Review 1 rehearsal.

## Limitations and future scope

- Email delivery, account self-registration, password reset, and rate limiting are not implemented. Authenticated state-changing POST requests are protected with a per-session CSRF token, and login rotates the session ID after authentication.
- Due notifications are generated by a polling background worker, not pushed to the browser; the member sees them on the next page load.
- The notification duplicate suppression is scoped to member, type, matching title text, and calendar date rather than a dedicated notification-event key.
- Reports are basic counts and time-grouped summaries; there is no export or advanced charting library.
- A single JDBC connection is opened per DAO operation; no connection pool is configured.
- Future work could add email delivery, audit logs, fine tracking, exports, pagination, and an externalized secrets manager.

## Review materials

See [REVIEW_1_RUBRIC_MAPPING.md](REVIEW_1_RUBRIC_MAPPING.md), [VIVA_QUESTIONS.md](VIVA_QUESTIONS.md), [DEMO_SCRIPT.md](DEMO_SCRIPT.md), and [FINAL_REVIEW_AUDIT.md](FINAL_REVIEW_AUDIT.md).

Project presentation: [Online Library Project Presentation](docs/Online_Library_Project_Presentation.pptx).
