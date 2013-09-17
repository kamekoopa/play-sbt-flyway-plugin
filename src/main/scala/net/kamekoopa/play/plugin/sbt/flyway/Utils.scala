package net.kamekoopa.play.plugin.sbt.flyway

import sbt._
import sbt.classpath.ClasspathUtilities

/**
 * その他のヘルパー関数群
 */
object Utils {

  val pathSeparator = java.io.File.pathSeparator

  /**
   * 文字列の先頭にパス区切り文字があれば除いたものを返します
   * @param path パス文字列
   * @return パス文字列
   */
  def removeHeadSeparator(path: String) = {
    if(path.startsWith(pathSeparator)){
      path.substring(1)
    }else{
      path
    }
  }

  /**
   * 文字列の最後にパス区切り文字があれば除いたものを返します
   * @param path パス文字列
   * @return パス文字列
   */
  def removeTailSeparator(path: String) = {
    if(path.endsWith(pathSeparator)){
      path.substring(0, path.length -1)
    }else{
      path
    }
  }

  /**
   * 指定されたクラスパス内で指定ブロックを実行します
   *
   * @param cp クラスパス
   * @tparam T 処理の結果の型
   * @return 処理結果
   */
  def toClassLoader[T](cp: Types.Id[Keys.Classpath]): ClassLoader = {
    ClasspathUtilities.toLoader(cp.map(_.data), getClass.getClassLoader)
  }


  /**
   * 指定ブロックを指定クラスローダに差し替えて実行します。
   * 終わると元に戻します。
   *
   * @param classLoader 差し替えるクラスローダ
   * @param f 処理
   * @tparam T 処理結果の型
   * @return 処理結果
   */
  def withClassLoader[T](classLoader: ClassLoader)(f: => T): T = {
    val thread = Thread.currentThread
    val backup = thread.getContextClassLoader
    try {
      thread.setContextClassLoader(classLoader)
      f
    } finally {
      thread.setContextClassLoader(backup)
    }
  }
}
