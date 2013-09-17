package net.kamekoopa.play.plugin.sbt.flyway

import org.scalatest.{Suite, FunSuite}
import com.typesafe.config.Config
import org.scalatest.matchers.MustMatchers._
/**
 */
class DbSettingsSuite  extends FunSuite {

  override def nestedSuites: List[Suite] = List(
    new 通常設定ファイル
    ,new URLが無い設定ファイル
    ,new userが無い設定ファイル
    ,new passwordが無い設定ファイル
    ,new マルチDB設定ファイル
  )


  class 通常設定ファイル extends FunSuite with Helper {
    val config = getConfig("normal.conf")

    test("コンフィグ数は1") {

      val settings = DbSetting(config)
      settings must have size 1
    }

    test("db名が合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).dbName must equal ("default")
    }

    test("urlが合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).dbUrl must equal ("jdbc:h2:file:app.db")
    }

    test("userが合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).dbUser must be ('defined)
      settings(0).dbUser.get must equal ("sa")
    }

    test("passwordが合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).dbPass must be ('defined)
      settings(0).dbPass.get must equal ("")
    }

    test("initOnMigrateが合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).initOnMigrate must be (right = true)
    }
  }


  class URLが無い設定ファイル extends FunSuite with Helper {
    val config = getConfig("url_none.conf")

    test("urlがない場合は設定に含まない") {
      val settings = DbSetting(config)
      settings must be ('empty)
    }
  }


  class userが無い設定ファイル extends FunSuite with Helper {
    val config = getConfig("user_none.conf")

    test("コンフィグ数は1") {

      val settings = DbSetting(config)
      settings must have size 1
    }

    test("db名が合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).dbName must equal ("default")
    }

    test("urlが合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).dbUrl must equal ("jdbc:h2:file:app.db")
    }

    test("userが合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).dbUser must be ('empty)
    }

    test("passwordが合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).dbPass must be ('defined)
      settings(0).dbPass.get must equal ("")
    }

    test("initOnMigrateが合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).initOnMigrate must be (right = false)
    }
  }


  class passwordが無い設定ファイル extends FunSuite with Helper {
    val config = getConfig("password_none.conf")

    test("コンフィグ数は1") {

      val settings = DbSetting(config)
      settings must have size 1
    }

    test("db名が合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).dbName must equal ("default")
    }

    test("urlが合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).dbUrl must equal ("jdbc:h2:file:app.db")
    }

    test("userが合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).dbUser must be ('defined)
      settings(0).dbUser.get must equal ("sa")
    }

    test("passwordが合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).dbPass must be ('empty)
    }

    test("initOnMigrateが合っている") {

      val settings = DbSetting(config)
      settings must have size 1
      settings(0).initOnMigrate must be (right = false)
    }
  }


  class マルチDB設定ファイル extends FunSuite with Helper {
    val config = getConfig("multi_db.conf")

    test("コンフィグ数は2") {

      val settings = DbSetting(config)
      settings must have size 2
    }

    test("db名が合っている") {

      val settings = DbSetting(config)
      settings must have size 2
      settings(0).dbName must equal ("default")
      settings(1).dbName must equal ("secondary")
    }

    test("urlが合っている") {

      val settings = DbSetting(config)
      settings must have size 2
      settings(0).dbUrl must equal ("jdbc:h2:file:app.db1")
      settings(1).dbUrl must equal ("jdbc:h2:file:app.db2")
    }

    test("userが合っている") {

      val settings = DbSetting(config)
      settings must have size 2
      settings(0).dbUser must be ('defined)
      settings(0).dbUser.get must equal ("sa1")
      settings(1).dbUser must be ('defined)
      settings(1).dbUser.get must equal ("sa2")
    }

    test("passwordが合っている") {

      val settings = DbSetting(config)
      settings must have size 2
      settings(0).dbPass must be ('defined)
      settings(0).dbPass.get must equal ("1")
      settings(1).dbPass must be ('defined)
      settings(1).dbPass.get must equal ("2")
    }

    test("initOnMigrateが合っている") {

      val settings = DbSetting(config)
      settings must have size 2
      settings(0).initOnMigrate must be (right = true)
      settings(1).initOnMigrate must be (right = false)
    }
  }


  trait Helper {
    implicit val loader = Thread.currentThread().getContextClassLoader
    def getConfig(file: String) = ConfigFileReader.fromResource(file).get
  }
}
