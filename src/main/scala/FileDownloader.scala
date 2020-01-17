class FileDownloader {

  import sys.process._
  import java.net.URL
  import java.io.File

  def fileDownloader(url: String, filename: String):Any = {
    new URL(url) #> new File(filename) !!

    fileDownloader("http://data.githubarchive.org/2018-03-01-0.json.gz", "prova");
  }


}
