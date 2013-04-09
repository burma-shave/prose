package controllers

import play.api._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.api.libs.openid._
import play.api.libs.concurrent.Execution.Implicits._
import scala.Seq
import java.util.UUID
import models.slick.SlickPersistenceComponent
import models.PersistenceComponent

trait Command
case class CreateUser(id:UUID, firstName: String, surname: String) extends Command
case class CreateArticle(id: UUID, author: UUID, title: String, body: String) extends Command

class CommandHandler(implicit val app: Application = play.api.Play.current) { this: PersistenceComponent =>
  def handle(command: Command) = command match {
    case CreateUser(id:UUID, firstName: String, surname: String) =>
      userRepository.create(id, firstName, surname)
    case CreateArticle(id: UUID, author: UUID, title: String, body: String) =>
      articleRepository.create(id, author, title, body)
  }
}

object Application extends Controller {

  val commandHandler: CommandHandler = new CommandHandler with SlickPersistenceComponent

  val loginForm = Form[String](single(
    "openid" -> nonEmptyText
  ))

  val createUserForm = Form(mapping(
     "firstName" -> nonEmptyText,
     "surname" -> nonEmptyText
  )((firstName, surname) => CreateUser(UUID.randomUUID(), firstName, surname))
    ((create: CreateUser) => Some(create.firstName, create.surname))
  )

  def index = Action {
    commandHandler.handle(CreateUser(UUID.randomUUID(), "Eric", "Jutrzenka"))
    Ok(views.html.index("!", loginForm))
  }


  def createUser = Action { implicit request =>
    commandHandler.handle(createUserForm.bindFromRequest.get)
    Ok("ok")
  }


  def loginPost = Action { implicit request =>
    loginForm.bindFromRequest.fold(
    error => {
      Logger.info("bad request " + error.toString)
      BadRequest(error.toString)
    },
    {
      case (openid) =>  AsyncResult (
        OpenID.redirectURL(
          openid,
          routes.Application.openIDCallback.absoluteURL(),
          Seq("email" -> "http://schema.openid.net/contact/email"))
          map { url => Redirect(url)
        } recover {
          case _ => Redirect(routes.Application.index)
        }
      )
    }
    )
  }


  def openIDCallback = Action { implicit request =>
    AsyncResult(
      OpenID.verifiedId map { info => {
        Ok(info.id + "\n" + info.attributes).withSession("user" -> info.attributes.getOrElse("email", "error"))
      }
      } recover {
        case _ => Redirect(routes.Application.index)
      }
    )
  }

}