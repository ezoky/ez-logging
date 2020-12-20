/*
 * Copyright (c) 2020 - EZOKY
 */

package com.ezoky.ezlogging

import com.ezoky.ezlogging.LogLevel.{Debug => DebugLogger, Error => ErrorLogger, Info => InfoLogger, NoLog => NoLogger, Trace => TraceLogger, Warn => WarnLogger}

import scala.util.{Failure, Success, Try}

/**
 * `Safe(...)` is intended to be used as a replacement for `Try(...).toOption`.
 *
 * It has a parameterized side effect: it logs a message in case of failure.
 *
 * Message logging is as fast as possible: message is not computed if required log level is not enabled.
 *
 * NB Safe type is used only as a Tag type to define default LogLevels
 *
 * @author gweinbach on 13/10/2020
 * @since 0.1.0
 */
trait Safe

object Safe {

  /**
   * Simple placeholder for `logLevel` parameter in `Safe` methods
   */
  val NoLog = NoLogger

  /**
   * Simple placeholder for `logLevel` parameter in `Safe` methods
   */
  val Trace = TraceLogger[Safe]()

  /**
   * Simple placeholder for `logLevel` parameter in `Safe` methods
   */
  val Debug = DebugLogger[Safe]()

  /**
   * Simple placeholder for `logLevel` parameter in `Safe` methods
   */
  val Info = InfoLogger[Safe]()

  /**
   * Simple placeholder for `logLevel` parameter in `Safe` methods
   */
  val Warn = WarnLogger[Safe]()

  /**
   * Simple placeholder for `logLevel` parameter in `Safe` methods
   */
  val Error = ErrorLogger[Safe]()

  /**
   * First string is substituted with Throwable class name.
   * Second string is subsituted with Throwable message
   */
  val DefaultMessageTemplate = "%s: %s"

  /**
   * A `Try(t).toEither` alternative with logging side effect in case of Failure.
   *
   * Returned `Left` message is lazy to avoid useless computation if message is not further used.
   */
  def trialFromTry[T](tryT: => Try[T],
                      logLevel: LogLevel = Error,
                      showException: Boolean = true,
                      messageTemplate: => String = DefaultMessageTemplate): Either[() => String, T] =

    tryT match {
      case Failure(exception) =>

        // Turns exception into a lazy message
        val lazyMessage = () => {
          val exceptionClassName = exception.getClass.getName
          val exceptionMessage = Option(exception.getMessage).getOrElse(exception.toString)
          Try(
            // This might throw an Exception if message format is not compliant with specs:
            // - it must contain at most 2 string placeholders (%s)
            messageTemplate.format(
              exceptionClassName,
              exceptionMessage
            )
          ).getOrElse {
            Error.log(s"Bad message template: '$messageTemplate'. It should contain at most 2 string placeholders (%s).")
            DefaultMessageTemplate.format(
              exceptionClassName,
              exceptionMessage
            )
          }
        }

        if (showException) {
          Error.logException(lazyMessage(), exception)
        }
        logLevel.log(lazyMessage())

        Left(lazyMessage)

      case Success(x)
      =>
        Right(x)
    }

  /**
   * An `Either[Throwable, T]` wrapper with logging side effect in case of Left.
   *
   * Transforms the Left into a (lazy) logged message.
   */
  def trialFromEither[E <: Throwable, T](eitherT: => Either[E, T],
                                         logLevel: LogLevel = Error,
                                         showException: Boolean = true,
                                         messageTemplate: => String = DefaultMessageTemplate): Either[() => String, T] = {
    val tryT: Try[T] = eitherT.fold(
      failed =>
        Failure(failed),
      successful =>
        Success(successful)
    )

    trialFromTry(
      tryT,
      logLevel,
      showException,
      messageTemplate
    )
  }

  /**
   * Catches any exception in `toTry` and logs a message according to given LogLevel.
   * Possibly logged Message is returned in `Left`.
   * In case of failure, the returned message is lazy to avoid uselessly building a potentially costly message in case
   * it is not used.
   *
   * `Safe.trial(somethingToTry)` is an alternative to `Safe(somethingToTry)` in case you would prefer a returned
   * `Either` (that keeps track of the logged message) instead of a simple `Option`.
   *
   */
  def trial[T](toTry: => T,
               logLevel: LogLevel = Error,
               showException: Boolean = true,
               messageTemplate: => String = DefaultMessageTemplate): Either[() => String, T] =
    trialFromTry(
      Try(toTry),
      logLevel,
      showException,
      messageTemplate
    )


  /**
   * Catches any exception in `toTry` and logs a message according to given LogLevel.
   *
   * @param toTry           The expression we want to try safely
   * @param logLevel        The log level we want the message to be logged in case of failure. Default is `Error`
   *                        An alternate LogLevel can be provided: either a predefined one (see default constant values
   *                        on `Safe` object) or any custom LogLevel object (that can be defined for a specific type
   *                        instead of `Safe`).
   *                        NB default log level is not defined for type `T` but for type `Safe`.
   * @param showException   A flag determining if the Exception message will be logged (always at Error level).
   *                        Logging the Exception message is independent from (and occurs before) logging the actual
   *                        message.
   * @param messageTemplate A message format containing at most 2 string placeholders: first one is for the Exception
   *                        class name, second one is for the Exception message. Default template is `"%s: %s"`
   * @tparam T The type of the tried expression
   * @return
   */
  def apply[T](toTry: => T,
               logLevel: LogLevel = Error,
               showException: Boolean = true,
               messageTemplate: => String = DefaultMessageTemplate): Option[T] =
    trial[T](
      toTry,
      logLevel,
      showException,
      messageTemplate
    ).fold(
      _ => None,
      r => Some(r)
    )
}