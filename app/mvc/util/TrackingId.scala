package mvc.util

import java.util.UUID

trait TrackingId {
  val generateUUID = UUID.randomUUID
}
