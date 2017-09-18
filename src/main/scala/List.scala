import Utils.Config
import Utils.getClient
import com.dropbox.core.json.JsonReader
import com.dropbox.core.v2.files.ListFolderResult

import scala.collection.JavaConverters._

object List {

  def ls(config: Config): Unit = {

    try { listAll(config) } catch {

      case ex: JsonReader.FileLoadException =>
        val errMsg =
          s"""
            |Error loading auth.json
            |Make sure the file is available in the current directory
            |If the files does not exist run 'ule auth' to generate it
            |${ex.getMessage}
          """.stripMargin

        println(errMsg)

      case ex: Exception => println(ex.getMessage)
    }
  }

  def listAll(config: Config): Unit = {
    val client = getClient()

    _listAll(client.files.listFolder(config.lsPath))

    def _listAll(result: ListFolderResult): Unit = {
      result.getEntries.asScala.foreach(e => println(e.getPathDisplay))

      if (result.getHasMore) {
        _listAll(client.files.listFolderContinue(result.getCursor))
      }
    }
  }
}
