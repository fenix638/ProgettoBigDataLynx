import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.types.StructType
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.FlatSpec
import org.scalatest.matchers.must.Matchers

class FinaleTest extends FlatSpec with Matchers {
  "Finale dataframe" should "convert correctly with Finale case class" in {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("SparkProject")

    val context = new SparkContext(sparkConf)
    val sqlContext = new HiveContext(context)

    val schema = ScalaReflection.schemaFor[Finale].dataType.asInstanceOf[StructType]
    import sqlContext.implicits._
    val jsonDF = sqlContext.read.schema(schema).json("C:\\Users\\Scott\\Desktop\\FinaleJson.json")
    val newJsonDF = jsonDF
      .withColumnRenamed("type","type_field")
      .withColumnRenamed("public","public_field")
      .withColumnRenamed("private","private_field")
      .withColumnRenamed("default","default_field")
      newJsonDF.show()

   /* val userDF = newJsonDF.select("payload.issue.user.*").withColumnRenamed("type","type_field")

    userDF.show()*/

    val datasetParsed = newJsonDF.as[FinaleForRDD]
   // print(datasetParsed.count())


    //val dfJson = sqlContext.read.json("C:\\Users\\Scott\\Desktop\\PayloadTest.json")

    //val rdd = dfJson.as[Payload].rdd

    //print(rdd.count())

    assert(true)
  }
}
