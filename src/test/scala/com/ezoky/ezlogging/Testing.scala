package com.ezoky.ezlogging

import org.mockito.MockitoSugar
import org.slf4j.Logger

import scala.reflect.runtime.universe.TypeTag

/**
 * @author gweinbach on 25/11/2020
 * @since 0.1.0
 */
object Testing
  extends MockitoSugar {

  def loggerFixture(loggerId: String,
                    isSomethingEnabled: Logger => Boolean,
                    isEnabled: Boolean): Logger = {
    val underlying = mock[org.slf4j.Logger]
    when(underlying.getName).thenReturn(loggerId)
    when(isSomethingEnabled(underlying)).thenReturn(isEnabled)
    underlying
  }

  object EzLoggable {

    def apply(id: String): EzLoggable =
      new EzLoggable {
        override protected lazy val loggerId: String = id
      }

    def apply(sl4JLogger: Logger): EzLoggable =
      new EzLoggable {
        override private[ezlogging] lazy val logger: Logger = sl4JLogger
        override protected lazy val loggerId: String = sl4JLogger.getName
      }

    def apply(loggerId: String,
              isSomethingEnabled: Logger => Boolean,
              isEnabled: Boolean): EzLoggable = {
      val fixture = loggerFixture(loggerId, isSomethingEnabled, isEnabled)
      Testing.EzLoggable(fixture)
    }
  }

  object EzLoggableType {

    def apply[T: TypeTag](sl4JLogger: Logger): EzLoggableType[T] =
      new EzLoggableType[T] {
        override private[ezlogging] lazy val logger: Logger = sl4JLogger
        override protected lazy val loggerId: String = sl4JLogger.getName
      }

    import scala.reflect.runtime.universe.typeOf

    def apply[T: TypeTag](isSomethingEnabled: Logger => Boolean,
                          isEnabled: Boolean): EzLoggableType[T] = {
      val fixture = loggerFixture(typeOf[T].typeSymbol.fullName, isSomethingEnabled, isEnabled)
      Testing.EzLoggableType(fixture)
    }
  }

  object LogLevel {

    def apply[T: TypeTag](loggableMethod: EzLoggable => (=> String) => Unit,
                          isSomethingEnabled: Logger => Boolean,
                          isLogLevelEnabled: Boolean): (LogLevel, Logger) = {
      val loggableFixture = Testing.EzLoggableType[T](isSomethingEnabled, isLogLevelEnabled)
      (new LogLevel {
        override def isEnabled: Boolean = isLogLevelEnabled
        protected lazy val loggable: EzLoggableType[T] = loggableFixture
        override val log: (=> String) => Unit = loggableMethod(loggable)
      }, loggableFixture.logger)
    }
  }

}
