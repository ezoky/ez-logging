/*
 * Copyright (c) 2020 - EZOKY
 */

package com.ezoky.ezlogging

/**
 * A high performance logger (based on underlying `org.slf4j.Logger`) with a very straightforward interface:
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
   * There must be an implicit [[EzLoggerFactory]] in the scope of the implementation
   */
  implicit protected val loggerFactory: EzLoggerFactory

  /**
   * Must be overriden
   */
  protected val loggerId: String

  /**
   * Can be overriden, but I don't see any good reason to do it except for test reason (replacing it with a mock).
   * This is the reason for the `private[ezlogging]` modifier.
   * If overriden, this should be kept consistent with [[loggerId]] value
   *
   * Exceptions can only be logged at Error level.
   */
  @transient
  private[ezlogging] lazy val logger: EzLogger = loggerFactory.buildLogger(loggerId)

  // Using reference parameters avoids "delazying" of lazy parameter if not required:
  // the string message parameter is computed only if proper log level is enabled.
  final def trace(message: => String): Unit = if (logger.isTraceEnabled) logger.trace(message)

  final def debug(message: => String): Unit = if (logger.isDebugEnabled) logger.debug(message)

  final def info(message: => String): Unit = if (logger.isInfoEnabled) logger.info(message)

  final def warn(message: => String): Unit = if (logger.isWarnEnabled) logger.warn(message)

  final def error(message: => String): Unit = if (logger.isErrorEnabled) logger.error(message)

  final def error(message: => String,
                  throwable: => Throwable): Unit = if (logger.isErrorEnabled) logger.error(message, throwable)

}

