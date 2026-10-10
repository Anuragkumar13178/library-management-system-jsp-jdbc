package com.onlinelibrary.dao;
import com.onlinelibrary.model.Book; import java.sql.SQLException; import java.util.List;
public interface BookDAO { List<Book> search(String title,String author,String genre) throws SQLException; List<Book> findAll() throws SQLException; Book findById(long id) throws SQLException; Book create(Book book) throws SQLException; void update(Book book) throws SQLException; void delete(long id) throws SQLException; }
