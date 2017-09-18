import java.io.File
import com.dropbox.core.DbxAuthInfo
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.DbxRequestConfig

object Utils {

  case class Config(mode: String = "", keyFile: File = new File("."), lsPath: String = "")

  val authFile = new File("auth.json")

  def getClient(): DbxClientV2 = {
      val authInfo = DbxAuthInfo.Reader.readFromFile(authFile)
      val requestConfig = new DbxRequestConfig("ule")
      new DbxClientV2(requestConfig, authInfo.getAccessToken, authInfo.getHost)
  }
}
