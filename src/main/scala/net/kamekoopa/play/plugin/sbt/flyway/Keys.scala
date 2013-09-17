package net.kamekoopa.play.plugin.sbt.flyway

import sbt._

/**
 * 設定とかタスクのキー
 */
trait Keys {

  lazy val Config = config("play-sbt-flyway").hide


  /* -- 設定類 -- */

  lazy val configFile = SettingKey[String]("config-file", "play's typesafe config file. default is 'application.conf'") in Config
  lazy val scriptLocation = SettingKey[String]("script-location", "migration script(sql) location. default is 'db/migration'") in Config


  /* -- タスク類 -- */

  lazy val info = TaskKey[Unit]("info", "execute flyway info task for obtain migration information.") in Config
  lazy val validate = TaskKey[Unit]("validate", "execute flyway validate task for validate migrate status.") in Config
  lazy val migrate = TaskKey[Unit]("migrate", "execute flyway migrate task.") in Config
  lazy val repair = TaskKey[Unit]("repair", "execute flyway repair task for repair meta data table of migration") in Config
}
