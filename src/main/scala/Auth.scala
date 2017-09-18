
import Utils.Config
import com.dropbox.core.{DbxAppInfo, DbxAuthInfo, DbxRequestConfig, DbxWebAuth}

object Auth {

  def auth(config: Config): Unit = {

    val appInfo = DbxAppInfo.Reader.readFromFile(config.keyFile)

    val requestConfig = new DbxRequestConfig("ule")
    val webAuth = new DbxWebAuth(requestConfig, appInfo)
    val webAuthRequest = DbxWebAuth.newRequestBuilder.withNoRedirect.build

    val authorizeUrl = webAuth.authorize(webAuthRequest)

    val instructionsMsg =
      s"""
         |1. Go to $authorizeUrl
         |2. Click "Allow" (you might have to log in first).
         |3. Copy the authorization code.
      """.stripMargin

    println(instructionsMsg)
    print("Enter the authorization code here: ")

    val code = io.Source.stdin.getLines().next()
    val authFinish = webAuth.finishFromCode(code)

    val successMsg =
      s"""
         |Authorization complete.
         |- User ID: ${authFinish.getUserId}
         |- Access Token: ${authFinish.getAccessToken}
       """.stripMargin

    println(successMsg)

    val authInfo = new DbxAuthInfo(authFinish.getAccessToken, appInfo.getHost)
    DbxAuthInfo.Writer.writeToFile(authInfo, Utils.authFile)

    println("Saved authorization information to \"" + Utils.authFile.getCanonicalPath + "\".")
  }
}
