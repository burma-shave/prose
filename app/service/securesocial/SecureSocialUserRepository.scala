package service.securesocial

import java.util.UUID
import config.slick.Profile
import config.{Persistence, CurrentPlayDb}
import securesocial.core._
import scala.Some
import scala.Some
import securesocial.core.UserId
import securesocial.core.OAuth2Info
import securesocial.core.OAuth1Info
import scala.Some
import play.api.libs.json.{JsValue, Json}
/**
 * Author: Eric Jutrzenka
 * Date:   14/04/2013
 * Time:   15:44
 */

trait SecureSocialUserRepository {
  this: Profile =>

  import profile.simple._

  import AuthenticationMethod._

  val mappingById = for {
    id <- Parameters[String]
    user <- UserMappings if user.id is id
  } yield user.userId

  implicit val AuthMethodTypeMapper = MappedTypeMapper.base[AuthenticationMethod, String](
    authMethod => authMethod.method,
    authMethod => authMethod match {
      case OAuth1.method => OAuth1
      case OAuth2.method => OAuth2
      case OpenId.method => OpenId
      case UserPassword.method => UserPassword
      case _ => throw new IllegalStateException
    }
  )

  implicit val OAuth1InfoTypeMapper = MappedTypeMapper.base[OAuth1Info, String](
    info => Json.stringify(Json.obj(
      "token" -> info.token,
      "secret" -> info.secret
      )),
    info => {
      val parse: JsValue = Json.parse(info)
      OAuth1Info((parse \ "token").as[String], (parse \ "secret").as[String])
    }
  )



  object UserMappings extends Table[(String, String)]("USER_AUTH_MAPPING") {
    def id = column[String]("ID", O.PrimaryKey)

    def userId = column[String]("USER_ID")

    def firstName = column[String]("FIRST_NAME")

    def lastName = column[String]("LAST_NAME")

    def fullName = column[String]("FULL_NAME")

    def email = column[Option[String]]("EMAIL")

    def avatarUrl = column[Option[String]]("AVATAR_URL")

    def authMethod = column[AuthenticationMethod]("AUTHENTICATION_METHOD")

    def oAuth1Info = column[Option[OAuth1Info]]("OAUTH_1_INFO")

   // def oAuth2Info = column[Option[OAuth2Info]]("OAUTH_2_INFO")

  //  def passwordInfo = column[Option[PasswordInfo]]("PASSWORD_INFO")

    def * = id ~ userId ~ firstName ~ lastName ~ fullName ~ email ~ avatarUrl ~ authMethod ~ oAuth1Info
  }

  def save(socialUser: Identity, userId: UUID) {
    db withSession {
      implicit session: Session =>
        mappingById(socialUser.id.id + socialUser.id.providerId).firstOption match {
          case None => UserMappings.insert(socialUser.id.id + socialUser.id.providerId, userId.toString)
          case Some(_) =>
        }
    }
  }

  def isMappedToUser(socialUser: Identity, userId: UUID) =
    db withSession {
      implicit session: Session =>
        mappingById(socialUser.id.id + socialUser.id.providerId).firstOption match {
          case None => false
          case Some(_) => true
        }
    }

  def find(socialId: UserId): Option[Identity] = db withSession {
    implicit session: Session =>
      db withSession {
        implicit session: Session =>
          mappingById(socialId.id + socialId.providerId).firstOption match {
            case None => None
            case Some(userId) => Option(SocialUser(socialId))
          }
      }
  }


}

object SecureSocialUserRepository extends SecureSocialUserRepository with CurrentPlayDb {

  var userMappings: Map[UserId, UUID] = Map[UserId, UUID]()
  var users: Map[UserId, Identity] = Map[UserId, Identity]()


}
