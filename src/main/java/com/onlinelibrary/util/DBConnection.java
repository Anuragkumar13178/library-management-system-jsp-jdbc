package com.onlinelibrary.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Loads connection settings from the classpath; callers own and close each connection. */
public final class DBConnection {
    private static final Properties SETTINGS = load();
    private DBConnection() {}
    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) throw new IllegalStateException("db.properties is missing");
            p.load(in);
            String url=System.getenv("OLMS_DB_URL"),user=System.getenv("OLMS_DB_USERNAME"),password=System.getenv("OLMS_DB_PASSWORD");
            if(url!=null&&!url.isBlank())p.setProperty("db.url",url);
            if(user!=null&&!user.isBlank())p.setProperty("db.username",user);
            if(password!=null&&!password.isBlank())p.setProperty("db.password",password);

            String localUrl=p.getProperty("db.url");
            String localUser=p.getProperty("db.username");
            String localPassword=p.getProperty("db.password");
            if("CHANGE_ME".equals(localPassword)) {
                // Optional developer-only override. db-local.properties is gitignored.
                try (InputStream local=DBConnection.class.getClassLoader().getResourceAsStream("db-local.properties")) {
                    if(local!=null) {
                        Properties overrides=new Properties();
                        overrides.load(local);
                        p.putAll(overrides);
                    }
                } catch(IOException e) { throw new ExceptionInInitializerError(e); }
            }
            if(p.getProperty("db.password", "").isBlank() || "CHANGE_ME".equals(p.getProperty("db.password"))) {
                throw new IllegalStateException("Database password is not configured. Set OLMS_DB_PASSWORD or create db-local.properties.");
            }
            return p;
        } catch (IOException e) { throw new ExceptionInInitializerError(e); }
    }
    static {
        // Tomcat does not always auto-register JDBC drivers that live in WEB-INF/lib,
        // which causes "No suitable driver found". Register the MySQL driver explicitly.
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { throw new ExceptionInInitializerError("MySQL driver (mysql-connector-j) is missing from WEB-INF/lib: " + e); }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(SETTINGS.getProperty("db.url"), SETTINGS.getProperty("db.username"), SETTINGS.getProperty("db.password"));
    }
    public static int borrowLimit() { return Integer.parseInt(SETTINGS.getProperty("app.borrow.limit", "5")); }
    public static int loanDays() { return Integer.parseInt(SETTINGS.getProperty("app.loan.days", "14")); }
}
