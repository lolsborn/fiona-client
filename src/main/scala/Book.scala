package net.readsync

import java.sql._
import scala.xml

object Book {
  
  class NotFoundException extends Exception

  def upsertUserBook(uid:Long, book:Book, conn:Connection) {
    
    // This uses the MySQL only ON DUPLICATE KEY upsert method
    val q = """INSERT INTO user_books(user_id, book_id, added) values(?,?, ?)
            on duplicate key update user_id=?, book_id=?"""
      
    val stmt = conn.prepareStatement(q)
    stmt.setLong(1, uid)
    stmt.setString(2, book.asin)
    val date = new java.util.Date()
    val now = date.getTime
    stmt.setTimestamp(3, new Timestamp(now))
    stmt.setLong(4, uid)
    stmt.setString(5, book.asin)
    stmt.executeUpdate()    
  }
        
  def get(asin:String, conn:Connection):Book = {
    val stmt:Statement = conn.createStatement
    val q = "SELECT * FROM books WHERE asin='%s'".format(asin)
    val rs = stmt.executeQuery(q)
    
    if (rs.next) {
      val asin = rs.getString("asin")
      val title = rs.getString("title")
      return new Book(asin, title)
    }
    throw new Book.NotFoundException
  }
  
  def upsert(book:Book, conn:Connection) {
    // This uses the MySQL only ON DUPLICATE KEY upsert method
    val q = "INSERT INTO books(asin, title) values(?, ?) on duplicate key update asin=?, title=?"
    
    val stmt = conn.prepareStatement(q)

    stmt.setString(1, book.asin)
    stmt.setString(2, book.title)
    stmt.setString(3, book.asin)
    stmt.setString(4, book.title)
            
    stmt.executeUpdate()
  }
  
  def delete(book:Book, conn:Connection) {
    val stmt:Statement = conn.createStatement
    val q = "DELETE FROM books WHERE asin='%s'".format(book.asin)
    stmt.executeUpdate(q)
  }
  
  def exists(book:Book, conn:Connection):Boolean = {
    val stmt:Statement = conn.createStatement
    val q = "SELECT * FROM books WHERE asin=%s".format(book.asin)
    val rs:ResultSet = stmt.executeQuery(q)
    if (rs.next) {
      return true
    }
    false
  }
}

class Book(_asin:String, _title:String) {
  
  def asin = _asin
  def title = _title
    
  override def toString() = {
    asin
  }
}