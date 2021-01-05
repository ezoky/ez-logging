/*
 * Copyright (c) 2020 - EZOKY
 */

package com.ezoky.ezlogging

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.TypeTag

/**
 * Provides with a log function corresponding to one particular log level.
 *
 * One implementation for each usual log level (including NoLog).
 *
 * @author gweinbach on 13/10/2020
 * @since 0.1.0
 */
trait Log {
  def isEnabled: Boolean

  val log: (=> String) => Unit
}

object Log {

  /**
   * `NoLog` log level is considered always enabled
   */
  case object NoLog extends Log {

    override def isEnabled: Boolean = true

    override lazy val log: (=> String) => Unit = _ => ()
  }

  sealed private[Log] abstract class LogWithLogger[T: TypeTag](implicit loggerFactory: EzLoggerFactory)
    extends Log {
    protected lazy val loggable: EzLoggableType[T] = new EzLoggableType[T]
  }

  case class Trace[T: TypeTag]()(implicit loggerFactory: EzLoggerFactory) extends LogWithLogger[T] {

    override def isEnabled: Boolean = loggable.logger.isTraceEnabled

    override lazy val log: (=> String) => Unit = loggable.trace _
  }

  case class Debug[T: TypeTag]()(implicit loggerFactory: EzLoggerFactory) extends LogWithLogger[T] {

    override def isEnabled: Boolean = loggable.logger.isDebugEnabled

    override lazy val log: (=> String) => Unit = loggable.debug _
  }

  case class Info[T: TypeTag]()(implicit loggerFactory: EzLoggerFactory) extends LogWithLogger[T] {

    override def isEnabled: Boolean = loggable.logger.isInfoEnabled

    override lazy val log: (=> String) => Unit = loggable.info _
  }

  case class Warn[T: TypeTag]()(implicit loggerFactory: EzLoggerFactory) extends LogWithLogger[T] {

    override def isEnabled: Boolean = loggable.logger.isWarnEnabled

    override lazy val log: (=> String) => Unit = loggable.warn _
  }

  case class Error[T: TypeTag]()(implicit loggerFactory: EzLoggerFactory) extends LogWithLogger[T] {

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
  def parse[T: TypeTag](messageLevelString: String)
                       (implicit loggerFactory: EzLoggerFactory): Option[Log] =

    messageLevelString.trim.toLowerCase match {
      case "error" => Some(Error[T]())
      case "warn" => Some(Warn[T]())
      case "info" => Some(Info[T]())
      case "debug" => Some(Debug[T]())
      case "trace" => Some(Trace[T]())
      case "nolog" => Some(NoLog)
      case _ => {
        Debug[T]().log(s"Failed to parse '$messageLevelString' into a Log")
        None
      }
    }
}

sealed trait LogLevel {

  def buildLog[T: TypeTag](implicit loggerFactory: EzLoggerFactory): Log
}

object LogLevel {

  case object NoLog extends LogLevel {
    override def buildLog[T: universe.TypeTag](implicit loggerFactory: EzLoggerFactory): Log.NoLog.type =
      Log.NoLog
  }

  case object Trace extends LogLevel {
    override def buildLog[T: universe.TypeTag](implicit loggerFactory: EzLoggerFactory): Log.Trace[T] =
      Log.Trace[T]()
  }

  case object Debug extends LogLevel {
    override def buildLog[T: universe.TypeTag](implicit loggerFactory: EzLoggerFactory): Log.Debug[T] =
      Log.Debug[T]()
  }

  case object Info extends LogLevel {
    override def buildLog[T: universe.TypeTag](implicit loggerFactory: EzLoggerFactory): Log.Info[T] =
      Log.Info[T]()
  }

  case object Warn extends LogLevel {
    override def buildLog[T: universe.TypeTag](implicit loggerFactory: EzLoggerFactory): Log.Warn[T] =
      Log.Warn[T]()
  }

  case object Error extends LogLevel {
    override def buildLog[T: universe.TypeTag](implicit loggerFactory: EzLoggerFactory): Log.Error[T] =
      Log.Error[T]()
  }

  /**
   * Intended to be used in Config parsing.
   *
   * @param messageLevelString
   * @return
   */
  def parse(messageLevelString: String)
           (implicit loggerFactory: EzLoggerFactory): Option[LogLevel] =

    messageLevelString.trim.toLowerCase match {
      case "error" => Some(Error)
      case "warn" => Some(Warn)
      case "info" => Some(Info)
      case "debug" => Some(Debug)
      case "trace" => Some(Trace)
      case "nolog" => Some(NoLog)
      case _ => {
        Log.Debug[LogLevel]().log(s"Failed to parse '$messageLevelString' into a LogLevel")
        None
      }
    }

}