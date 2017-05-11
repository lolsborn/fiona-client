package net.readsync

import java.util.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.{ Calendar, TimeZone }

object Util {
  
  def getTimeStamp() = {
    val cal = Calendar.getInstance()
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    format.setTimeZone(TimeZone.getTimeZone("GMT"))
    format.format(cal.getTime)
  }
  
  def parseTimestamp(dateStr:String):Timestamp = {
    new Timestamp(Util.parseDateString(dateStr).getTime)
  }
  
  def parseDateString(dateStr:String):Date = {
    val df = new SimpleDateFormat("yyyy-MM-ddThh:mm:ss+SSSS")
    df.parse(dateStr)
  }
  
}
