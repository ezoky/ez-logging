/*
 * Copyright (c) 2020 - EZOKY
 */

package com.ezoky.ezlogging

import scala.reflect.runtime.universe.TypeTag

/**
 * Provides with a log function corresponding to one particular log level.
 * One implementation for each usual log level (including NoLog).
 *
 * @author gweinbach on 13/10/2020
 * @since 0.1.0
 */
sealed trait MessageLevel {
  val log: (String) => Unit
}

object MessageLevel
  extends EzLoggable {

  case object NoLog extends MessageLevel {
    override lazy val log: (String) => Unit = _ => ()
  }

  sealed protected abstract class MessageLevelWithLogger[T: TypeTag] extends MessageLevel {
    protected lazy val logger: EzLogger[T] = new EzLogger[T]
  }

  case class Trace[T: TypeTag]() extends MessageLevelWithLogger[T] {
    override lazy val log: (String) => Unit = logger.trace(_)
  }

  case class Debug[T: TypeTag]() extends MessageLevelWithLogger[T] {
    override lazy val log: (String) => Unit = logger.debug(_: String)
  }

  case class Info[T: TypeTag]() extends MessageLevelWithLogger[T] {
    override lazy val log: (String) => Unit = logger.info(_: String)
  }

  case class Warn[T: TypeTag]() extends MessageLevelWithLogger[T] {
    override lazy val log: (String) => Unit = logger.warn(_: String)
  }

  case class Error[T: TypeTag]() extends MessageLevelWithLogger[T] {
    override lazy val log: (String) => Unit = logger.error(_: String)
  }


  def parse[T: TypeTag](messageLevelString: String): Option[MessageLevel] =

    messageLevelString.trim.toLowerCase match {
      case "error" => Some(Error[T]())
      case "warn" => Some(Warn[T]())
      case "info" => Some(Info[T]())
      case "debug" => Some(Debug[T]())
      case "trace" => Some(Trace[T]())
      case "nolog" => Some(NoLog)
      case _ => {
        debug(s"Failed to parse '$messageLevelString' into a MessageLevel")
        None
      }
    }

}
