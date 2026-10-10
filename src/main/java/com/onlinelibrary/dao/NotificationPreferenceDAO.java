package com.onlinelibrary.dao;
import java.sql.SQLException; import java.util.Map;
public interface NotificationPreferenceDAO { Map<String,Boolean> find(long memberId) throws SQLException; void save(long memberId,boolean due,boolean overdue,boolean arrivals) throws SQLException; }
