package net.readsync

class FionaRequest(url:String, path:String, device:Device) extends Get(url) {
  
  val signer = new RequestSigner(device.privateKey, device.adpToken)
  
  addHeader("Host", FionaClient.TODO_SERVER)
  addHeader("Connection","Keep-Alive")
  addHeader("User-Agent","Dalvik/1.2.0")
  addHeader("x-adp-authentication-token", device.adpToken)
  val sig = signer.digestHeaderForRequest("GET", path, "")
  addHeader("x-adp-request-digest", sig)
  
}