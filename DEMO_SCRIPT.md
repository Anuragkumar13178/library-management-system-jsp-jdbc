# Review 1 demo script (about 8–9 minutes)

Before the demo, run MySQL and Tomcat 10.1, load the schema and sample data, and verify the demo accounts. Use the member credentials in `README.md`.

| Time | Action | What to explain |
|---|---|---|
| 0:00–0:35 | Open `/online-library/` and log in as `meera@library.test`. | Authentication uses a session; the password is checked against a salted PBKDF2 hash. |
| 0:35–1:10 | Show the member dashboard and its current loans, unread notifications, and saved searches. | Member IDs come from the authenticated session, not form input. |
| 1:10–1:40 | Search for “Java” or choose a title/author/genre filter. | Search uses bound parameters and stores filter values in `search_history`. |
| 1:40–2:00 | Open search history. | Search records are scoped to the signed-in member. |
| 2:00–2:35 | Borrow an available title and show success. | The transaction inserts a loan and decrements inventory inside one JDBC transaction. |
| 2:35–3:00 | Open the borrowing history and point out due date/status. | `LocalDate` calculates the due date; the return action is a member route. |
| 3:00–3:25 | Return the loan, then revisit the catalog/history. | A member may return only their own active transaction; return and stock restoration commit together. |
| 3:25–3:55 | Open notifications and preferences; mark a notification read and toggle a preference. | Notifications are persisted in MySQL; the worker polls due/overdue records. |
| 3:55–4:10 | Sign out and log in as `librarian@library.test`. | Logout invalidates the session; authorization is checked by filters. |
| 4:10–5:00 | Add a temporary book, edit a field, then delete it. | ISBN uniqueness and inventory checks are enforced in the database and validation layer. Delete is refused when history references the book. |
| 5:00–5:40 | Search members, edit a member record, and deactivate a demo member only if appropriate for the rehearsal. | Membership IDs and emails are unique; deactivation preserves borrowing history. |
| 5:40–6:15 | Review transactions, including the overdue sample loan. | Loan records retain borrow, due, return, and status history. |
| 6:15–6:45 | Send an announcement to all members or one test member. | The librarian operation writes member-specific database notification rows. |
| 6:45–7:15 | Open reports and explain summary cards, genre distribution, most-borrowed list, and monthly trends. | Counts are SQL aggregation queries and the JSP renders table/CSS visuals. |
| 7:15–8:30 | Show source files: `TransactionDAOImpl`, `LibraryServiceImpl`, `BookSearchServlet`, `AuthorizationFilter`, a JSP, `NotificationWorker`, and the concurrency integration test. | Explain the synchronized service entry and why database row locks/transactions are still needed across application instances. |

For a reliable rehearsal, use a temporary database. Do not delete the sample books or deactivate the primary demo accounts before the formal review.
