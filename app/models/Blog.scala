/**
 * Author: Eric Jutrzenka
 * Date:   15/03/2013
 * Time:   20:44
 */

package models

import java.util.UUID
import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession


trait Command
case class CreateUser(id:UUID, firstName: String, surname: String) extends Command

case class CreateArticle(id: UUID, author: UUID, title: String, body: String) extends Command

trait UserRepository {
  def create(id: UUID, firstName: String, surname: String)
}

trait ArticleRepository {
  def create(id: UUID, author: UUID, title: String, body: String)
}

trait PersistenceComponent {
  val userRepository: UserRepository
  val articleRepository: ArticleRepository
}

trait StdOutPersistenceComponent extends PersistenceComponent {
  val userRepository = new StdOutUserRepository
  val articleRepository = new StdOutArticleRepository

  class StdOutUserRepository extends UserRepository {
    def create(id: UUID, firstName: String, surname: String) {
      println("create!")
    }
  }

  class StdOutArticleRepository extends ArticleRepository {
    def create(id: UUID, author: UUID, title: String, body: String) {

        println("create article!")
    }
  }
}

trait SlickPersistenceComponent extends PersistenceComponent {

  val userRepository = new SlickUserRepository
  val articleRepository = new SlickArticleRepository



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
      Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver") withSession {
        println(Users.ddl)
        Users.ddl.create
        Users.insert(id.toString, firstName, surname)
      }
    }
  }

  class SlickArticleRepository extends ArticleRepository{
    def create(id: UUID, author: UUID, title: String, body: String) {

    }
  }
 }

//trait StdOutPersistenceComponent extends PersistenceComponent {
//}

class CommandHandler { this: PersistenceComponent =>
  def handle(command: Command) = command match {
    case CreateUser(id:UUID, firstName: String, surname: String) =>
      userRepository.create(id, firstName, surname)
    case CreateArticle(id: UUID, author: UUID, title: String, body: String) =>
      articleRepository.create(id, author, title, body)
  }
}

object App {
  val commandHandler: CommandHandler = new CommandHandler with SlickPersistenceComponent
}

