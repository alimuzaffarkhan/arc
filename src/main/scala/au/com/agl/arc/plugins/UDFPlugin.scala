package au.com.agl.arc.plugins

import java.util.ServiceLoader

import scala.collection.JavaConverters._

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.SQLContext
import au.com.agl.arc.util.Utils

trait UDFPlugin {

  // return the list of udf names that were registered for logging
  def register(sqlContext: SQLContext)(implicit logger: au.com.agl.arc.util.log.logger.Logger): Seq[String]

}

object UDFPlugin {

  def registerPluginsForName(name: String)(implicit spark: SparkSession, logger: au.com.agl.arc.util.log.logger.Logger) {

    val loader = Utils.getContextOrSparkClassLoader
    val serviceLoader = ServiceLoader.load(classOf[UDFPlugin], loader)

    for (p <- serviceLoader.iterator().asScala.toList if p.getClass.getName == name) {
      val pluginUDFs = p.register(spark.sqlContext)

      val name = p.getClass.getName

      val logData = new java.util.HashMap[String, Object]()
      logData.put("name", name)
      logData.put("udfs", pluginUDFs.asJava)

      logger.info().message(s"Registered UDF Plugin $name").field("udfPlugin", logData).log()
    }
  }

}
