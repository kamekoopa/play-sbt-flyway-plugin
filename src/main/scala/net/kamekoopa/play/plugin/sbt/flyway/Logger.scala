package net.kamekoopa.play.plugin.sbt.flyway

import sbt.Keys._
import com.googlecode.flyway.core.util.logging.LogCreator
import com.googlecode.flyway.core.util.logging
import sbt.{Logger => SbtLogger}

/**
 * ログ出力用のヘルパ
 */
object Logger {

  /**
   * [[sbt.Keys.TaskStreams]]ロガー
   *
   * @param streams taskStream
   */
  class StreamLogger(streams: TaskStreams) extends BaseLogger {
    val log: SbtLogger = streams.log
  }

  /**
   * コンソールロガー
   */
  class ConsoleLogger extends BaseLogger {
    val log: SbtLogger = sbt.ConsoleLogger.apply()
  }

  /**
   * Flywayのログ出力先を切り替えるために利用する[[com.googlecode.flyway.core.util.logging.LogCreator]]。
   * [[net.kamekoopa.play.plugin.sbt.flyway.Logger.StreamLogger]]を利用しています
   *
   * @param streams taskStream [[net.kamekoopa.play.plugin.sbt.flyway.Logger.StreamLogger]]構築用のストリーム
   */
  class FlywayStreamLogCreator(streams: TaskStreams) extends StreamLogger(streams) with LogCreator {

    def createLogger(clazz: Class[_]): logging.Log = new logging.Log {
      def warn(message: String) {
        FlywayStreamLogCreator.this.warn(message)
      }

      def error(message: String, e: Exception) {
        FlywayStreamLogCreator.this.error(message, e)
      }

      def error(message: String) {
        FlywayStreamLogCreator.this.error(message)
      }

      def debug(message: String) {
        FlywayStreamLogCreator.this.debug(message)
      }

      def info(message: String) {
        FlywayStreamLogCreator.this.info(message)
      }
    }
  }

  /**
   * 指定された[[sbt.Logger]]で出力するアダプタ
   */
  trait BaseLogger {
    val log: SbtLogger

    def success(message: String) = {
      log.success(message)
    }

    def debug(message: String) = {
      log.debug(message)
    }

    def info(message: String) = {
      log.info(message)
    }

    def warn(message: String) = {
      log.warn(message)
    }

    def error(message: String) = {
      log.error(message)
    }

    def error(message: String, e: Exception) = {
      log.error(message)
      log.trace(e)
    }
  }
}
