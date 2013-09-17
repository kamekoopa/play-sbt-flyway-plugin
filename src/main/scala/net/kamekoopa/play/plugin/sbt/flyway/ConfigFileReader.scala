package net.kamekoopa.play.plugin.sbt.flyway

import com.typesafe.config.{ConfigFactory, Config}
import Utils._
import sbt._


/**
 * confファイルを読み込むヘルパ
 */
object ConfigFileReader {

  val logger = new Logger.ConsoleLogger

  /**
   * システムプロパティ`config.resource`、システムプロパティ`config.file`、引数で指定されたファイル名
   * の順番でconfファイルを探します。
   *
   * @param settingFileName 最終的にフォールバックとして使われるファイル名
   * @param loader クラスローダ
   * @return 設定ファイルのオプション
   */
  def apply(settingFileName: String)(implicit loader: ClassLoader): Option[Config] = {

    val maybeConfig = sys.props.get("config.resource").flatMap({res =>
      logger.info("system property 'config.resource="+res+"' was detected")
      fromResource(res)
    })
    .orElse({
      sys.props.get("config.file").flatMap({file =>
        logger.info("system property 'config.file="+file+"' was detected")
        fromFile(file)
      })
    })
    .orElse({
      logger.info("try to read specified config file '"+ settingFileName +"'")
      fromFile(settingFileName)
    })

    if (maybeConfig.isEmpty) {
      logger.error("config file '"+settingFileName+"' not found")
    }

    maybeConfig
  }

  /**
   * ファイルシステムから読み込みます
   *
   * @param fileName ファイル名
   *
   * @return 設定ファイルのオプション
   */
  def fromFile(fileName: String): Option[Config] = {

    val configFile = new File(removeHeadSeparator(fileName))
    logger.info("attempt config file reading from file system '"+ configFile.getPath +"'")

    val option: Option[Config] = wrapOption(ConfigFactory.parseFile(configFile))
    if(!option.isDefined) {
      logger.info("'"+ configFile.getPath +"' not found")
    }
    option
  }

  /**
   * クラスパスリソース上から読み込みます
   *
   * @param resourceName リソース名
   * @param loader クラスローダ
   * @return 設定ファイルのオプション
   */
  def fromResource(resourceName: String)(implicit loader: ClassLoader): Option[Config] = {
    withClassLoader(loader){
      logger.info("attempt config file reading from resource '"+ resourceName +"'")
      val option: Option[Config] = wrapOption(ConfigFactory.parseResources(resourceName))
      if(!option.isDefined){
        logger.info("'"+ resourceName +"' not found")
      }
      option
    }
  }

  /**
   * 設定ファイルがemptyの場合Noneにします
   *
   * @param config 設定ファイル
   * @return 設定ファイルのオプション
   */
  private def wrapOption(config: Config): Option[Config] = {
    if(config.isEmpty){
      None
    }else{
      Some(config)
    }
  }
}
