/*
 * Copyright (c) 2020 - EZOKY
 */

package com.ezoky.ezlogging

import com.ezoky.ezlogging.MessageLevel.{NoLog => NoLogger, Debug => DebugLogger, Error => ErrorLogger, Info => InfoLogger, Trace => TraceLogger, Warn => WarnLogger}
import com.ezoky.ezlogging.{MessageLevel => SafeMessageLevel}

import scala.util.{Failure, Success, Try}

/**
 * @author gweinbach on 13/10/2020
 * @since 0.1.0
 */
object Safe
  extends EzLoggable {

  val NoLog: SafeMessageLevel = NoLogger

  val Trace: SafeMessageLevel = TraceLogger[Safe.type]()

  val Debug: SafeMessageLevel = DebugLogger[Safe.type]()

  val Info: SafeMessageLevel = InfoLogger[Safe.type]()

  val Warn: SafeMessageLevel = WarnLogger[Safe.type]()

  val Error: SafeMessageLevel = ErrorLogger[Safe.type]()

  /**
   * First string is substituted with Throwable class name.
   * Second string is subsituted with Throwable message
   */
  private val DefaultMessageTemplate = "%s: %s"

  def trialFromTry[T](tryT: => Try[T],
                      showException: Boolean = true,
                      messageLevel: SafeMessageLevel = Error,
                      messageTemplate: => String = DefaultMessageTemplate): Either[String, T] =

    tryT match {
      case Failure(exception) =>

        // Turns Throwable into a message and logs as side effect
        val message = String.format(
          messageTemplate,
          exception.getClass.getName,
          if (exception.getMessage == null) {
            exception.toString
          }
          else {
            exception.getMessage
          }
        )

        if (showException) {
          error(message, exception)
        }
        else {
          messageLevel.log(message)
        }
        Left(message)

      case Success(x) =>
        Right(x)
    }

  def trialFromEither[E <: Throwable, T](eitherT: => Either[E, T],
                                         showException: Boolean = true,
                                         messageLevel: SafeMessageLevel = Error,
                                         messageTemplate: => String = DefaultMessageTemplate): Either[String, T] = {
    val tryT: Try[T] = eitherT.fold(
      e =>
        Failure(e),
      t =>
        Success(t)
    )

    trialFromTry(tryT, showException, messageLevel, messageTemplate)
  }

  def trial[T](t: => T,
               showException: Boolean = true,
               messageLevel: SafeMessageLevel = Error,
               messageTemplate: => String = DefaultMessageTemplate): Either[String, T] =
    trialFromTry(Try(t), showException, messageLevel, messageTemplate)


  def apply[T](t: => T,
               showException: Boolean = true,
               messageLevel: SafeMessageLevel = Error,
               messageTemplate: => String = DefaultMessageTemplate): Option[T] =
    trial[T](
      t,
      showException,
      messageLevel,
      messageTemplate
    ).fold(
       _ => None,
      r => Some(r)
    )

}