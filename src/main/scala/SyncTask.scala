package net.readsync

import org.apache.log4j.Logger
import org.json.simple.JSONObject
import java.sql._

object SyncTask {

  val log = Logger.getLogger("SyncTask")
  val userName:String = "root"
  val password:String = "loveliving"
  val url:String = "jdbc:mysql://localhost/readsync"
  Class.forName ("com.mysql.jdbc.Driver").newInstance()
  
  def run(task: JSONObject) { 
    
    val uid:Long =  task.get("uid").asInstanceOf[Long]
    log.info("Syncing: %d".format(uid))

    val conn = DriverManager.getConnection (url, userName, password)
    val client = new FionaClient(uid, conn)
    client.syncMetaData()
    conn.close()
  }
  
}