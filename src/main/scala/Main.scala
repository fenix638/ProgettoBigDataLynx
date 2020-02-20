import java.io._
import java.net.{HttpURLConnection, URL}
import java.util.Properties
import java.util.zip.GZIPInputStream

import Main.newJsonDF
import au.com.bytecode.opencsv.CSVWriter
import org.apache.commons.io.IOUtils
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.types.StructType
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Row


object Main {

  val conf = new SparkConf().setMaster("local[2]").setAppName("CountingSheep")
  val sc = new SparkContext(conf)
  val sqlContext = new HiveContext(sc)

  import sqlContext.implicits._

  val schema = ScalaReflection.schemaFor[Finale].dataType.asInstanceOf[StructType]
  val hiveContext = new HiveContext(sc)
  val jsonDF = hiveContext.read.schema(schema).json(System.getProperty("user.dir") + "//src//main//resources//downloadedJson//File.json")
  val newJsonDF = jsonDF
    .withColumnRenamed("type", "type_field")
    .withColumnRenamed("public", "public_field")
    .withColumnRenamed("private", "private_field")
    .withColumnRenamed("default", "default_field")


  def readProperties(): String = {
    val prop: Properties = new Properties()

    prop.load(new FileInputStream(new File("src/main/resources/application.properties")))

    val path = "http://data.githubarchive.org/" + prop.getProperty("Anno") + "-" + prop.getProperty("Mese") + "-" + prop.getProperty("Giorno") + "-0.json.gz"

    // val path = "http://data.githubarchive.org/2018-03-01-0.json.gz"

    return path
  }


  def downloadFile(path: String): Unit = {

    val url = new URL(path)
    val conn: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
    //@outpustream è il file che viene copiato sul desktop, cambia il percorso se non vuoi ciò
    val outputStream = new BufferedOutputStream(new FileOutputStream(new File(System.getProperty("user.dir") + "//src//main//resources//downloadedJson//File.json")))
    conn.connect()
    conn.setInstanceFollowRedirects(true)
    var redirect: java.lang.Boolean = false

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

   // val path = readProperties();

    //downloadFile(path);

    //singoliActor(newJsonDF)

    //singoliRepo(newJsonDF)

    //countRepo(newJsonDF)

    //countEvent(newJsonDF)

    //countMaxEventPerActor(newJsonDF)

    countMaxEventPerRepo(newJsonDF)

    //countCommitPerActor(newJsonDF)

    //countEventPerActor(newJsonDF)

    //countCommitDivisiPerActorEType(newJsonDF)

    //countEventPerOraActorRepoEType(newJsonDF)

    //countMaxMinEventPerActorOraRepo(newJsonDF)

    //tipiEvent(newJsonDF)

    //countMaxMinCommitPerOra(newJsonDF)

    //countMaxMinCommitPerOraActor

    //countActorPerOra(newJsonDF)

    //countMaxMinActorPerOra(newJsonDF)
  }

  def dfQuery(dfQuery: Array[Row], path: String): Unit = {
    val fileDF = new File(System.getProperty("user.dir") + "//src//main//resources//csvResult//" + path + ".csv")

    val writerDF = new FileWriter(fileDF)

    val writeOnCsvDF = new CSVWriter(writerDF,
      CSVWriter.DEFAULT_SEPARATOR,
      CSVWriter.DEFAULT_QUOTE_CHARACTER,
      CSVWriter.DEFAULT_ESCAPE_CHARACTER,
      CSVWriter.DEFAULT_LINE_END
    )

    writeOnCsvDF.writeNext(dfQuery.map(_.toString()))
  }

  def dsQuery(dsQuery: Array[Row], path: String): Unit = {
    val fileDS = new File(System.getProperty("user.dir") + "//src//main//resources//csvResult//" + path + "ActorSingoliDS.csv")

    val writerDS = new FileWriter(fileDS)

    val writeOnCsvDS = new CSVWriter(writerDS,
      CSVWriter.DEFAULT_SEPARATOR,
      CSVWriter.DEFAULT_QUOTE_CHARACTER,
      CSVWriter.DEFAULT_ESCAPE_CHARACTER,
      CSVWriter.DEFAULT_LINE_END
    )

    writeOnCsvDS.writeNext(dsQuery.map(_.toString()))
  }

