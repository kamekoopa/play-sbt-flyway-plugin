package net.kamekoopa.play.plugin.sbt.flyway

import org.scalatest.FunSuite
import com.typesafe.config.{ConfigException, Config}
import org.scalatest.matchers.MustMatchers._

class ConfigFileReaderSuite extends FunSuite {

  implicit val loader = Thread.currentThread().getContextClassLoader

  test("通常の設定ファイルをリソースから読み込める") {

    val option: Option[Config] = ConfigFileReader.fromResource("normal.conf")

    option must be ('defined)

    val config: Config = option.get
    config.getString("db.default.url") must equal("jdbc:h2:file:app.db")
    config.getString("db.default.user") must equal("sa")
    config.getString("db.default.password") must equal("")
    config.getString("db.default.initOnMigrate") must equal("true")
  }

  test("通常の設定ファイルをファイルシステムから読み込める") {

    val option: Option[Config] = ConfigFileReader.fromFile("src/test/resources/normal.conf")

    option must be ('defined)

    val config: Config = option.get
    config.getString("db.default.url") must equal("jdbc:h2:file:app.db")
    config.getString("db.default.user") must equal("sa")
    config.getString("db.default.password") must equal("")
    config.getString("db.default.initOnMigrate") must equal("true")
  }

  test("通常の設定ファイルをシステムプロパティ(config.resource)から読み込める") {
    try {

      System.setProperty("config.resource", "normal.conf")
      val option: Option[Config] = ConfigFileReader("dummy.conf")

      option must be ('defined)

      val config: Config = option.get
      config.getString("db.default.url") must equal("jdbc:h2:file:app.db")
      config.getString("db.default.user") must equal("sa")
      config.getString("db.default.password") must equal("")
      config.getString("db.default.initOnMigrate") must equal("true")

    }finally{
      System.clearProperty("config.resource")
    }
  }

  test("通常の設定ファイルをシステムプロパティ(config.file)から読み込める") {
    try {

      System.setProperty("config.file", "src/test/resources/normal.conf")

      val option: Option[Config] = ConfigFileReader("dummy.conf")

      option must be ('defined)

      val config: Config = option.get
      config.getString("db.default.url") must equal("jdbc:h2:file:app.db")
      config.getString("db.default.user") must equal("sa")
      config.getString("db.default.password") must equal("")
      config.getString("db.default.initOnMigrate") must equal("true")

    }finally{
      System.clearProperty("config.file")
    }
  }

  test("通常の設定ファイルをフォールバックとしての引数から読み込める") {

    val option: Option[Config] = ConfigFileReader("src/test/resources/normal.conf")

    option must be ('defined)

    val config: Config = option.get
    config.getString("db.default.url") must equal("jdbc:h2:file:app.db")
    config.getString("db.default.user") must equal("sa")
    config.getString("db.default.password") must equal("")
    config.getString("db.default.initOnMigrate") must equal("true")
  }

  test("存在しない設定ファイルはNone") {

    val option: Option[Config] = ConfigFileReader("src/test/resources/none.conf")

    option must be ('empty)
  }

  test("空の設定ファイルはNone") {

    val option: Option[Config] = ConfigFileReader("src/test/resources/empty.conf")

    option must be ('empty)
  }

  test("urlが存在しない設定ファイルでurlを取得しようとすると例外") {

    intercept[ConfigException.Missing] {
      val option: Option[Config] = ConfigFileReader("src/test/resources/url_none.conf")

      option must be ('defined)

      val config: Config = option.get
      config.getString("db.default.url")
    }
  }

  test("userが存在しない設定ファイルでuserを取得しようとすると例外") {

    intercept[ConfigException.Missing] {
      val option: Option[Config] = ConfigFileReader("src/test/resources/user_none.conf")

      option must be ('defined)

      val config: Config = option.get
      config.getString("db.default.user")
    }
  }

  test("passwordが存在しない設定ファイルでpasswordを取得しようとすると例外") {

    intercept[ConfigException.Missing] {
      val option: Option[Config] = ConfigFileReader("src/test/resources/password_none.conf")

      option must be ('defined)

      val config: Config = option.get
      config.getString("db.default.password")
    }
  }

  test("複数のDB設定が取得できる") {

    val option: Option[Config] = ConfigFileReader("src/test/resources/multi_db.conf")

    option must be ('defined)

    val config: Config = option.get
    config.getString("db.default.url") must equal("jdbc:h2:file:app.db1")
    config.getString("db.default.user") must equal("sa1")
    config.getString("db.default.password") must equal("1")
    config.getString("db.default.initOnMigrate") must equal("true")

    config.getString("db.secondary.url") must equal("jdbc:h2:file:app.db2")
    config.getString("db.secondary.user") must equal("sa2")
    config.getString("db.secondary.password") must equal("2")
    config.getString("db.secondary.initOnMigrate") must equal("false")
  }
}
