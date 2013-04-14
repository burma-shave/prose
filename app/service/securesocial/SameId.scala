package service.securesocial

import securesocial.core.{Identity, Authorization}
import SecureSocialUserRepository._
import java.util.UUID

/**
 * Author: Eric Jutrzenka
 * Date:   14/04/2013
 * Time:   18:04
 */
case class SameId(userId: String) extends Authorization {
  def isAuthorized(user: Identity): Boolean =
    isMappedToUser(user, UUID.fromString(userId))
}
