package au.com.agl.arc.plugins

import java.util.ServiceLoader

import scala.collection.JavaConverters._
import org.apache.spark.sql.{DataFrame, SparkSession}
import au.com.agl.arc.api.API.PipelineStage
import au.com.agl.arc.util.Utils

class LogResultsetsLifecyclePlugin extends LifecyclePlugin {

  override def before(stage: PipelineStage)(implicit spark: SparkSession, logger: au.com.agl.arc.util.log.logger.Logger) {}

  override def after(stage: PipelineStage, result: Option[DataFrame], isLast: Boolean)(implicit spark: SparkSession, logger: au.com.agl.arc.util.log.logger.Logger) {
    result match {
      case Some(df) => df.show(false)
      case None =>
    }
  }

}