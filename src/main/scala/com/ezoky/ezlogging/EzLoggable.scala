/*
 * Copyright (c) 2020 - EZOKY
 */

package com.ezoky.ezlogging

import org.slf4j.{Logger, LoggerFactory}

/**
 * A "javaish" high performance logger (based on underlying `org.slf4j.Logger`) with a very straightforward interface:
 * should be used through inheritance.
 *
 * One can put safely any complex (costly!) processing in the message block (including string interpolation) as it will
 * only be executed if message level is adequate. This is especially useful in debug/trace.
 * 
 * Only errors can be logged as a result of a Throwable.
 *
 * @author gweinbach on 13/10/2020
 * @since 0.1.0
 */
trait EzLoggable {

  /**
   * Can be overriden
   */
  @transient
  protected lazy val loggerId: String = getClass.getName

  /**
   * Can be overriden, but I don't see any good reason to do it except for test reason (replacing it with a mock).
   * This is the reason for the `private[ezlogging]` modifier.
   * If overriden, shis should be kept consistent with [[loggerId]] value
   */
  @transient
  private[ezlogging] lazy val logger: Logger = LoggerFactory.getLogger(loggerId)

  // Using reference parameters avoids "delazying" of lazy parameter if not required:
  // the string message parameter is computed only if proper log level is enabled.
  def trace(message: => String): Unit = if (logger.isTraceEnabled) logger.trace(message)

  def debug(message: => String): Unit = if (logger.isDebugEnabled) logger.debug(message)

  def info(message: => String): Unit = if (logger.isInfoEnabled) logger.info(message)

  def warn(message: => String): Unit = if (logger.isWarnEnabled) logger.warn(message)

  def error(message: => String): Unit = if (logger.isErrorEnabled) logger.error(message)

  def error(message: => String,
            throwable: => Throwable): Unit = if (logger.isErrorEnabled) logger.error(message, throwable)

}


object EzLoggable {

  /**
   * Use only when nothing else is possible: this does not give any interesting information about source
   * of message (logger Id is [[EzLoggable]])
   */
  val Default: EzLoggable = EzLoggable(getClass.getName)

  def apply(id: String): EzLoggable =
    new EzLoggable {
      override protected lazy val loggerId: String = id
    }

  def apply(clazz: Class[_]): EzLoggable =
    EzLoggable(clazz.getName)

  /**
   * Use only in tests
   */
  private[ezlogging] def apply(sl4JLogger: Logger): EzLoggable =
    new EzLoggable {
      override protected lazy val loggerId: String = sl4JLogger.getName
      override private[ezlogging] lazy val logger: Logger = sl4JLogger
    }
}