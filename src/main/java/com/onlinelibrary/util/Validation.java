package com.onlinelibrary.util;
import com.onlinelibrary.exception.LibraryException; import com.onlinelibrary.model.Book; import java.util.regex.Pattern;
public final class Validation { private static final Pattern EMAIL=Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",Pattern.CASE_INSENSITIVE); private Validation(){}
 public static void book(Book b)throws LibraryException{if(b.getTitle()==null||b.getTitle().isBlank()||b.getAuthor()==null||b.getAuthor().isBlank()||b.getIsbn()==null||b.getIsbn().isBlank()||b.getGenre()==null||b.getGenre().isBlank())throw new LibraryException("Title, author, ISBN and genre are required");if(b.getTotalQuantity()<1||b.getAvailableQuantity()<0||b.getAvailableQuantity()>b.getTotalQuantity())throw new LibraryException("Inventory quantities are invalid");}
 public static void member(String name,String email,String membership)throws LibraryException{if(name==null||name.isBlank()||membership==null||membership.isBlank()||email==null||!EMAIL.matcher(email).matches())throw new LibraryException("Name, membership ID and valid email are required");}
}
