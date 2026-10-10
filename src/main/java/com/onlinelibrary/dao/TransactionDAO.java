package com.onlinelibrary.dao;
import com.onlinelibrary.model.LibraryTransaction; import java.sql.SQLException; import java.util.List;
public interface TransactionDAO { long borrow(long memberId,long bookId,int limit,int loanDays) throws SQLException; void returnBook(long memberId,long transactionId) throws SQLException; List<LibraryTransaction> history(long memberId) throws SQLException; List<LibraryTransaction> findAll(String status) throws SQLException; }
