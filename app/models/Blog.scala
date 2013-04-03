/**
 * Author: Eric Jutrzenka
 * Date:   15/03/2013
 * Time:   20:44
 */

package models

import java.util.UUID

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
  val commandHandler: CommandHandler = new CommandHandler with StdOutPersistenceComponent
}

