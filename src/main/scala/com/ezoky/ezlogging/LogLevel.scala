/*
 * Copyright (c) 2020 - EZOKY
 */

package com.ezoky.ezlogging

import scala.reflect.runtime.universe.TypeTag

/**
 * Provides with a log function corresponding to one particular log level.
 *
 * One implementation for each usual log level (including NoLog).
 *
 * @author gweinbach on 13/10/2020
 * @since 0.1.0
 */
trait LogLevel {
  def isEnabled: Boolean
  val log: (=> String) => Unit
}

object LogLevel {

  /**
   * `NoLog` log level is considered always enabled
   */
  case object NoLog extends LogLevel {

    override def isEnabled: Boolean = true

    override lazy val log: (=> String) => Unit = _ => ()
  }

  sealed private[LogLevel] abstract class LogLevelWithLogger[T: TypeTag] extends LogLevel {
    protected lazy val loggable: EzLoggableType[T] = new EzLoggableType[T]
  }

  case class Trace[T: TypeTag]() extends LogLevelWithLogger[T] {

    override def isEnabled: Boolean = loggable.logger.isTraceEnabled

    override lazy val log: (=> String) => Unit = loggable.trace _
  }

  case class Debug[T: TypeTag]() extends LogLevelWithLogger[T] {

    override def isEnabled: Boolean = loggable.logger.isDebugEnabled

    override lazy val log: (=> String) => Unit = loggable.debug _
  }

  case class Info[T: TypeTag]() extends LogLevelWithLogger[T] {

    override def isEnabled: Boolean = loggable.logger.isInfoEnabled

    override lazy val log: (=> String) => Unit = loggable.info _
  }

  case class Warn[T: TypeTag]() extends LogLevelWithLogger[T] {

    override def isEnabled: Boolean = loggable.logger.isWarnEnabled

    override lazy val log: (=> String) => Unit = loggable.warn _
  }

  case class Error[T: TypeTag]() extends LogLevelWithLogger[T] {

    override def isEnabled: Boolean = loggable.logger.isErrorEnabled

    override lazy val log: (=> String) => Unit = loggable.error _
    lazy val logException: (=> String, => Throwable) => Unit = loggable.error _
  }

  /**
   * Intended to be used in Config parsing.
   *
   * @param messageLevelString
   * @tparam T
   * @return
   */
  def parse[T: TypeTag](messageLevelString: String): Option[LogLevel] =

    messageLevelString.trim.toLowerCase match {
      case "error" => Some(Error[T]())
      case "warn" => Some(Warn[T]())
      case "info" => Some(Info[T]())
      case "debug" => Some(Debug[T]())
      case "trace" => Some(Trace[T]())
      case "nolog" => Some(NoLog)
      case _ => {
        Debug[T]().log(s"Failed to parse '$messageLevelString' into a LogLevel")
        None
      }
    }

}
