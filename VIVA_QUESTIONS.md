# Viva questions and answers

## Core Java

1. **What is encapsulation?** Hiding fields and exposing controlled methods. `Book` and `User` keep state private behind getters and setters.
2. **What is abstraction?** Exposing what a component does while hiding implementation. `BookDAO` describes persistence operations without exposing its SQL to callers.
3. **What is an interface?** A contract of operations. `LibraryService` and the DAO interfaces define contracts implemented by concrete classes.
4. **What is inheritance?** A class derives behavior from a parent. Project exceptions inherit common checked behavior from `LibraryException`.
5. **What is polymorphism?** Code calls an interface while the runtime object supplies behavior. The service uses DAO interface references backed by JDBC implementations.
6. **What is a constructor?** It initializes a new object. `Book` and `User` provide empty and parameterized constructors.
7. **What is an exception?** An object that reports an abnormal condition. The service raises domain failures and servlets show an error view.
8. **Checked versus unchecked exceptions?** Checked exceptions must be caught or declared; unchecked exceptions extend `RuntimeException`. JDBC and domain library errors are checked here.
9. **What is a custom exception?** A project-specific error type, such as `BookUnavailableException`, that gives callers a meaningful failure category.
10. **What is a Java collection?** An object for holding groups of values. DAO methods return `List` and `Map` results.
11. **List versus Set versus Map?** A `List` is ordered and allows duplicates; a `Set` enforces uniqueness; a `Map` associates keys with values.
12. **What is a stream?** A pipeline for processing collection values. The member dashboard filters loan history to count active loans.
13. **What is a lambda?** A compact function expression, used as the notification thread factory and in stream filtering.
14. **What is a thread?** An independent path of execution. The notification worker runs scheduled scans on a background thread.
15. **What is a race condition?** A result depends on timing between concurrent operations. Two borrowers could both observe one available copy without locking.
16. **What is a critical section?** Code that reads or changes shared state and must be protected. Borrowing the last copy is a critical section.
17. **What does `synchronized` do?** It allows one thread at a time to enter the synchronized method for a given service object.
18. **What is `ExecutorService`?** A framework for managing worker threads. The notification worker uses a scheduled executor to run repeated checks.
19. **What is `ReentrantLock`?** An explicit lock with lock/unlock operations and extra options; this project instead uses `synchronized` and database row locks.
20. **Why is synchronization alone insufficient for inventory?** It coordinates only threads sharing one JVM object. Multiple Tomcat instances or processes need database transactions and row locks.
21. **Which date/time types are used?** `LocalDate` for borrow, due, and return dates; `LocalDateTime` for notification timestamps.

## JDBC

22. **What is JDBC?** Java's standard API for connecting to relational databases and running SQL.
23. **What does `DriverManager` do?** It opens a JDBC connection using the configured URL and credentials.
24. **What is a `Connection`?** A session with the database used to create statements and control transactions.
25. **Why use `PreparedStatement`?** It binds values separately from SQL, prevents SQL injection, and keeps user values out of SQL syntax.
26. **What is a `ResultSet`?** A cursor over rows returned by a query.
27. **`executeQuery()` versus `executeUpdate()`?** The first returns rows for a query; the second returns an affected-row count for insert, update, or delete.
28. **What is a database transaction?** A group of database changes treated as one unit.
29. **What does `commit()` do?** It makes the current transaction's changes durable.
30. **What does `rollback()` do?** It undoes the current transaction's uncommitted changes after a failure.
31. **How does borrowing use a JDBC transaction?** It locks/checks member and book rows, inserts a loan, reduces available stock, then commits; failures roll back.
32. **How does returning use a transaction?** It verifies an active loan belonging to the session member, updates the loan, restores stock, and commits together.
33. **What is SQL injection?** Attacker-controlled input changes the meaning of a SQL statement. Binding values with prepared statements prevents that class of input from becoming SQL syntax.
34. **How are JDBC resources closed?** DAO methods use try-with-resources for connections, statements, and result sets.
35. **Why use `SELECT ... FOR UPDATE`?** It locks the selected database row until the transaction completes so another borrower waits before checking the same stock.

## Servlets and JSP

