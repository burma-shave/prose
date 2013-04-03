package controllers

import play.api._
import data.Form
import scala.concurrent._
import play.api.data.Forms._

import play.api.mvc._
import play.api.libs.openid._
import play.api.libs.concurrent.Execution.Implicits._



object Application extends Controller {

  val loginForm = Form[String](single(
    "openid" -> nonEmptyText
  ))

  def index = Action {
    Ok(views.html.index("!", loginForm))
  }




  def loginPost = Action { implicit request =>
    loginForm.bindFromRequest.fold(
    error => {
      Logger.info("bad request " + error.toString)
      BadRequest(error.toString)
    },
    {
      case (openid) =>  AsyncResult (
        OpenID.redirectURL(openid, routes.Application.openIDCallback.absoluteURL()) map { url =>
          Ok(url)
        } recover {
          case _ => Redirect(routes.Application.index)
        }
      )
    }
    )
  }

  def openIDCallback = Action { implicit request =>
    AsyncResult(
      OpenID.verifiedId map { info =>
        Ok(info.id + "\n" + info.attributes)
      } recover {
        case _ => Redirect(routes.Application.index)
      }
    )
  }

}