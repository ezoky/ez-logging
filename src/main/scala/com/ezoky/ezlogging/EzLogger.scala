/*
 * Copyright (c) 2020 - EZOKY
 */

package com.ezoky.ezlogging

import scala.reflect.runtime.universe.TypeTag


/**
 * Type class providing logging ability as an effect.
 * The logger will use log parameters (log level) as specified for the type.
 *
 * =Explicit usage=
 * A logger can be created for a specific type.
 * {{{
 * import com.ezoky.ezlogging.EzLogger
 *
 * val intLogger = new EzLogger[Int]
 * intLogger.trace("This will be traced if Int log level is 'trace'")
 *
 * }}}
 * 
 * =Implicit usage=
 * A logger is created implicitly and will provide this type with any usual log method.
 * {{{
 * import com.ezoky.ezlogging.EzLogger._
 *
 * "Toto".debug("A debug message")
 *  1.trace("Even an int can use trace")
 *  true.warn("A boolean can warn")
 *
 * }}}
 * Use cautiously as a new EzLogger instance will be created for every call
 *
 * @author gweinbach on 14/10/2020
 * @since 0.1.0
 */
class EzLogger[T: TypeTag]
  extends EzLoggable {

  override protected lazy val loggerId: String = implicitly[TypeTag[T]].tpe.typeSymbol.fullName
}

object EzLogger {
  
  implicit def ezLoggable[T: TypeTag](t: T): EzLogger[T] = new EzLogger[T]
}
