package net.kamekoopa.play.plugin.sbt.flyway

import com.typesafe.config.Config

/**
 * confファイルから読み取ったDBの設定情報を表すクラス。
 * インスタンスの生成にはコンパニオンオブジェクトを利用してください。
 */
case class DbSetting private(
  dbName: String,
  dbUrl: String,
  dbUser: Option[String],
  dbPass: Option[String],
  initOnMigrate: Boolean = false
)

/**
 * [[net.kamekoopa.play.plugin.sbt.flyway.DbSetting]]のファクトリとしてのオブジェクト
 */
object DbSetting {
  import ConfigConverter._

  /**
   * confファイルからDB名ごとの[[net.kamekoopa.play.plugin.sbt.flyway.DbSetting]]を返す
   *
   * @param config confファイル
   * @return DB名ごとのDB設定オブジェクト
   */
  def apply(config: Config): Seq[DbSetting] = {

    config.subKey("db").map({ dbName =>

      val urlKey = "db.%s.url".format(dbName)
      config.getStringOpt(urlKey) map { url =>

        val userKey = "db.%s.user".format(dbName)
        val user = config.getStringOpt(userKey)

        val passKey = "db.%s.password".format(dbName)
        val pass = config.getStringOpt(passKey)

        val iomKey = "db.%s.initOnMigrate".format(dbName)
        val initOnMigrate = config.getBooleanOpt(iomKey)  getOrElse false

        new DbSetting(dbName, url, user, pass, initOnMigrate)
      }
    }).collect { case Some(c) => c }
    .toSeq
  }
}

/**
 * [[com.typesafe.config.Config]]に便利メソッドを追加するクラス
 *
 * @param config configオブジェクト
 */
case class RichConfig(config: Config) {
  import scala.collection.JavaConverters._

  /**
   * 指定したキーのサブキー(子キー)の集合を取得する
   *
   * @param key キー名
   * @return サブキーの集合
   */
  def subKey(key: String) = config.getConfig(key).root().keySet().asScala.toSet

  /**
   * 指定したキーの値を文字列のOptionで取得する
   *
   * @param key キー名
   * @return 文字列のOption
   */
  def getStringOpt(key: String): Option[String] = {
    try{
      Some(config.getString(key))
    }catch{
      case e: Throwable => None
    }
  }

  /**
   * 指定したキーの値を真偽型のOptionで取得する
   * @param key キー名
   * @return 真偽型のOption
   */
  def getBooleanOpt(key: String): Option[Boolean] = {
    try{
      Some(config.getBoolean(key))
    }catch{
      case e: Throwable => None
    }
  }
}

/**
 * [[com.typesafe.config.Config]]を[[net.kamekoopa.play.plugin.sbt.flyway.RichConfig]]に変換する
 * implicit conversion関数があるモジュール
 * {{{
 *   import ConfigConverter._
 *   val config: Config = ???
 *   config.subKey("someKey")
 * }}}
 */
private object ConfigConverter {
  implicit def config2RichConfig(config: Config): RichConfig = RichConfig(config)
}
