import java.io.File
import Auth.auth
import List.ls
import Utils.Config
import scopt.OptionParser

object Main {

  object Commands {
    val auth: String = "auth"
    val ls: String = "ls"
  }

  def main(args: Array[String]): Unit = {

    parser().parse(args, Config()) match {
      case Some(config) =>

        config.mode match {
          case Commands.auth => auth(config)
          case Commands.ls => ls(config)
        }

      case None =>
      // arguments are bad, error message will have been displayed
    }
  }

  def parser(): OptionParser[Config] = {
    new scopt.OptionParser[Config]("ule") {
      head("ule", "0.1")

      // auth options
      cmd(Commands.auth).action( (_, c) => c.copy(mode = Commands.auth) ).
        text("auth is a sub command for acquiring dropbox access tokens.").
        children(
          opt[File]("keys").required().valueName("<file>").
            action( (x, c) => c.copy(keyFile = x) ).
            text("file containing dropbox app keys")
        )

      // ls options
      cmd(Commands.ls).action( (_, c) => c.copy(mode = Commands.ls) ).
        text("ls is a sub command for listing dropbox folders and files.").
        children(
          arg[String]("<path>").optional().action( (x, c) => c.copy(lsPath = x) ).
            text("path to list")
        )
    }
  }
}
