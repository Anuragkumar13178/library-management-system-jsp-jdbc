package com.onlinelibrary.dao.impl;
import com.onlinelibrary.dao.TransactionDAO; import com.onlinelibrary.model.LibraryTransaction; import com.onlinelibrary.util.DBConnection;
import java.sql.*; import java.time.LocalDate; import java.util.*;
public class TransactionDAOImpl implements TransactionDAO {
 public long borrow(long memberId,long bookId,int limit,int loanDays)throws SQLException{
  try(Connection c=DBConnection.getConnection()){c.setAutoCommit(false);try{
   try(PreparedStatement p=c.prepareStatement("SELECT status FROM users WHERE id=? AND role='MEMBER' FOR UPDATE")){p.setLong(1,memberId);try(ResultSet r=p.executeQuery()){if(!r.next()||!"ACTIVE".equals(r.getString(1)))throw new SQLException("Member is inactive or missing");}}
   try(PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM transactions WHERE member_id=? AND status IN ('BORROWED','OVERDUE')")){p.setLong(1,memberId);try(ResultSet r=p.executeQuery()){r.next();if(r.getInt(1)>=limit)throw new SQLException("BORROW_LIMIT");}}
   try(PreparedStatement p=c.prepareStatement("SELECT available_quantity FROM books WHERE id=? FOR UPDATE")){p.setLong(1,bookId);try(ResultSet r=p.executeQuery()){if(!r.next())throw new SQLException("BOOK_MISSING");if(r.getInt(1)<1)throw new SQLException("BOOK_UNAVAILABLE");}}
   long tx;try(PreparedStatement p=c.prepareStatement("INSERT INTO transactions(book_id,member_id,borrow_date,due_date,status) VALUES(?,?,?,?,'BORROWED')",Statement.RETURN_GENERATED_KEYS)){p.setLong(1,bookId);p.setLong(2,memberId);p.setDate(3,java.sql.Date.valueOf(LocalDate.now()));p.setDate(4,java.sql.Date.valueOf(LocalDate.now().plusDays(loanDays)));p.executeUpdate();try(ResultSet r=p.getGeneratedKeys()){r.next();tx=r.getLong(1);}}
   try(PreparedStatement p=c.prepareStatement("UPDATE books SET available_quantity=available_quantity-1 WHERE id=? AND available_quantity>0")){p.setLong(1,bookId);if(p.executeUpdate()!=1)throw new SQLException("BOOK_UNAVAILABLE");}
   c.commit();return tx;
  }catch(SQLException e){c.rollback();throw e;}}
 }
 public void returnBook(long memberId,long transactionId)throws SQLException{try(Connection c=DBConnection.getConnection()){c.setAutoCommit(false);try{long bookId;try(PreparedStatement p=c.prepareStatement("SELECT book_id FROM transactions WHERE id=? AND member_id=? AND status IN ('BORROWED','OVERDUE') FOR UPDATE")){p.setLong(1,transactionId);p.setLong(2,memberId);try(ResultSet r=p.executeQuery()){if(!r.next())throw new SQLException("TRANSACTION_NOT_ACTIVE_OR_NOT_OWNED");bookId=r.getLong(1);}}
  try(PreparedStatement p=c.prepareStatement("UPDATE transactions SET status='RETURNED',return_date=? WHERE id=? AND member_id=? AND status IN ('BORROWED','OVERDUE')")){p.setDate(1,java.sql.Date.valueOf(LocalDate.now()));p.setLong(2,transactionId);p.setLong(3,memberId);if(p.executeUpdate()!=1)throw new SQLException("TRANSACTION_NOT_ACTIVE_OR_NOT_OWNED");}
  try(PreparedStatement p=c.prepareStatement("UPDATE books SET available_quantity=available_quantity+1 WHERE id=? AND available_quantity<total_quantity")){p.setLong(1,bookId);if(p.executeUpdate()!=1)throw new SQLException("INVENTORY_LIMIT");}c.commit();}catch(SQLException e){c.rollback();throw e;}}}
 private LibraryTransaction map(ResultSet r)throws SQLException{LibraryTransaction t=new LibraryTransaction();t.setId(r.getLong("id"));t.setBookId(r.getLong("book_id"));t.setMemberId(r.getLong("member_id"));t.setBookTitle(r.getString("title"));t.setMemberName(r.getString("member_name"));t.setBorrowDate(r.getDate("borrow_date").toLocalDate());t.setDueDate(r.getDate("due_date").toLocalDate());java.sql.Date ret=r.getDate("return_date");t.setReturnDate(ret==null?null:ret.toLocalDate());t.setStatus(r.getString("status"));return t;}
 private static final String SELECT="SELECT t.*,b.title,u.name member_name FROM transactions t JOIN books b ON b.id=t.book_id JOIN users u ON u.id=t.member_id ";
 public List<LibraryTransaction> history(long memberId)throws SQLException{return query(SELECT+"WHERE t.member_id=? ORDER BY t.borrow_date DESC",memberId,null);}
 public List<LibraryTransaction> findAll(String status)throws SQLException{return query(SELECT+"WHERE (? IS NULL OR t.status=?) ORDER BY t.borrow_date DESC",null,status);}
 private List<LibraryTransaction> query(String sql,Long member,String status)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){if(member!=null)p.setLong(1,member);else{p.setString(1,status);p.setString(2,status);}try(ResultSet r=p.executeQuery()){List<LibraryTransaction>x=new ArrayList<>();while(r.next())x.add(map(r));return x;}}}
}

