package net.readsync

import java.sql._

object Device {
  
  class NotFoundException extends Exception
  
  def touch(syncTime:String, uid:Long, conn:Connection) {
    val stmt:Statement = conn.createStatement
    val q = "UPDATE device set last_sync='%s' WHERE user_id=%d".format(syncTime, uid)
    stmt.executeUpdate(q)
  }
  
  def deviceForUid(uid:Long, conn:Connection):Device = {
    val stmt:Statement = conn.createStatement
    val q = "SELECT * FROM device WHERE user_id=%d".format(uid)
    val rs:ResultSet = stmt.executeQuery(q)
    if (rs.next) {
      val serialno = rs.getString("device_serialno")
      val lastSync = rs.getString("last_sync")
      val privateKey = rs.getString("private_key")
      val adpToken = rs.getString("adp_token")
      return new Device(serialno, uid, lastSync, privateKey, adpToken)
    }
    throw new Device.NotFoundException
  }
  
  def exists(uid:Long, conn:Connection):Boolean = {
    val stmt:Statement = conn.createStatement
    val q = "SELECT * FROM device WHERE user_id=%d".format(uid)
    val rs:ResultSet = stmt.executeQuery(q)
    if (rs.next)
      return true
    false
  }  
  
}

class Device(_serialno:String, _uid:Long, _lastSync:String, _privateKey:String, _adpToken:String) {
  def uid = _uid
  def serialno = _serialno
  def lastSync = _lastSync
  def privateKey = _privateKey
  def adpToken = _adpToken
}