  def rddQuery(rddQuery: Array[String], path: String): Unit = {
    val fileRDD = new File(System.getProperty("user.dir") + "//src//main//resources//csvResult//" + path + "ActorSingoliRDD.csv")

    val writerRDD = new FileWriter(fileRDD)

    val writeOnCsvRDD = new CSVWriter(writerRDD,
      CSVWriter.DEFAULT_SEPARATOR,
      CSVWriter.DEFAULT_QUOTE_CHARACTER,
      CSVWriter.DEFAULT_ESCAPE_CHARACTER,
      CSVWriter.DEFAULT_LINE_END
    )

    writeOnCsvRDD.writeNext(rddQuery)
  }

  //Trovare i singoli «actor» e salvarli in formato csv/su db postgres su un file;

  def singoliActor(newJsonDF: DataFrame): Unit = {
    //DATAFRAME
    val query = newJsonDF.select(newJsonDF.col("actor")).distinct().collect()
    dfQuery(query, "ActorSingoliDF")

    //DATASET
    val datasetParsed = newJsonDF.as[FinaleForRDD]

    val queryDS = datasetParsed.select(datasetParsed.col("actor")).distinct().collect()

    dsQuery(queryDS, "ActorSingoliDS")

    //RDD

    val rdd = datasetParsed.rdd

    val queryRDD = rdd.filter(
      x => x.actor != null
    ).map(x => x.actor).distinct().collect()

    rddQuery(queryRDD.map(_.toString), "ActorSingoliRDD")

  }

  //Trovare i singoli «repo» e salvarli in formato csv/su db postgres su un file;
  def singoliRepo(newJsonDF: DataFrame): Unit = {
    val query = newJsonDF.select(newJsonDF.col("repo")).distinct().collect()

    dfQuery(query, "RepoSingoliDF")
    //DATASET
    val datasetParsed = newJsonDF.as[FinaleForRDD]

    val queryDS = datasetParsed.select(datasetParsed.col("repo")).distinct().collect()

    dsQuery(queryDS, "RepoSingoliDS")

    //RDD

    val rdd = datasetParsed.rdd

    val queryRDD = rdd.filter(
      x => x.repo != null
    ).map(x => x.repo).distinct().collect()

    rddQuery(queryRDD.map(_.toString), "RepoSingoliRDD")
  }

  //Contare il numero di «repo»;
  def countRepo(newJsonDF: DataFrame): Unit = {
    //DF
    val query = newJsonDF.select(newJsonDF.col("repo")).count()
    println(query)
    //DS
    val datasetParsed = newJsonDF.as[FinaleForRDD]
    val queryDS = datasetParsed.select(newJsonDF.col("repo")).count()
    println(queryDS)
    //RDD
    val rdd = datasetParsed.rdd
    val queryRDD = rdd.filter(
      x => x.repo != null
    ).map(x => x.repo).count()
    println(queryRDD)

  }

  //Contare il numero di «event», divisi per «type», «actor», «repo»;
  def countEvent(newJsonDF: DataFrame): Unit = {
    //DF
    val query = newJsonDF.groupBy("type_field", "actor", "repo").agg($"type_field").count()
    println(query)
    //DS
    val datasetParsed = newJsonDF.as[FinaleForRDD]
    val queryDS = datasetParsed.groupBy("type_field", "actor", "repo").agg($"type_field").count()
    println(queryDS )
    //RDD
    val rdd = datasetParsed.rdd
    val queryRDD = rdd
  }

