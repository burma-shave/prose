package service.securesocial

import _root_.java.util.UUID
import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.providers.Token
import securesocial.core.UserId
import config.Persistence

/**
 * Author: Eric Jutrzenka
 * Date:   14/04/2013
 * Time:   13:49
 */
class InMemoryUserService(application: Application) extends UserServicePlugin(application) {

  def find(id: UserId): Option[Identity] = {
    SecureSocialUserRepository.find(id)
  }

  def save(user: Identity): Identity = {
    val userId: UUID = UUID.randomUUID()
    Persistence.userRepository.create(userId, user.firstName, user.lastName)
    SecureSocialUserRepository.save(user, userId)
    user
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = None

  def save(token: Token) {}

  def findToken(token: String): Option[Token] = None

  def deleteToken(uuid: String) {}

  def deleteTokens() {}

  def deleteExpiredTokens() {}
}