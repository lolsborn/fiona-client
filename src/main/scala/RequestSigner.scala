package net.readsync

import java.util.logging.{ Level, Logger }
import java.util.{ Calendar, TimeZone }
import java.security.{ KeyFactory, PrivateKey, MessageDigest }
import java.security.spec.PKCS8EncodedKeySpec
import java.text.SimpleDateFormat
import javax.crypto.Cipher
import org.apache.commons.codec.binary.Base64

object RequestSigner {
  
  def getSigningDate():String = {
      val cal = Calendar.getInstance()
      val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
      format.setTimeZone(TimeZone.getTimeZone("GMT"))
      format.format(cal.getTime)
  }
    
}

class RequestSigner(_privateKey:String, adpToken:String) {

    val privateKey = decodeKey(_privateKey)

    def decodeKey(keyStr:String):PrivateKey = {
        val decoded = Base64.decodeBase64(keyStr.getBytes())
        val spec:PKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(decoded)

        KeyFactory.getInstance("RSA").generatePrivate(spec)
    }

    def digestHeaderForRequest(method:String, url:String, postdata:String,
      signingDate: =>String = RequestSigner.getSigningDate ):String = {

        val time:String = signingDate

        val sb = new StringBuffer
        sb.append(method)
        sb.append("\n")
        sb.append(url)
        sb.append("\n")
        sb.append(time)
        sb.append("\n")
        sb.append(postdata)
        sb.append("\n")
        sb.append(adpToken)

        val sig:String = generateSignature(sb.toString)
        val finalBuffer = new StringBuffer
        finalBuffer.append(sig)
        finalBuffer.append(":")
        finalBuffer.append(time)

        finalBuffer.toString
    }

    def generateSignature(data:String): String = {
      val digest:MessageDigest = MessageDigest.getInstance("SHA-256")
      digest.update(data.getBytes())
      val digestData = digest.digest()
      val cipher:Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
      cipher.init(0x1, privateKey) //TODO: replace constant
      val encryptedBytes = cipher.doFinal(digestData)
      val encodedBytes = Base64.encodeBase64(encryptedBytes)
      
      new String(encodedBytes, "UTF-8")
    }
}