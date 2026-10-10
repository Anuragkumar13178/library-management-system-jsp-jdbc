package com.onlinelibrary.dao;
import com.onlinelibrary.model.Notification; import java.sql.SQLException; import java.util.List;
public interface NotificationDAO { List<Notification> findForMember(long memberId) throws SQLException; long unreadCount(long memberId) throws SQLException; void markRead(long memberId,long id) throws SQLException; void create(long memberId,String title,String message,String type) throws SQLException; void announce(Long memberId,String title,String message) throws SQLException; void newArrival(String title) throws SQLException; }
