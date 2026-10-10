package com.onlinelibrary.dao;
import java.sql.SQLException; import java.util.List; import java.util.Map;
public interface SearchHistoryDAO { void save(long memberId,String text,String title,String author,String genre) throws SQLException; List<Map<String,Object>> findForMember(long memberId) throws SQLException; }
