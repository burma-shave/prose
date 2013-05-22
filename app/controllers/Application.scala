package controllers

import play.api._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

import java.util.UUID

import models.PersistenceComponent
import models.slick.SlickPersistenceComponent
import config.CurrentPlayDb

import views._

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

object Application extends Controller with SlickPersistenceComponent
with CurrentPlayDb {
  this: PersistenceComponent =>


  val createArticleForm = Form(tuple(
    "title" -> nonEmptyText,
    "body" -> nonEmptyText
  ))

  val createUserForm = Form(tuple(
    "firstName" -> nonEmptyText,
    "lastName" -> nonEmptyText
  ))

  def createUser = Action { implicit request =>
    val (firstName, lastName) = createUserForm.bindFromRequest().get
    val id: UUID = UUID.randomUUID
    userRepository.create(id, firstName, lastName)
    Created(id.toString)
  }

  def createUserView = Action { implicit request =>
    Ok(html.create_user("Create User"))
  }

  def createArticle(userId: String) = Action { implicit request =>
    val (title, body) = createArticleForm.bindFromRequest().get
    val id: UUID = UUID.randomUUID()
    val author: UUID = UUID.fromString(userId)
    articleRepository.create(id, author, title, body)
    Ok(id.toString)
  }

  def createArticleView = Action { implicit request =>
    Ok(html.create_article("Create User"))
  }
}