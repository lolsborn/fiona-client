package net.readsync

import java.util.{ Date, GregorianCalendar, Calendar }
import java.text.SimpleDateFormat

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class UtilSpec extends FlatSpec with ShouldMatchers {

   "parseDateString" should "take a amazon date string and return a date" in {
     val dateStr:String = "2010-04-01T00:00:00+0000"
     val df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss+SSSS")
     val date = df.parse(dateStr)
     val cal = new GregorianCalendar();
     cal.set(2010, Calendar.APRIL, 01, 00, 00, 00)
     cal.set(Calendar.MILLISECOND, 0)
     date.equals(cal.getTime) should equal(true)
   } 
   
   "parseTimestamp" should "take a date string and return a java.sql.Date" in {
     val dateStr:String = "2010-04-01T00:00:00+0000"
     val df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss+SSSS")
     val date = df.parse(dateStr)
     val cal = new GregorianCalendar();
     cal.set(2010, Calendar.APRIL, 01, 00, 00, 00)
     cal.set(Calendar.MILLISECOND, 0)
     val sqlDate = new java.sql.Timestamp(cal.getTime.getTime)
     sqlDate.getTime should equal(date.getTime)     
   }

}