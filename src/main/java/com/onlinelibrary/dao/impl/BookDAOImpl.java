package com.onlinelibrary.dao.impl;
import com.onlinelibrary.dao.BookDAO; import com.onlinelibrary.model.Book; import com.onlinelibrary.util.DBConnection;
import java.sql.*; import java.util.*;
public class BookDAOImpl implements BookDAO {
    private String n(String value) { return value == null ? "" : value.trim(); }
 private Book map(ResultSet r)throws SQLException{Object year=r.getObject("publication_year");return new Book(r.getLong("id"),r.getString("title"),r.getString("author"),r.getString("isbn"),r.getString("genre"),r.getString("publisher"),year==null?null:((Number)year).intValue(),r.getInt("total_quantity"),r.getInt("available_quantity"));}
 public List<Book> search(String title,String author,String genre)throws SQLException{String sql="SELECT * FROM books WHERE (?='' OR title LIKE ?) AND (?='' OR author LIKE ?) AND (?='' OR genre LIKE ?) ORDER BY title";try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){String t=n(title),a=n(author),g=n(genre);p.setString(1,t);p.setString(2,"%"+t+"%");p.setString(3,a);p.setString(4,"%"+a+"%");p.setString(5,g);p.setString(6,"%"+g+"%");try(ResultSet r=p.executeQuery()){List<Book>x=new ArrayList<>();while(r.next())x.add(map(r));return x;}}}
 public List<Book> findAll()throws SQLException{return search("","","");} public Book findById(long id)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM books WHERE id=?")){p.setLong(1,id);try(ResultSet r=p.executeQuery()){return r.next()?map(r):null;}}}
 public Book create(Book b)throws SQLException{String q="INSERT INTO books(title,author,isbn,genre,publisher,publication_year,total_quantity,available_quantity) VALUES(?,?,?,?,?,?,?,?)";try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(q,Statement.RETURN_GENERATED_KEYS)){bind(p,b);p.executeUpdate();try(ResultSet r=p.getGeneratedKeys()){if(r.next())b.setId(r.getLong(1));}return b;}catch(SQLIntegrityConstraintViolationException e){throw new com.onlinelibrary.exception.DuplicateISBNException("ISBN already exists",e);}}
 public void update(Book b)throws SQLException{
  String select="SELECT total_quantity,available_quantity FROM books WHERE id=? FOR UPDATE";
  String update="UPDATE books SET title=?,author=?,isbn=?,genre=?,publisher=?,publication_year=?,total_quantity=?,available_quantity=? WHERE id=?";
  try(Connection c=DBConnection.getConnection()){
   c.setAutoCommit(false);
   try{
    try(PreparedStatement lock=c.prepareStatement(select)){
     lock.setLong(1,b.getId());
     try(ResultSet r=lock.executeQuery()){
      if(!r.next()) throw new SQLException("Book not found");
      int oldTotal=r.getInt(1), oldAvailable=r.getInt(2);
      int borrowed=oldTotal-oldAvailable;
      if(b.getTotalQuantity()<borrowed) throw new SQLException("Total quantity cannot be lower than the number of borrowed copies ("+borrowed+")");
      if(b.getAvailableQuantity()<borrowed || b.getAvailableQuantity()>b.getTotalQuantity()) throw new SQLException("Available quantity must include all borrowed copies and cannot exceed total quantity");
     }
    }
    try(PreparedStatement p=c.prepareStatement(update)){
     p.setString(1,b.getTitle());p.setString(2,b.getAuthor());p.setString(3,b.getIsbn());p.setString(4,b.getGenre());p.setString(5,b.getPublisher());
     if(b.getPublicationYear()==null)p.setNull(6,Types.SMALLINT);else p.setInt(6,b.getPublicationYear());
     p.setInt(7,b.getTotalQuantity());p.setInt(8,b.getAvailableQuantity());p.setLong(9,b.getId());
     if(p.executeUpdate()==0)throw new SQLException("Book not found");
    }
    c.commit();
   }catch(SQLException e){try{c.rollback();}catch(SQLException ignored){} throw e;}
  }catch(SQLIntegrityConstraintViolationException e){throw new com.onlinelibrary.exception.DuplicateISBNException("ISBN already exists",e);}
 }
 private void bind(PreparedStatement p,Book b)throws SQLException{p.setString(1,b.getTitle());p.setString(2,b.getAuthor());p.setString(3,b.getIsbn());p.setString(4,b.getGenre());p.setString(5,b.getPublisher());if(b.getPublicationYear()==null)p.setNull(6,Types.SMALLINT);else p.setInt(6,b.getPublicationYear());p.setInt(7,b.getTotalQuantity());p.setInt(8,b.getAvailableQuantity());}
 public void delete(long id)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM books WHERE id=? AND NOT EXISTS(SELECT 1 FROM transactions WHERE book_id=?)")){p.setLong(1,id);p.setLong(2,id);if(p.executeUpdate()==0)throw new SQLException("Book is missing or has transaction history");}}
}

