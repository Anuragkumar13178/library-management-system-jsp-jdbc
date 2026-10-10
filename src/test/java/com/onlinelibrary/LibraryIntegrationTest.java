package com.onlinelibrary;

import com.onlinelibrary.dao.impl.*;
import com.onlinelibrary.exception.*;
import com.onlinelibrary.model.*;
import com.onlinelibrary.service.impl.LibraryServiceImpl;
import com.onlinelibrary.util.*;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

/** Integration tests use real JDBC and require a disposable database loaded with schema.sql. */
class LibraryIntegrationTest {
 private final UserDAOImpl users=new UserDAOImpl(); private final BookDAOImpl books=new BookDAOImpl();
 private final TransactionDAOImpl txs=new TransactionDAOImpl(); private final LibraryServiceImpl service=new LibraryServiceImpl();
 private User member,other; private Book book; private String suffix;
 @BeforeEach void setUp()throws Exception{suffix=UUID.randomUUID().toString().replace("-","").substring(0,10);member=createMember("a");other=createMember("b");book=createBook(8);}
 private User createMember(String part)throws Exception{User u=new User(0,"T-"+suffix+part,"Test Member","test-"+suffix+part+"@example.test",PasswordUtil.hash("TestPass@123"),"MEMBER","ACTIVE");return users.createMember(u);}
 private Book createBook(int quantity)throws Exception{Book b=new Book(0,"Test book "+suffix,"Test Author","T"+suffix,"Testing","Test Press",2025,quantity,quantity);return books.create(b);}
 @AfterEach void clean()throws Exception{try(Connection c=DBConnection.getConnection()){
  for(String table:List.of("notifications","notification_preferences","search_history","transactions"))try(PreparedStatement p=c.prepareStatement("DELETE FROM "+table+" WHERE member_id IN (?,?)")){p.setLong(1,member.getId());p.setLong(2,other.getId());p.executeUpdate();}
  try(PreparedStatement p=c.prepareStatement("DELETE FROM books WHERE id=?")){p.setLong(1,book.getId());p.executeUpdate();}
  try(PreparedStatement p=c.prepareStatement("DELETE FROM users WHERE id IN (?,?)")){p.setLong(1,member.getId());p.setLong(2,other.getId());p.executeUpdate();}
 }}
 @Test void successfulLoginUsesPasswordHash()throws Exception{assertEquals(member.getId(),service.authenticate(member.getEmail(),"TestPass@123").getId());}
 @Test void invalidLoginIsRejected(){assertThrows(InvalidLoginException.class,()->service.authenticate(member.getEmail(),"wrong"));}
 @Test void duplicateIsbnIsRejected()throws Exception{Book duplicate=new Book(0,"Duplicate","Author",book.getIsbn(),"Testing","Press",2025,1,1);assertThrows(SQLException.class,()->books.create(duplicate));}
 @Test void unavailableBookCannotBeBorrowed()throws Exception{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("UPDATE books SET available_quantity=0 WHERE id=?")){p.setLong(1,book.getId());p.executeUpdate();}assertThrows(BookUnavailableException.class,()->service.borrow(member.getId(),book.getId()));}
 @Test void successfulBorrowDecreasesInventoryAndCreatesHistory()throws Exception{long id=service.borrow(member.getId(),book.getId());assertTrue(id>0);assertEquals(7,books.findById(book.getId()).getAvailableQuantity());assertEquals(1,txs.history(member.getId()).size());}
 @Test void borrowingLimitIsEnforced()throws Exception{for(int i=0;i<DBConnection.borrowLimit();i++)service.borrow(member.getId(),book.getId());assertThrows(BorrowLimitExceededException.class,()->service.borrow(member.getId(),book.getId()));}
 @Test void successfulReturnRestoresInventory()throws Exception{long id=service.borrow(member.getId(),book.getId());service.returnBook(member.getId(),id);assertEquals(8,books.findById(book.getId()).getAvailableQuantity());assertEquals("RETURNED",txs.history(member.getId()).get(0).getStatus());}
 @Test void duplicateReturnIsRejected()throws Exception{long id=service.borrow(member.getId(),book.getId());service.returnBook(member.getId(),id);assertThrows(TransactionNotFoundException.class,()->service.returnBook(member.getId(),id));}
 @Test void anotherMemberCannotReturnTheLoan()throws Exception{long id=service.borrow(member.getId(),book.getId());assertThrows(TransactionNotFoundException.class,()->service.returnBook(other.getId(),id));}
 @Test void unauthorizedRoleIsRejected(){assertThrows(UnauthorizedAccessException.class,()->AccessPolicy.requireRole("MEMBER","LIBRARIAN"));}
 @Test void notificationPreferencesArePersisted()throws Exception{var prefs=new NotificationPreferenceDAOImpl();prefs.save(member.getId(),false,true,false);Map<String,Boolean>saved=prefs.find(member.getId());assertEquals(false,saved.get("dueDate"));assertEquals(true,saved.get("overdue"));assertEquals(false,saved.get("newArrivals"));}
 @Test void bookUpdateAcceptsExplicitAvailableQuantityWithinBorrowedConstraint()throws Exception{
  long id=service.borrow(member.getId(),book.getId());
  Book edited=books.findById(book.getId());
  edited.setTotalQuantity(10);
  edited.setAvailableQuantity(8);
  books.update(edited);
  Book saved=books.findById(book.getId());
  assertEquals(10,saved.getTotalQuantity());
  assertEquals(8,saved.getAvailableQuantity());
 }
 @Test void bookUpdateRejectsTotalBelowBorrowedCopies()throws Exception{
  service.borrow(member.getId(),book.getId());
  Book edited=books.findById(book.getId());
  edited.setTotalQuantity(0);
  edited.setAvailableQuantity(0);
  assertThrows(SQLException.class,()->books.update(edited));
 }
 @Test void inventoryCannotBecomeNegative()throws Exception{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("UPDATE books SET available_quantity=-1 WHERE id=?")){p.setLong(1,book.getId());assertThrows(SQLException.class,p::executeUpdate);}}
 @Test void inventoryCannotExceedTotal()throws Exception{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("UPDATE books SET available_quantity=total_quantity+1 WHERE id=?")){p.setLong(1,book.getId());assertThrows(SQLException.class,p::executeUpdate);}}
 @Test void concurrentBorrowersCannotTakeTheLastCopy()throws Exception{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("UPDATE books SET total_quantity=1,available_quantity=1 WHERE id=?")){p.setLong(1,book.getId());p.executeUpdate();}CountDownLatch start=new CountDownLatch(1);ExecutorService pool=Executors.newFixedThreadPool(2);try{List<Future<Boolean>>results=new ArrayList<>();for(User u:List.of(member,other))results.add(pool.submit(()->{start.await();try{service.borrow(u.getId(),book.getId());return true;}catch(BookUnavailableException e){return false;}}));start.countDown();int winners=0;for(Future<Boolean>f:results)if(f.get())winners++;assertEquals(1,winners);assertEquals(0,books.findById(book.getId()).getAvailableQuantity());}finally{pool.shutdownNow();}}
 @Test void serviceTranslatesUnavailableSqlStateToCustomException()throws Exception{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("UPDATE books SET available_quantity=0 WHERE id=?")){p.setLong(1,book.getId());p.executeUpdate();}BookUnavailableException ex=assertThrows(BookUnavailableException.class,()->service.borrow(member.getId(),book.getId()));assertEquals("No available copy remains",ex.getMessage());}
}