36. **What is a servlet?** A Java class managed by a servlet container that handles HTTP requests and responses.
37. **What is the servlet lifecycle?** The container creates the servlet, calls `init`, services requests, then calls `destroy` when removing it.
38. **What does `init()` do?** It performs one-time servlet setup after construction.
39. **What is `service()`?** The container's request dispatch method, which routes to `doGet`, `doPost`, or another HTTP method.
40. **When is `doGet()` used?** For retrieval and navigation, such as catalog search or viewing reports.
41. **When is `doPost()` used?** For state changes such as borrowing, returning, profile updates, and book changes.
42. **What is `HttpServletRequest`?** The servlet API object containing request parameters, headers, path, and session access.
43. **What is `HttpServletResponse`?** The servlet API object used to set response status, headers, redirects, or output.
44. **What is `HttpSession`?** Server-side state associated with a browser session; the app stores user ID, role, and display name there.
45. **What does `RequestDispatcher.forward()` do?** It transfers the same request and attributes to a server-side JSP view.
46. **What does `sendRedirect()` do?** It asks the browser to make a new request to another URL, commonly after a successful POST.
47. **What is a servlet filter?** A component that runs before/after mapped servlet requests. The project uses authentication and role authorization filters.
48. **What is JSP?** A server-side view technology that is translated into a servlet and rendered as HTML.
49. **JSP versus servlet?** Servlets handle request flow and application logic; JSP is used for presentation.
50. **What is EL?** Expression Language reads request/session attributes in JSP without Java scriptlets.
51. **What is JSTL?** Standard JSP tags for loops, conditions, and escaping output.
52. **Why avoid JSP scriptlets?** They mix Java logic with markup and make views harder to maintain. The application's views use EL/JSTL.
53. **What is MVC?** Model-view-controller separates data/business logic, presentation, and request handling.

## Database

54. **What is a primary key?** A column or column set that uniquely identifies each row.
55. **What is a foreign key?** A constraint requiring a reference to a related row, such as a loan's member and book.
56. **What is normalization?** Organizing data to reduce duplication and update anomalies. Member, book, transaction, and preference data live in separate related tables.
57. **What is an index?** A data structure that helps the database find rows faster, at the cost of storage and write overhead.
58. **What is a join?** A query operation that combines related rows, used to show book and member names with transactions.
59. **What does `GROUP BY` do?** It groups rows for aggregate calculations, such as monthly loan counts or genre counts.
60. **Name aggregate functions.** `COUNT`, `SUM`, `AVG`, `MIN`, and `MAX`; the reports use `COUNT` and `SUM`.

## Project-specific

61. **Explain the project architecture.** JSP/JSTL renders; a servlet handles the request; the service enforces the borrowing workflow; a DAO executes prepared JDBC statements; MySQL stores state.
62. **Explain login.** `LoginServlet` looks up the email, checks status and the PBKDF2 hash, then creates a session with user ID, name, and role.
63. **How is book search implemented?** `BookSearchServlet` passes title/author/genre to the service; `BookDAOImpl` binds them in a prepared query; successful search filters are stored in `search_history`.
64. **Explain borrowing.** The service serializes local calls and the DAO starts a database transaction, locks rows, checks account/limit/stock, creates the loan, decrements stock, and commits.
65. **Explain return.** The DAO selects an active loan by both transaction ID and session member ID, sets return date/status, increments stock within one transaction, and commits.
66. **How are librarian transactions monitored?** A joined transaction query presents the member and title; report and status filters show current, returned, and overdue loans.
67. **How do notifications work?** Librarians create stored announcements; new books create preference-aware arrival notices; a scheduled worker creates due/overdue notices in MySQL.
68. **How is search history private?** Each history lookup filters by the member ID taken from the authenticated session.
69. **How are reports calculated?** `ReportDAOImpl` runs aggregate SQL for counts, most borrowed titles, genres, and monthly borrowing/return totals.
70. **What concurrency problem does the test demonstrate?** Two threads compete for a one-copy book; the test expects one successful transaction and one unavailable result.
71. **Why use both synchronization and row locks?** The synchronized service reduces same-JVM contention; database locks and a transaction protect shared state across multiple app processes.
72. **What if the stock update fails after the loan insert?** The transaction rolls back, so the inserted loan cannot remain without its matching inventory change.
73. **How are passwords protected?** `PasswordUtil` uses PBKDF2-HMAC-SHA256 with random salts and a work factor; login recomputes and compares derived bytes.
74. **What are the limits of the demo notification worker?** It polls on a schedule and suppresses duplicates using title/date matching; it does not push messages to an open browser or send email.
75. **What would you improve next?** Add CSRF protection, login rate limiting, pooled connections, stronger notification event keys, pagination, and password reset/email delivery.
