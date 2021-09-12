package mvc.util

import scala.reflect.ClassTag

import org.slf4j.Logger
import org.slf4j.LoggerFactory

trait TrackingLogging {
  lazy val trackingLogger: TrackingLogger = {
    TrackingLogger(LoggerFactory.getLogger(getClass))
  }
}

sealed case class TrackingLogger(logger: Logger) extends TrackingId {

  /**
   * Message only log
   */
  def info(message:  => String): Unit = { if (logger.isInfoEnabled)  logger.info(GrantTrackingId(message))  }
  def warn(message:  => String): Unit = { if (logger.isWarnEnabled)  logger.warn(GrantTrackingId(message))  }
  def error(message: => String): Unit = { if (logger.isErrorEnabled) logger.error(GrantTrackingId(message)) }
  def debug(message: => String): Unit = { if (logger.isDebugEnabled) logger.debug(GrantTrackingId(message)) }
  def trace(message: => String): Unit = { if (logger.isTraceEnabled) logger.trace(GrantTrackingId(message)) }

  /**
   * Handle messages and exceptions
   */
  def info(message:  => String, throwable: Throwable): Unit = { if (logger.isInfoEnabled)  logger.info(GrantTrackingId(message),  throwable) }
  def warn(message:  => String, throwable: Throwable): Unit = { if (logger.isWarnEnabled)  logger.warn(GrantTrackingId(message),  throwable) }
  def error(message: => String, throwable: Throwable): Unit = { if (logger.isErrorEnabled) logger.error(GrantTrackingId(message), throwable) }
  def debug(message: => String, throwable: Throwable): Unit = { if (logger.isDebugEnabled) logger.debug(GrantTrackingId(message), throwable) }
  def trace(message: => String, throwable: Throwable): Unit = { if (logger.isTraceEnabled) logger.trace(GrantTrackingId(message), throwable) }
}

object TrackingLogger {
  def apply[T](implicit classTag: ClassTag[T]): TrackingLogger = {
    TrackingLogger(LoggerFactory.getLogger(classTag.runtimeClass))
  }
}
