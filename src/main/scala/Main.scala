import java.io.File
import com.dropbox.core.{DbxAppInfo, DbxAuthInfo, DbxRequestConfig, DbxWebAuth}
import scopt.OptionParser

object Main {

  object Commands {
    val Auth: String = "auth"
  }

  case class Config(mode: String = "",
                    keyFile: File = new File("."),
                    authFile: File = new File("auth.json"))

  def main(args: Array[String]): Unit = {

    parser().parse(args, Config()) match {
      case Some(config) =>

        config.mode match {
          case Commands.Auth => auth(config)
        }

      case None =>
      // arguments are bad, error message will have been displayed
    }
  }

  def parser(): OptionParser[Config] = {
    new scopt.OptionParser[Config]("ule") {
      head("ule", "0.1")

      // Auth options
      cmd(Commands.Auth).action( (_, c) => c.copy(mode = Commands.Auth) ).
        text("auth is a sub command for acquiring dropbox access tokens.").
        children(
          opt[File]("keys").required().valueName("<file>").
            action( (x, c) => c.copy(keyFile = x) ).
            text("file containing dropbox app keys"),

          opt[File]("out").valueName("<file>")
            .action( (x, c) => c.copy(authFile = x) ).
            text("file to store access token (defaults to auth.json)")
        )
    }
  }

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
    DbxAuthInfo.Writer.writeToFile(authInfo, config.authFile)

    println("Saved authorization information to \"" + config.authFile.getCanonicalPath + "\".")
  }
}
