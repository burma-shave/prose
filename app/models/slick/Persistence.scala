package models.slick

import config.slick._
import java.util.UUID

import models._


/**
 * Author: Eric Jutrzenka
 * Date:   07/04/2013
 * Time:   18:18
 */
trait SlickPersistenceComponent extends PersistenceComponent { this: Profile =>

  implicit val userRepository = new SlickUserRepository
  implicit val articleRepository = new SlickArticleRepository

  import profile.simple._

  object Users extends Table[User]("USERS") {
    def id = column[String]("ID", O.PrimaryKey)
    def firstName = column[String]("FIRSTNAME")
    def lastName = column[String]("SURNAME")
    def * = id ~ firstName ~ lastName <> (User, User.unapply _)
  }

  object Articles extends Table[(String, String, String, String)]("ARTICLES") {
    def id = column[String]("ID", O.PrimaryKey)
    def authorId = column[String]("AUTHOR_ID")
    def title = column[String]("TITLE")
    def body = column[String]("BODY")
    def * = id ~ authorId ~ title ~ body
  }

  class SlickUserRepository extends UserRepository {
    def create(id: UUID, firstName: String, surname: String) {
      db withSession { implicit session: Session =>
        Users.insert(User(id.toString, firstName, surname))
      }
    }
    def findAll(): List[User] = {
      db withSession { implicit session: Session =>
        Query(Users).list()
      }
    }
  }

  class SlickArticleRepository extends ArticleRepository {
    def create(id: UUID, author: UUID, title: String, body: String) {
      db withSession { implicit session: Session =>
        Articles.insert(id.toString(), author.toString(), title, body)
      }
    }
  }
}

