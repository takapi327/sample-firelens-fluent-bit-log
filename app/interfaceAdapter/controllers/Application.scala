package interfaceAdapter.controllers

import javax.inject.Inject
import play.api.mvc._

import mvc.util.TrackingLogging

class ApplicationController @Inject()(implicit
  cc: MessagesControllerComponents
) extends AbstractController(cc)
     with TrackingLogging {

  /**
   * Health check for AWS ALB
   */
  def healthCheck = Action { Ok("ok") }

  /**
   * Methods for validating the info log
   */
  def getInfo = Action {
    trackingLogger.info("Infoのテスト")
    Ok("ok")
  }

  /**
   * Methods for validating the warn log
   */
  def getWarn = Action {
    try { throw new Exception("Warnのテスト") } catch { case t: Throwable => trackingLogger.warn("警告のテスト", t) }
    Ok("ok")
  }

  /**
   * Methods for validating the error log
   */
  def getError = Action {
    try { throw new Exception("Errorのテスト") } catch { case t: Throwable => trackingLogger.error("エラーのテスト", t) }
    Ok("ok")
  }
}
