package mvc.util

import java.util.UUID

trait TrackingId {
  val generateUUID = UUID.randomUUID

  /**
   * Give the Log a tracking ID
   */
  def GrantTrackingId(message: String): String = {
    s"[trackingId=${generateUUID}] ${message}"
  }
}
