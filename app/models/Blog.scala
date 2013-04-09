/**
 * Author: Eric Jutrzenka
 * Date:   15/03/2013
 * Time:   20:44
 */

package models

import java.util.UUID
import scala.slick.driver.H2Driver.simple._

import models.slick.current.dao._
import models.slick.current._

import play.api.Play.current


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

  class SlickUserRepository extends UserRepository {
    def create(id: UUID, firstName: String, surname: String) {
      db withSession { implicit session =>
        Users.insert(id.toString, firstName, surname)
      }
    }
  }

  class SlickArticleRepository extends ArticleRepository{
    def create(id: UUID, author: UUID, title: String, body: String) {
      db withSession { implicit session =>
        Articles.insert(id.toString(), author.toString(), title, body)
      }
    }
  }
 }

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

