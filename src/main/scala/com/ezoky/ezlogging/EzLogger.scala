package com.ezoky.ezlogging

/**
 * A type class to define structural type with minimal API for a Logger.
 * We use this type class instead of directly Slf4J Logger in order to enable injection and ease tests.
 *
 * @author gweinbach on 27/12/2020
 * @since 0.2.0
 */
private[ezlogging] trait EzLogger {

  def id: String

  def isTraceEnabled: Boolean

  def trace(message: String): Unit

  def isDebugEnabled: Boolean

  def debug(message: String): Unit

  def isInfoEnabled: Boolean

  def info(message: String): Unit

  def isWarnEnabled: Boolean

  def warn(message: String): Unit

  def isErrorEnabled: Boolean

  def error(message: String): Unit

  def error(message: String,
            throwable: Throwable): Unit
}

private[ezlogging] trait EzLoggerFactory {
  def buildLogger(loggerId: String): EzLogger
}

private[ezlogging] object EzLoggerFactory {

  import org.slf4j.{Logger, LoggerFactory}

  private[ezlogging] object Slf4J
    extends EzLoggerFactory {

    def buildLogger(loggerId: String): EzLogger =
      new EzLogger {

        private lazy val logger: Logger =
          LoggerFactory.getLogger(loggerId)
        
        override def id: String = logger.getName

        override def isTraceEnabled: Boolean = logger.isTraceEnabled

        override def trace(message: String): Unit = logger.trace(message)

        override def isDebugEnabled: Boolean = logger.isDebugEnabled

        override def debug(message: String): Unit = logger.debug(message)

        override def isInfoEnabled: Boolean = logger.isInfoEnabled

        override def info(message: String): Unit = logger.info(message)

        override def isWarnEnabled: Boolean = logger.isWarnEnabled

        override def warn(message: String): Unit = logger.warn(message)

        override def isErrorEnabled: Boolean = logger.isErrorEnabled

        override def error(message: String): Unit = logger.error(message)

        override def error(message: String,
                           throwable: Throwable): Unit = logger.error(message, throwable)
      }
  }

}