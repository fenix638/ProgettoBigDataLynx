import java.io._
import java.net.{HttpURLConnection, URL}
import java.util.Properties
import java.util.zip.GZIPInputStream
import org.apache.commons.io.IOUtils
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}



object Main {

  def readProperties(): String = {
    val prop: Properties = new Properties()

    prop.load(new FileInputStream(new File("C:\\Users\\Studente\\Desktop\\ProgettoBigData2019\\eskere\\ProgettoBigDataLynx\\src\\main\\resources\\application.properties")))

    val path = "http://data.githubarchive.org/"+prop.getProperty("Anno")+"-"+prop.getProperty("Mese")+"-"+prop.getProperty("Giorno")+"-0.json.gz"

    // val path = "http://data.githubarchive.org/2018-03-01-0.json.gz"

    return path
  }


  def downloadFile(path: String): Unit = {

    val url = new URL(path)
    val conn: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
    //@outpustream è il file che viene copiato sul desktop, cambia il percorso se non vuoi ciò
    val outputStream = new BufferedOutputStream(new FileOutputStream(new File("C:/Users/Studente/Desktop/FileCopiatoScala.json")))
    conn.connect()
    conn.setInstanceFollowRedirects(true)
    var redirect: Boolean = false

    if (conn.getResponseCode == 301) {
      redirect = true;
    }

    if (redirect) {
      val nuovaConn = new URL(conn.getHeaderField("Location")).openConnection().asInstanceOf[HttpURLConnection]
      nuovaConn.addRequestProperty("User-Agent", "")
      nuovaConn.connect()
      val reader = new GZIPInputStream(new BufferedInputStream((nuovaConn.getInputStream)))

      try {
        IOUtils.copy(reader, outputStream)
        IOUtils.closeQuietly(reader)
        IOUtils.closeQuietly(outputStream)
      }
      catch {
        case x: Exception => println("hai inserito valori sbagliati" + x.getMessage);
      }
      finally {
        nuovaConn.disconnect()
        outputStream.flush()
        outputStream.close()
        reader.close()
      }
    }

  }


  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("CountingSheep")
    val sc = new SparkContext(conf)

    val hiveContext = new HiveContext(sc)
    /*   val jsonDF = hiveContext.read.json("D:/Lynx/Big Data/cose/abc.json")

     // println(jsonDF.toString());

      val c = jsonDF.dtypes
      c.foreach(x=>println(x._1+" "+x._2))
*/
    val path = readProperties();

    downloadFile(path);

  }














}



}
