package controllers

import play.api._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.api.libs.openid._
import play.api.libs.concurrent.Execution.Implicits._
import scala.Seq
import java.util.UUID

import models.{PersistenceComponent, ArticleRepository, UserRepository}
import models.slick.SlickPersistenceComponent
import config.CurrentPlayDb
import securesocial.core.java.SecureSocial.SecuredAction
import service.securesocial.{SameId, SecureSocialUserRepository}

trait Command

case class CreateUser(id: UUID, firstName: String, surname: String) extends Command

case class CreateArticle(id: UUID, author: UUID, title: String, body: String) extends Command

class CommandHandler(implicit val app: Application = play.api.Play.current) {
  this: PersistenceComponent =>
  def handle(command: Command) = command match {
    case CreateUser(id: UUID, firstName: String, surname: String) =>
      userRepository.create(id, firstName, surname)
    case CreateArticle(id: UUID, author: UUID, title: String, body: String) =>
      articleRepository.create(id, author, title, body)
  }
}

object Application extends Controller with securesocial.core.SecureSocial {

  val commandHandler: CommandHandler =
    new CommandHandler
      with SlickPersistenceComponent
      with CurrentPlayDb

  import commandHandler._

  val createArticleForm = Form(tuple(
    "title" -> nonEmptyText,
    "body" -> nonEmptyText
  ))

  def index = SecuredAction { implicit request =>
    Ok(views.html.index("!"))
  }

  def articles(userId: String) = SecuredAction(SameId(userId)) { implicit request =>
    Ok(views.html.create_article(userId, createArticleForm))
  }

  def createArticle(userId: String) = SecuredAction(SameId(userId)) { implicit request =>
    val (title, body) = createArticleForm.bindFromRequest().get
    handle(CreateArticle(UUID.randomUUID(), UUID.fromString(userId), title, body))
    Ok("ok")
  }
}