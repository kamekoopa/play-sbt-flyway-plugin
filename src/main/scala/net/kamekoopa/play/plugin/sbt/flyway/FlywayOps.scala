package net.kamekoopa.play.plugin.sbt.flyway

import com.googlecode.flyway.core.Flyway
import Utils._
import com.googlecode.flyway.core.api.{MigrationInfo, MigrationInfoService}

/**
 */
case class FlywayOps(scriptLocation: String, dbSettings: Seq[DbSetting]) {

  private case class FlywayWrapper private(dbSetting: DbSetting, flyway: Flyway)
  private object FlywayWrapper {
    def apply(dbSetting: DbSetting): FlywayWrapper = {
        val flyway = new Flyway
        flyway.setDataSource(dbSetting.dbUrl, dbSetting.dbUser.getOrElse(null), dbSetting.dbPass.getOrElse(null))
        flyway.setInitOnMigrate(dbSetting.initOnMigrate)
        flyway.setLocations(removeTailSeparator(scriptLocation) + "/%s".format(dbSetting.dbName))
        new FlywayWrapper(dbSetting, flyway)
    }
  }

  private val logger = new Logger.ConsoleLogger



  private val flywayWrappers = dbSettings map { dbSetting =>
    FlywayWrapper(dbSetting)
  }

  def info() = {
    execute { wrapper =>

      logger.info("executes `info` to ["+wrapper.dbSetting.dbName+"]")
      logger.info("  "+wrapper.dbSetting.dbUrl)

      val infoService: MigrationInfoService = wrapper.flyway.info()

      if(infoService.applied().length == 0){
        logger.info("    applied - none")
      }else{
        infoService.applied().map({ info =>
          logger.info("    applied - " + infoToOutput(info))
        })
      }

      if(infoService.current() == null) {
        logger.info("    current - none")
      } else {
        logger.info("    current - " + infoToOutput(infoService.current()))
      }

      if(infoService.pending().length == 0){
        logger.info("    pending - none")
      } else {
        infoService.pending().map({ info =>
          logger.info("    pending - " + infoToOutput(info))
        })
      }
    }
  }

  def validate() = {
    execute { wrapper =>
      logger.info("executes `validate` to ["+wrapper.dbSetting.dbName+"]")
      logger.info("  "+wrapper.dbSetting.dbUrl)
      wrapper.flyway.validate()
    }
  }

  def migrate() = {
    execute { wrapper =>
      logger.info("executes `migrate` to ["+wrapper.dbSetting.dbName+"]")
      logger.info("  "+wrapper.dbSetting.dbUrl)
      val applied = wrapper.flyway.migrate()
      logger.success(applied+" applied")
    }
  }

  def repair() = {
    execute { wrapper =>
      logger.info("executes `repair` to ["+wrapper.dbSetting.dbName+"]")
      logger.info("  "+wrapper.dbSetting.dbUrl)
      wrapper.flyway.repair()
      logger.success("repair was succeed")
    }
  }

  private def execute[T](f : FlywayWrapper => T) = {
    try {
      flywayWrappers map f
    }catch{
      case e: Exception => logger.error(e.getMessage, e)
      throw e
    }
  }

  private def infoToOutput(info: MigrationInfo): String = {
    "ver: %s type: %s desc: %s".format(info.getVersion.toString, info.getType, info.getDescription)
  }
}
