package net.kamekoopa.play.plugin.sbt.flyway

import sbt._
import sbt.Keys._
import Utils._
import com.googlecode.flyway.core.util.logging.LogFactory
import net.kamekoopa.play.plugin.sbt.flyway.Logger.FlywayStreamLogCreator

/**
 * Play2の設定ファイルを使ってflywayコマンドをsbtコンソールで発行するプラグイン
 */
object PlayFlywaySbtPlugin extends Plugin {

  /**
   * デフォルト設定
   */
  private object Defaults extends Keys {
    val settings = Seq(
      configFile := "conf/application.conf",
      scriptLocation := "db/migration"
    )
  }

  /**
   * タスクの定義
   */
  object flyway extends Keys {

    val settings = inConfig(Config)(Defaults.settings) ++ Seq(

      //infoタスク
      info <<= (fullClasspath in Runtime, streams, configFile, scriptLocation) map {
        (cp, streams, confFileName, script) =>

          implicit val loader = toClassLoader(cp)

          withClassLoader(loader){

            LogFactory.setLogCreator(new FlywayStreamLogCreator(streams))

            ConfigFileReader(confFileName).map({configFile =>
              DbSetting(configFile)
            }).map({settings =>
              FlywayOps(script, settings).info()
            })
          }
      },

      //validateタスク
      validate <<= (fullClasspath in Runtime, streams, configFile, scriptLocation) map {
        (cp, streams, confFileName, script) =>

          implicit val loader = toClassLoader(cp)

          withClassLoader(loader){

            LogFactory.setLogCreator(new FlywayStreamLogCreator(streams))

            ConfigFileReader(confFileName).map({configFile =>
              DbSetting(configFile)
            }).map({settings =>
              FlywayOps(script, settings).validate()
            })
          }
      },

      //migrateタスク
      migrate <<= (fullClasspath in Runtime, streams, configFile, scriptLocation) map {
        (cp, streams, confFileName, script) =>

          implicit val loader = toClassLoader(cp)

          withClassLoader(loader){

            LogFactory.setLogCreator(new FlywayStreamLogCreator(streams))

            ConfigFileReader(confFileName).map({configFile =>
              DbSetting(configFile)
            }).map({settings =>
              FlywayOps(script, settings).migrate()
            })
          }
      },

      //repairタスク
      repair <<= (fullClasspath in Runtime, streams, configFile, scriptLocation) map {
        (cp, streams, confFileName, script) =>

          implicit val loader = toClassLoader(cp)

          withClassLoader(loader){

            LogFactory.setLogCreator(new FlywayStreamLogCreator(streams))

            ConfigFileReader(confFileName).map({configFile =>
              DbSetting(configFile)
            }).map({settings =>
              FlywayOps(script, settings).repair()
            })
          }
      }
    )
  }
}
