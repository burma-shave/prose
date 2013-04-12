package models.slick

import play.api.db.slick.Profile
import scala.slick.driver.ExtendedProfile
import play.api.db.slick.DB
import java.util.UUID

import models.{ArticleRepository, UserRepository, PersistenceComponent}

/**
 * Author: Eric Jutrzenka
 * Date:   07/04/2013
 * Time:   18:18
 */
trait SlickPersistenceComponent extends PersistenceComponent { this: Profile =>

  val userRepository = new SlickUserRepository
  val articleRepository = new SlickArticleRepository

  import profile.simple._
  import play.api.Play.current


  object Users extends Table[(String, String, String)]("USERS") {
    def id = column[String]("ID", O.PrimaryKey) // This is the primary key column
    def firstName = column[String]("FIRSTNAME")
    def surName = column[String]("SURNAME")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = id ~ firstName ~ surName
  }

  object Articles extends Table[(String, String, String, String)]("ARTICLES") {
    def id = column[String]("ID", O.PrimaryKey) // This is the primary key column
    def authorId = column[String]("AUTHOR_ID")
    def title = column[String]("TITLE")
    def body = column[String]("BODY")
    def * = id ~ authorId ~ title ~ body
  }

  class SlickUserRepository extends UserRepository {
    def create(id: UUID, firstName: String, surname: String) {
      DB withSession { implicit session =>
        Users.insert(id.toString, firstName, surname)
      }
    }
  }

  class SlickArticleRepository extends ArticleRepository {
    def create(id: UUID, author: UUID, title: String, body: String) {
      DB withSession { implicit session =>
        Articles.insert(id.toString(), author.toString(), title, body)
      }
    }
  }
}

trait CurrentDb extends Profile {
  override val profile: ExtendedProfile = DB.driver(play.api.Play.current)
}