  //Trovare il massimo/minimo numero di «event» per «actor»;
  def countMaxEventPerActor(newJsonDF: DataFrame): Unit ={
    //DF
    val queryMax = newJsonDF.groupBy($"type_field",$"actor").agg(count("type_field") as("a")).agg(max("a")).show()
    val queryMin = newJsonDF.groupBy($"type_field",$"actor").agg(count("type_field") as("b")).agg(min("b")).show()
    //DS
    val datasetParsed = newJsonDF.as[FinaleForRDD]
    val queryDSMax = datasetParsed.groupBy($"type_field",$"actor").agg(count("type_field") as("a")).agg(max("a")).show()
    val queryDsMin = datasetParsed.groupBy($"type_field",$"actor").agg(count("type_field") as("a")).agg(min("a")).show()
    //RDD
    val rdd = datasetParsed.rdd
    val queryRDD = rdd
  }
  //Trovare il massimo/minimo numero di «event» per ora per «repo»;
  def countMaxEventPerRepo(newJsonDF: DataFrame): Unit ={
    //DF
    val queryMax = newJsonDF.groupBy($"type_field",$"repo",$"created_at").agg(count("type_field") as("a")).agg(max("a")).show()
    val queryMin = newJsonDF.groupBy($"type_field",$"repo",$"created_at").agg(count("type_field") as("b")).agg(min("b")).show()
    //DS
    val datasetParsed = newJsonDF.as[FinaleForRDD]
    val queryDSMax = datasetParsed.groupBy($"type_field",$"repo",$"created_at").agg(count("type_field") as("a")).agg(max("a")).show()
    val queryDsMin = datasetParsed.groupBy($"type_field",$"repo",$"created_at").agg(count("type_field") as("a")).agg(min("a")).show()
    //RDD
    val rdd = datasetParsed.rdd
    val queryRDD = rdd
  }

  //Contare il numero di «commit» per ogni «actor»;
  def countCommitPerActor(newJsonDF: DataFrame): Unit ={
    //DF
    val query = newJsonDF.groupBy("actor", "payload.commits").agg($"payload.commits").count()
    println(query)
    //DS
    val datasetParsed = newJsonDF.as[FinaleForRDD]
    val queryDS = datasetParsed.groupBy("actor", "payload.commits").agg($"payload.commits").count()
    println(queryDS )
    //RDD
    val rdd = datasetParsed.rdd
    val queryRDD = rdd
  }


  //Contare il numero di <<event>> per ogni <<actor>>
  def countEventPerActor(newJsonDF: DataFrame): Unit ={
    //DF
    val query = newJsonDF.groupBy("actor", "type_field").agg($"type_field").count()
    println(query)
    //DS
    val datasetParsed = newJsonDF.as[FinaleForRDD]
    val queryDS = datasetParsed.groupBy("actor", "type_field").agg($"type_field").count()
    println(queryDS)
    //RDD
    val rdd = datasetParsed.rdd
    val queryRDD = rdd
  }

  //Contare il numero di <<commit>> divisi per <<type>> e <<actor>>

  def countCommitDivisiPerActorEType(newJsonDF: DataFrame): Unit ={
    //DF
    val query = newJsonDF.groupBy("payload.commits", "type_field", "actor").agg($"payload.commits").count()
    println(query)
    //DS
    val datasetParsed = newJsonDF.as[FinaleForRDD]
    val queryDS = datasetParsed.groupBy("payload.commits", "type_field", "actor").agg($"payload.commits").count()
    println(queryDS)
    //RDD
    val rdd = datasetParsed.rdd
    val queryRDD = rdd
  }

  //Contare il numero di «event», divisi per «type», «actor», «repo» e <<ora>>;
  def countEventPerOraActorRepoEType(newJsonDF: DataFrame): Unit = {
    //DF
    val query = newJsonDF.groupBy("type_field", "actor", "repo", "created_at").agg($"type_field").count()
    println(query)
    //DS
    val datasetParsed = newJsonDF.as[FinaleForRDD]
    val queryDS = datasetParsed.groupBy("type_field", "actor", "repo", "created_at").agg($"type_field").count()
    println(queryDS )
    //RDD
    val rdd = datasetParsed.rdd
    val queryRDD = rdd
  }


  //Trovare il massimo/minimo numero di «event» per <<ora>> , <<repo>>, «actor»;
  def countMaxMinEventPerActorOraRepo(newJsonDF: DataFrame): Unit ={
    //DF
    val queryMax = newJsonDF.groupBy($"type_field",$"actor", $"repo", $"created_at").agg(count("type_field").as("a")).agg(max("a")).show()
    val queryMin = newJsonDF.groupBy($"type_field",$"actor", $"repo", $"created_at").agg(count("type_field").as("a")).agg(min("a")).show()
    //DS
    val datasetParsed = newJsonDF.as[FinaleForRDD]
    val queryDSMax = datasetParsed.groupBy($"type_field",$"actor", $"repo", $"created_at").agg(count("type_field").as("a")).agg(max("a")).show()
    val queryDSMin = datasetParsed.groupBy($"type_field",$"actor", $"repo", $"created_at").agg(count("type_field").as("a")).agg(min("a")).show()
    //RDD
    val rdd = datasetParsed.rdd
    val queryRDD = rdd
  }

