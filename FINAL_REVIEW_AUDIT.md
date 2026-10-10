# Final review audit

## Current implementation status

The project contains a connected JSP → Servlet → Service → DAO → JDBC → MySQL application for both roles. Catalog search, search history, borrowing, member-owned return, profile updates, password hashing, notification preferences/read state, librarian catalog/member/transaction/announcement screens, SQL reports, and a scheduled database notification worker are implemented in source. The schema and sample data contain the six required core tables, six demo accounts, fifteen books, loan history including an overdue item, notifications, preferences, and search events.

Build and runtime status is **not verified in this workspace**: the available environment reports Java 8, has no `javac`, and has no Maven. The project targets Java 17/Tomcat 10.1. Do not claim successful compilation, passing tests, or a full rubric score until the commands below pass in the intended environment.

## Evidence checklist

| Check | Status and evidence |
|---|---|
| Raw JDBC / `DriverManager` | Present in `src/main/java/com/onlinelibrary/util/DBConnection.java`. |
| `PreparedStatement` and `ResultSet` | Present throughout `src/main/java/com/onlinelibrary/dao/impl/`. User filters are bound parameters. |
| JDBC commit and rollback | Present for borrow and return in `TransactionDAOImpl.java`. |
| Jakarta `HttpServlet`, `doGet`, and `doPost` | Present under `controller/`. |
| JSP/JSTL pages | Present under `src/main/webapp/WEB-INF/views/`; login is at `src/main/webapp/login.jsp`. |
| Session authentication / authorization | `AuthenticationFilter.java`, `AuthorizationFilter.java`, and `LoginServlet.java`. |
| DAO interfaces | Seven interfaces under `dao/`, with JDBC implementations under `dao/impl/`. |
| Custom exceptions | Domain failures are thrown/mapped in `LibraryServiceImpl.java`; access failure in `AccessPolicy.java`; duplicate unique-key failures in the JDBC DAOs. |
| Multithreading and synchronization | `NotificationWorker.java` runs scheduled SQL work; `LibraryServiceImpl.borrow` is synchronized; concurrency integration test races two borrowers. |
| Database concurrency protection | `TransactionDAOImpl` locks the member/book rows and checks stock within a transaction; schema has inventory checks. |
| Notifications in database | `NotificationDAOImpl`, `NotificationWorker`, `NotificationManagementServlet`, and `notifications` table. |
| Search history | `BookSearchServlet`, `SearchHistoryDAOImpl`, and `search_history` table. |
| Inventory reports | `ReportDAOImpl` and `ReportServlet`; report view shows tables and CSS bars. |
| Member return ownership | Return SQL filters by both transaction ID and session member ID. |
| Inventory bounds / duplicate returns | Guarded update predicates, transaction state check, and database `CHECK` constraint. |
| Password protection | PBKDF2-HMAC-SHA256 in `PasswordUtil`; sample SQL stores encoded salted hashes. |
| JUnit coverage | Fifteen JDBC-backed test methods in `src/test/java/com/onlinelibrary/LibraryIntegrationTest.java`; not run here. |
| SQL setup | `database/schema.sql` and `database/sample_data.sql`; not executed here. |

## Potential mark deductions and viva risks

- Compilation, MySQL setup, WAR deployment, and integration tests still need verification under JDK 17, Maven, MySQL 8, and Tomcat 10.1.
- The test suite uses real MySQL and must point to a disposable test schema; never use a valuable data set.
- The notification worker polls and stores notices but does not push to browsers or send email. Duplicate suppression is based on member/type/title/date matching.
- The application does not implement CSRF tokens, rate limiting, email verification, or password reset.
- There is no connection pool, pagination, or report export.
- The demo accounts share one classroom password; change or discard them outside the classroom demo.
- Tomcat must be 10.1 (Servlet 6.0) for the configured Jakarta API; older Tomcat 9 uses `javax.servlet` and is incompatible.

## Remaining verification before presenting

1. Install/use JDK 17 and Maven 3.8+; run `mvn clean test package` from this directory.
2. Create a disposable MySQL 8 database, run `database/schema.sql`, then `database/sample_data.sql`, and confirm all demo logins.
3. Set `OLMS_DB_URL`, `OLMS_DB_USERNAME`, and `OLMS_DB_PASSWORD` for tests against a disposable schema; run the integration suite and confirm the concurrent last-copy test has exactly one winner.
4. Deploy `target/online-library.war` to Tomcat 10.1 and rehearse every action in `DEMO_SCRIPT.md`.
5. Confirm profile updates, unique member/email validation, notification preference behavior, new-arrival creation, overdue worker notices, and book delete restrictions against the running database.
6. Capture the actual application screenshots and add them to the README if time permits.

This audit records source-level evidence only. It makes no claim that every rubric point is verified at runtime or that the project earns 33/33.
