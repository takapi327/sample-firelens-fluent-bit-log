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

  def getService = Action {
    Ok("Sample firelens fluent bit logのアプリケーション")
  }

  /**
   * Methods for validating the warn log
   */
  def getWarn = Action {
    trackingLogger.warn("Tracking Logger 警告のテスト")
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
