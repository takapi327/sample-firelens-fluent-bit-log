package mvc.util

//import play.api.libs.json.Json

//import ch.qos.logback.core.LayoutBase
//import ch.qos.logback.core.CoreConstants
//import ch.qos.logback.classic.spi.ILoggingEvent

import java.util.regex.Pattern

import ch.qos.logback.classic.pattern.ThrowableProxyConverter
import ch.qos.logback.classic.spi.IThrowableProxy

class OneLineThrowableProxyConverter extends ThrowableProxyConverter {

  val sp = Pattern.compile("\r\n|[\n\r]")
  val tb = Pattern.compile("\t", Pattern.LITERAL)

  override def throwableProxyToString(tp: IThrowableProxy): String = {
    val str = super.throwableProxyToString(tp)
    tb.matcher(sp.matcher(str).replaceAll("\\\\n")).replaceAll("    ")
  }
}
/*
object LogbackJsonEncoder extends LayoutBase[ILoggingEvent] {

  def doLayout(event: ILoggingEvent): String = {
    Json.obj(
      "timeStamp" -> event.getTimeStamp,
      "level"     -> event.getLevel.toString,
      "maker"     -> event.getMarker.toString,
      "thread"    -> event.getThreadName,
      "message"   -> event.getMessage,
      "throwable" -> event.getThrowableProxy.toString,
      "class"     -> event.getClass.toString,
      "argument"  -> event.getArgumentArray.toString,
      "callerData" -> event.getCallerData.toString,
      "loggerContextVO" -> event.getLoggerContextVO.toString,
      "formattedMessage" -> event.getFormattedMessage,
      "mdcPropertyMap" -> event.getMDCPropertyMap.toString,
      "loggerName" -> event.getLoggerName,
      "coreConstants" -> CoreConstants.LINE_SEPARATOR
    ).toString()
  }
}
 */
