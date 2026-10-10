package com.onlinelibrary.service;
import com.onlinelibrary.model.*; import java.util.*;
public interface LibraryService {
 User authenticate(String email,String password)throws Exception; List<Book> searchBooks(long memberId,String text,String title,String author,String genre)throws Exception;
 long borrow(long memberId,long bookId)throws Exception; void returnBook(long memberId,long transactionId)throws Exception;
 List<LibraryTransaction> history(long memberId)throws Exception; List<Book> books()throws Exception; ReportSummary report()throws Exception;
}
