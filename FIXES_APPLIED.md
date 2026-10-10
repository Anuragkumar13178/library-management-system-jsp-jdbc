# Fixes Applied

This version contains the following fixes to the original project:

1. **Removed committed database password**
   - `src/main/resources/db.properties` now contains `CHANGE_ME` only.
   - Recommended configuration uses `OLMS_DB_URL`, `OLMS_DB_USERNAME`, and `OLMS_DB_PASSWORD` environment variables.
   - Optional local override: `src/main/resources/db-local.properties` (gitignored).
   - Added `db-local.properties.example` as a safe template.

2. **Fixed book inventory update logic**
   - Book edits now use the explicitly submitted available quantity.
   - The current book row is locked with `SELECT ... FOR UPDATE`.
   - The number of already-borrowed copies is calculated before the update.
   - Total quantity cannot be reduced below borrowed copies.
   - Available quantity cannot be below borrowed copies or above total quantity.
   - The edit is committed/rolled back as one JDBC transaction.

3. **Added CSRF protection**
   - Authenticated POST requests under `/member/*`, `/librarian/*`, and `/logout` require a per-session CSRF token.
   - The shared JSP header injects the token into POST forms.
   - Token comparison uses constant-time comparison.

4. **Added session fixation protection**
   - Successful login rotates the existing HTTP session ID with `changeSessionId()` before storing authenticated user data.

5. **Removed stale build artifacts**
   - The old `target/` directory and WAR were removed from the submission so compiled artifacts cannot contain outdated configuration/secrets.

6. **Added regression tests**
   - Added integration tests covering valid book updates and rejecting a total quantity below currently borrowed copies.

## Verification

The source was statically inspected after the changes. A complete `mvn clean test package` and live MySQL integration run still needs to be performed on a machine with JDK 17, Maven 3.8+, MySQL 8+, and the configured test database.
