package net.readsync

import java.io._
import org.apache.http.client._
import org.apache.http._
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.params._
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.methods.{ HttpPost, HttpGet }
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.util.EntityUtils
import org.apache.http.entity.StringEntity

object Http {

  def map2ee(values: Map[String, Any]) = java.util.Arrays asList (
    values.toSeq map { 
      case (k, v) => new BasicNameValuePair(k, v.toString)
    } toArray : _*
  )
}

class Response(resp: org.apache.http.HttpResponse) {

  def status:Int = resp.getStatusLine().getStatusCode
  def reason:String = resp.getStatusLine().getReasonPhrase
  def version:String = resp.getStatusLine().getProtocolVersion().toString
  
  def body():String = {
    if (resp.getEntity == null)
      ""
    else
      EntityUtils.toString(resp.getEntity)
  }
}

class Get(url: String) extends HttpGet(url) {
  
  protected val httpclient = new DefaultHttpClient  
  def execute():Response = new Response(httpclient.execute(this))     
}

class Post(url: String, postData: String) extends HttpPost(url) {

  val httpParams:HttpParams = new BasicHttpParams()
  val timeoutConnection = 1000 * 10
  HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection)
  protected val httpclient = new DefaultHttpClient(httpParams)

  setEntity(new StringEntity(postData))
  
  def execute():Response = new Response(httpclient.execute(this))     
}