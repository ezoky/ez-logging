/*
 * Copyright (c) 2020 - EZOKY
 */

package com.ezoky.ezlogging

import com.ezoky.ezlogging.{MessageLevel => SafeMessageLevel}
import com.ezoky.ezlogging.MessageLevel.{NoLog, Debug => DebugLogger, Error => ErrorLogger, Info => InfoLogger, Trace => TraceLogger, Warn => WarnLogger}

import scala.util.Try

/**
 * @author gweinbach on 13/10/2020
 * @since 0.1.0
 */
object Safe
  extends EzLoggable {

  val None: SafeMessageLevel = NoLog

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

  def trialFromEither[E <: Throwable, T](tryT: => Either[E, T],
                                         showException: Boolean = true,
                                         messageLevel: SafeMessageLevel = Error,
                                         messageTemplate: => String = DefaultMessageTemplate): Either[String, T] =

    tryT.fold(

      // Turns Throwable into a message and logs as side effect
      exception => {
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
      },

      x =>
        Right(x)
    )

  def trialFromTry[T](tryT: => Try[T],
                      showException: Boolean = true,
                      messageLevel: SafeMessageLevel = Error,
                      messageTemplate: => String = DefaultMessageTemplate): Either[String, T] =
    trialFromEither(tryT.toEither, showException, messageLevel, messageTemplate)

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
    ).toOption

}