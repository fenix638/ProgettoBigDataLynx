import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.FlatSpec
import org.scalatest.matchers.must.Matchers

class PayloadTest extends FlatSpec with Matchers{
  "Payload dataframe" should "convert correctly with Payload case class" in {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("SparkProject")

    val context = new SparkContext(sparkConf)
    val sqlContext = new HiveContext(context)

    import sqlContext.implicits._
    val dfJson = sqlContext.read.json("C:\\Users\\Scott\\Desktop\\PayloadTest.json")

    val rdd = dfJson.as[Payload].rdd

    print(rdd.count())

    assert(true)
  }
}
