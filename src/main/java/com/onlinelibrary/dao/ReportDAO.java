package com.onlinelibrary.dao;
import com.onlinelibrary.model.ReportSummary; import java.sql.SQLException; import java.util.List; import java.util.Map;
public interface ReportDAO { ReportSummary summary() throws SQLException; List<Map<String,Object>> mostBorrowed() throws SQLException; List<Map<String,Object>> genreDistribution() throws SQLException; List<Map<String,Object>> monthlyBorrowing() throws SQLException; List<Map<String,Object>> monthlyReturns() throws SQLException; }
