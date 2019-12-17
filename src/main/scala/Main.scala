import org.apache.spark.{SparkConf, SparkContext}

object Main {
  def main(args: Array[String]): Unit ={

    val conf = new SparkConf().setMaster("local[2]").setAppName("CountingSheep")
    val sc = new SparkContext(conf);

  }
}
