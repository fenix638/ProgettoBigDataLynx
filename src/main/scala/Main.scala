import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.hive.HiveContext

object Main {
  def main(args: Array[String]): Unit ={
    val conf = new SparkConf().setMaster("local[2]").setAppName("CountingSheep")
    val sc = new SparkContext(conf);


    val hiveContext = new HiveContext(sc)
    val jsonDF = hiveContext.read.json("D:/Lynx/Big Data/cose/abc.json")

   // println(jsonDF.toString());

    val c = jsonDF.dtypes
    c.foreach(x=>println(x._1+" "+x._2))


  }



}
