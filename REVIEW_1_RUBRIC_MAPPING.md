# Review 1 rubric mapping

Evidence below refers to implemented source files in this project. Mark coverage remains an evaluator's decision; this map does not claim a score.

## 1. Problem understanding and solution design — 8 marks

- **Problem and objectives:** `README.md` explains the catalog, member, loan, notification, and reporting problem.
- **Two roles:** `users.role` in `database/schema.sql`; session role set by `LoginServlet`; separated `/member/*` and `/librarian/*` routes.
- **MVC:** JSP views in `src/main/webapp/WEB-INF/views`; servlet controllers in `controller`; service in `service`; DAO persistence in `dao` / `dao.impl`.
- **DAO and modularity:** `BookDAO`, `UserDAO`, `TransactionDAO`, `NotificationDAO`, `SearchHistoryDAO`, `NotificationPreferenceDAO`, and `ReportDAO` each have JDBC implementations.
- **Normalized data:** six tables and foreign keys in `database/schema.sql`; unique IDs and inventory bounds are database-enforced.
- **Workflows:** login, search/history, borrow, return/history, profile, notification preferences/read state, librarian catalog/member/transaction management, announcements, and reports are wired through servlet routes.
- **Design decisions:** prepared statements prevent input-driven SQL construction; transactions and row locks protect stock; JSP holds presentation rather than business logic; password hashing is in `PasswordUtil`.

## 2. Core Java concepts — 10 marks

| Concept | Evidence |
|---|---|
| Encapsulation and constructors | `model/Book.java`, `model/User.java`, `model/LibraryTransaction.java` |
| Abstraction and interfaces | DAO interfaces and `service/LibraryService.java` |
| Inheritance | `exception/LibraryException.java` and specific domain exceptions |
| Polymorphism | `LibraryServiceImpl` depends on DAO interfaces; servlet filters implement `Filter` |
| Collections | DAO implementations return `List`, `Map`, and `Optional` |
| Exception handling | `LibraryServiceImpl`, servlet error forwarding, JDBC rollback paths |
| Custom exceptions | `BookUnavailableException`, `BorrowLimitExceededException`, `InvalidLoginException`, `UnauthorizedAccessException`, `TransactionNotFoundException`, `BookNotFoundException`; duplicate constraint exceptions are JDBC-specific custom exceptions |
| Multithreading | `thread/NotificationWorker.java` uses `ScheduledExecutorService` |
| Synchronization | `LibraryServiceImpl.borrow` synchronizes access in the shared web service; JDBC row locks cover database concurrency |
| Date and time | `LocalDate` in loan models/DAO and `LocalDateTime` for notifications |
| Streams/lambdas | `MemberDashboardServlet` filters loan history; notification worker supplies a thread-factory lambda |

## 3. Database integration (JDBC) — 8 marks

- `util/DBConnection.java`: `DriverManager` and `Connection` creation.
- `dao/impl/BookDAOImpl.java`, `UserDAOImpl.java`, `TransactionDAOImpl.java`, and other DAO implementations: `PreparedStatement`, `ResultSet`, CRUD, search, joins, and try-with-resources.
- `TransactionDAOImpl.borrow` and `returnBook`: explicit `setAutoCommit(false)`, row locks, `commit`, and `rollback`.
- `ReportDAOImpl`: aggregation, `GROUP BY`, joins, ordering, and `ResultSetMetaData` for generic report rows.
- `database/schema.sql`: unique keys, foreign keys, indexes, and inventory `CHECK` constraint.
- User-controlled SQL values are passed as bound parameters; fixed report SQL contains no request values.

## 4. Servlets and web integration — 7 marks

- `controller/LoginServlet.java`, `LogoutServlet.java`, member and librarian servlet classes extend `HttpServlet` and implement relevant `doGet`/`doPost` methods.
- `filter/AuthenticationFilter.java` checks the session; `AuthorizationFilter.java` applies `AccessPolicy` for server-side roles.
- Servlets use `HttpSession`, set request attributes, forward with `RequestDispatcher` via `WebSupport.view`, and redirect after successful POST actions.
- Actual JSP/JSTL views are under `src/main/webapp/WEB-INF/views`; login and index JSPs are under `src/main/webapp`.
- Database access is absent from JSPs. JSP EL/JSTL renders request/session attributes.

## Evidence-backed gaps

- The local build and JUnit suite could not be run because this workspace has Java 8 only and Maven is unavailable. Verify under JDK 17 and Maven before review.
- Security hardening beyond the review scope (CSRF protection, rate limits, password reset, connection pooling) is future work.
- `NotificationWorker` uses title/date-based duplicate suppression, which is less robust than a unique notification-event key.
