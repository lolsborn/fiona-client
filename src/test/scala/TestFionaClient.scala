package net.readsync

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import scala.xml._
import java.sql._

class FionaClientSpec extends FlatSpec with ShouldMatchers {

  val userName:String = "root"
  val password:String = "secret"
  val url:String = "jdbc:mysql://localhost/readsync"

  val conn = DriverManager.getConnection (url, userName, password)

  "syncMetaData" should "should allow forcing a full sync" in {
    val client = new FionaClient(603302696, conn)
    val resp = client.fetchMetaData(full=true)
    resp.syncType should equal("full")
  }

  "syncMetaData" should "sync device meta-data and write it to the database " in {
    val client = new FionaClient(603302696, conn)
    val resp = client.fetchMetaData()
    resp.syncType should equal("update")
  }
  
  "parseBooks" should "take a bunch of XML and return a list of book objects" in {
    val client = new FionaClient(603302696, conn)
    val xml = <response syncType="full" encryptedFionaAccountId="A6000XCDJM7IW">
                <sync_time>2010-09-28T19:57:45+0000</sync_time>
                <annotation_sync_status>1</annotation_sync_status>
                <min_todo_item_request_interval>0</min_todo_item_request_interval>
                <min_sync_metadata_request_interval>10</min_sync_metadata_request_interval>
                <log_upload_status>0</log_upload_status>
                <collection_upload_interval_secs>45</collection_upload_interval_secs>
                <add_update_list>
                  <meta_data>
                    <ASIN>B003ODIZL6</ASIN>
                    <title>The New Oxford American Dictionary</title>
                    <authors/>
                    <publishers>
                      <publisher>Oxford University Press</publisher>
                    </publishers>
                    <publication_date>2010-04-01T00:00:00+0000</publication_date>
                    <cde_contenttype>EBOK</cde_contenttype>
                    <content_type>application/x-mobipocket-ebook</content_type>
                  </meta_data>
                  <meta_data>
                    <ASIN>B002RHN7RM</ASIN>
                    <title>Coders at Work</title>
                    <authors>
                      <author>Seibel, Peter</author></authors>
                      <publishers>
                        <publisher>Apress</publisher>
                      </publishers>
                      <publication_date>2009-09-16T00:00:00+0000</publication_date>
                      <cde_contenttype>EBOK</cde_contenttype>
                      <content_type>application/x-mobipocket-ebook</content_type>
                    </meta_data>
                  </add_update_list>
                  <removal_list/>
                </response>

    val parsedBooks = client.parseBooks(xml)
        
    (parsedBooks.length) should be (2)
    
    parsedBooks(0) should have (
      'asin ("B003ODIZL6"),
      'title ("The New Oxford American Dictionary")
    )

    parsedBooks(1) should have (
      'asin ("B002RHN7RM"),
      'title ("Coders at Work")
    )
      
  }
  
  
}