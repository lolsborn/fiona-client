package net.readsync

import java.security.Security
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

object RequestSignerSpec {
  def testDate:String = "2010-08-18T20:32:58Z"
}

// This test will fail because it had some hard-coded keys in it that I pulled out.
class RequestSignerSpec extends FlatSpec with ShouldMatchers {
  Security addProvider new BouncyCastleProvider

  "A RequestSigner" should "sign a request using the a private key" in {
    
    val pk = "<removed>"
    val token = "<removed>"
    val rs = new RequestSigner(pk, token)
    
    val valid = "<removed>"

    val ret = rs.digestHeaderForRequest("GET",
      "/FionaTodoListProxy/syncMetaData?last_sync_time=2010-09-18T20%3A27%3A17%2B0000",
      "",
      RequestSignerSpec.testDate)
    
    ret should equal(valid)
  }
}