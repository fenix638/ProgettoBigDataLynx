import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

object Main {
  def main(args: Array[String]): Unit ={

    val conf = new SparkConf().setMaster("local[2]").setAppName("CountingSheep")
    val sc = new SparkContext(conf);

    val sqlContext = new HiveContext(sc)

    val jsonDF = sqlContext.read.json("C:\\Users\\Studente\\Downloads\\2018-03-01-0.json")

    println(jsonDF.toString())

    println(jsonDF.toString())

  }
}
