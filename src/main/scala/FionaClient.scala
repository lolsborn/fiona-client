package net.readsync

import scala.xml._
import scala.collection.mutable.ListBuffer
import java.util.Date
import org.apache.commons.codec.net.URLCodec
import java.sql._

class MetaDataResponse(_syncType:String, _syncTime:String, _add:List[Book], _remove:List[Book]) {
  def syncType = _syncType
  def syncTime = _syncTime
  def add = _add
  def remove = _remove
}

object FionaClient {
  val TODO_SERVER = "todo-ta-g7g.amazon.com"
  val FIRS_SERVER = "firs-ta-g7g.amazon.com"
}

class FionaClient(uid:Long, conn:Connection) {
  

  val device = Device.deviceForUid(uid, conn)

  def parseBooks(books:NodeSeq):List[Book] = {
    val bb = new ListBuffer[Book]
    for (book <- (books \\ "meta_data")) {
      val asin = (book \ "ASIN").text
      val title = (book \ "title").text
      bb.append(new Book(asin, title))
    }
    bb.toList
  }
  
  def syncMetaData(full:Boolean = false)  {
    val resp:MetaDataResponse = fetchMetaData(full)
    
    resp.add.foreach( book => { 
      Book.upsert(book, conn)
      Book.upsertUserBook(uid, book, conn)
    })
    Device.touch(resp.syncTime, uid, conn)
  }
  
  def fetchMetaData(full:Boolean = false):MetaDataResponse = {
    val path = {
      if (full) {
        "/FionaTodoListProxy/syncMetaData"
      } else if (device.lastSync != "") {
        val encoder = new URLCodec
        val time = encoder.encode(device.lastSync)
        "/FionaTodoListProxy/syncMetaData?last_sync_time=%s".format(time)
      } else {
        "/FionaTodoListProxy/syncMetaData"
      }
    }
    
    val url = "https://%s%s".format(FionaClient.TODO_SERVER, path)
    val req = new FionaRequest(url, path, device)
    val resp:Response = req.execute()
    
    val body = resp.body
    // println(path)
    // println(body)
    val xml = XML.loadString(body)
    
    val syncType = ((xml \ "response") \ "@syncType").text
    val syncTime = (xml \ "sync_time").text.split(";")(0)
    val add = (xml \ "add_update_list") \\ "meta_data"
    val remove = (xml \ "removal_list") \\ "meta_data"
    
    Device.touch(syncTime, uid, conn)
    
    new MetaDataResponse(syncType, syncTime, parseBooks(add), parseBooks(remove))
  }
  
}