package controllers

import play.api._
import play.api.data.Forms._
import play.api.data._


import play.api.mvc._
import play.api.libs.openid._
import play.api.libs.concurrent.Execution.Implicits._

import scala.Seq

import models.App.commandHandler._
import models.CreateUser
import java.util.UUID


object Application extends Controller {

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
    handle(CreateUser(UUID.randomUUID(), "Eric", "Jutrzenka"))
    Ok(views.html.index("!", loginForm))
  }


  def createUser = Action { implicit request =>
    handle(createUserForm.bindFromRequest.get)
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