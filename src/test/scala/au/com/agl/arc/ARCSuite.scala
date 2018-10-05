package au.com.agl.arc

import java.net.URI

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

import org.apache.commons.lang3.exception.ExceptionUtils
import scala.collection.JavaConverters._

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils

import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

import au.com.agl.arc.util._
import au.com.agl.arc.api._
import au.com.agl.arc.api.API._
import au.com.agl.arc.util.log.LoggerFactory 

import au.com.agl.arc.util.TestDataUtils

class ARCSuite extends FunSuite with BeforeAndAfter {

  var session: SparkSession = _  
  val targetFile = FileUtils.getTempDirectoryPath()
  val inputView = "inputView"
  val outputView = "outputView"

  before {
    implicit val spark = SparkSession
                  .builder()
                  .master("local[*]")
                  .config("spark.ui.port", "9999")
                  .appName("Spark ETL Test")
                  .getOrCreate()
    // spark.sparkContext.setLogLevel("ERROR")

    session = spark
  }

  after {
    session.stop()
  }

  test("ARCSuite") {
    implicit val spark = session
    import spark.implicits._
    implicit val logger = LoggerFactory.getLogger(spark.sparkContext.applicationId)
    implicit val arcContext = ARCContext(jobId=None, jobName=None, environment="test", environmentId=None, configUri=None, isStreaming=false)

    val dataset = TestDataUtils.getKnownDataset
    dataset.createOrReplaceTempView(inputView)

    val pipeline = ETLPipeline(
        SQLTransform(
            name="SQLTransformName", 
            inputURI=new URI(targetFile),
            sql=s"SELECT * FROM ${inputView} WHERE booleanDatum = fasdf",
            outputView=outputView,
            persist=false,
            sqlParams=Map.empty,
            params=Map.empty
        ) :: Nil
        ,Nil
    )

    val thrown = intercept[Exception with DetailException] {
        ARC.run(pipeline)
    }

    assert(thrown.getMessage.contains("cannot resolve '`fasdf`' given input columns"))
  }    
}


