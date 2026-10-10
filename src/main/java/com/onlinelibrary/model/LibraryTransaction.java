package com.onlinelibrary.model;
import java.time.LocalDate;
public class LibraryTransaction {
 private long id,bookId,memberId; private String bookTitle,memberName,status; private LocalDate borrowDate,dueDate,returnDate;
 public LibraryTransaction() {} public long getId(){return id;} public void setId(long v){id=v;} public long getBookId(){return bookId;} public void setBookId(long v){bookId=v;} public long getMemberId(){return memberId;} public void setMemberId(long v){memberId=v;} public String getBookTitle(){return bookTitle;} public void setBookTitle(String v){bookTitle=v;} public String getMemberName(){return memberName;} public void setMemberName(String v){memberName=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public LocalDate getBorrowDate(){return borrowDate;} public void setBorrowDate(LocalDate v){borrowDate=v;} public LocalDate getDueDate(){return dueDate;} public void setDueDate(LocalDate v){dueDate=v;} public LocalDate getReturnDate(){return returnDate;} public void setReturnDate(LocalDate v){returnDate=v;}
}