  //Trovare i vari tipi di evento <<type>> e salvarli in formato csv
  def tipiEvent(newJsonDF: DataFrame): Unit = {
    val query = newJsonDF.select(newJsonDF.col("type_field")).distinct().collect()

    dfQuery(query, "TipiDiEventDF")
    //DATASET
    val datasetParsed = newJsonDF.as[FinaleForRDD]
    val queryDS = datasetParsed.select(datasetParsed.col("type_field")).distinct().collect()
    dsQuery(queryDS, "TipiDiEventDS")

    //RDD
    val rdd = datasetParsed.rdd
    val queryRDD = rdd.filter(
      x => x.repo != null
    ).map(x => x.repo).distinct().collect()

    rddQuery(queryRDD.map(_.toString), "TipiDiEventRDD")
  }

  //Trovare il massimo/minimo numero di <<commit>> per ora
  def countMaxMinCommitPerOra(newJsonDF: DataFrame): Unit ={
    //DF
    val queryMax = newJsonDF.groupBy($"payload.commits", $"created_at").agg(count("payload.commits").as("a")).agg(max("a")).show()
    val queryMin = newJsonDF.groupBy($"payload.commits", $"created_at").agg(count("payload.commits").as("a")).agg(min("a")).show()
    //DS
    val datasetParsed = newJsonDF.as[FinaleForRDD]
    val queryDSMax = datasetParsed.groupBy($"payload.commits", $"created_at").agg(count("payload.commits").as("a")).agg(max("a")).show()
    val queryDSMin = datasetParsed.groupBy($"payload.commits", $"created_at").agg(count("payload.commits").as("a")).agg(min("a")).show()
    //RDD
    val rdd = datasetParsed.rdd
    val queryRDD = rdd
  }

  //Trovare il massimo/minimo numero di <<commit>> per ora e per actor
  def countMaxMinCommitPerOraActor(newJsonDF: DataFrame): Unit ={
    //DF
    val queryMax = newJsonDF.groupBy($"payload.commits", $"created_at", $"actor").agg(count("payload.commits").as("a")).agg(max("a")).show()
    val queryMin = newJsonDF.groupBy($"payload.commits", $"created_at", $"actor").agg(count("payload.commits").as("a")).agg(min("a")).show()
    //DS
    val datasetParsed = newJsonDF.as[FinaleForRDD]
    val queryDSMax = datasetParsed.groupBy($"payload.commits", $"created_at", $"actor").agg(count("payload.commits").as("a")).agg(max("a")).show()
    val queryDSMin = datasetParsed.groupBy($"payload.commits", $"created_at", $"actor").agg(count("payload.commits").as("a")).agg(min("a")).show()
    //RDD
    val rdd = datasetParsed.rdd
    val queryRDD = rdd
  }

  //Contare il numero di <<actor>> attivi per ogni <<ora>>
  def countActorPerOra(newJsonDF: DataFrame): Unit ={
    //DF
    val query = newJsonDF.select("actor", "created_at").groupBy($"created_at").count().show()
    println(query)
    //DS
    val datasetParsed = newJsonDF.as[FinaleForRDD]
    val queryDS = datasetParsed.select("actor", "created_at").groupBy($"created_at").count().show()
    println(queryDS)
    //RDD
    val rdd = datasetParsed.rdd
    val queryRDD = rdd
  }

  //Trovare il massimo/minimo numero di <<actor>> attivi per <<ora>>
  def countMaxMinActorPerOra(newJsonDF: DataFrame): Unit ={
    //DF
    val queryMax = newJsonDF.groupBy( $"created_at", $"actor").agg(count("created_at").as("a")).agg(max("a")).show()
    val queryMin = newJsonDF.groupBy( $"created_at", $"actor").agg(count("created_at").as("a")).agg(min("a")).show()
    //DS
    val datasetParsed = newJsonDF.as[FinaleForRDD]
    val queryDSMax = datasetParsed.groupBy( $"created_at", $"actor").agg(count("created_at").as("a")).agg(max("a")).show()
    val queryDSMin = datasetParsed.groupBy( $"created_at", $"actor").agg(count("created_at").as("a")).agg(min("a")).show()
    //RDD
    val rdd = datasetParsed.rdd
    val queryRDD = rdd

  }




}