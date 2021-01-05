package com.ezoky.ezlogging.slf4j

import com.ezoky.ezlogging.Log.{Debug => GenericDebug, Error => GenericError, Info => GenericInfo, NoLog => GenericNoLog, Trace => GenericTrace, Warn => GenericWarn, parse => genericParse}
import com.ezoky.ezlogging.{Log => GenericLogLevel}

import scala.reflect.runtime.universe.TypeTag

/**
 * @author gweinbach on 30/12/2020
 * @since 0.2.0
 */

object LogLevel {

  val NoLog = GenericNoLog

  def Trace[T: TypeTag]() = GenericTrace[T]()

  def Debug[T: TypeTag]() = GenericDebug[T]()

  def Info[T: TypeTag]() = GenericInfo[T]()

  def Warn[T: TypeTag]() = GenericWarn[T]()

  def Error[T: TypeTag]() = GenericError[T]()

  /**
   * Intended to be used in Config parsing.
   *
   * @param messageLevelString
   * @tparam T
   * @return
   */
  def parse[T: TypeTag](messageLevelString: String): Option[GenericLogLevel] =
    genericParse[T](messageLevelString)

}
