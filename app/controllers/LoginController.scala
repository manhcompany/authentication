package controllers

import javax.inject.{Inject, Singleton}
import models.{DatabaseExecutionContext, User, UserRepository}
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtHeader}
import play.api.data.Forms._
import play.api.data._
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents}

import scala.concurrent.Future

case class UserLogin(email: String, password: String)

case class UserRegister(email: String, password: String)

@Singleton
class LoginController @Inject()(cc: MessagesControllerComponents,
                                userService: UserRepository)(implicit dec: DatabaseExecutionContext) extends MessagesAbstractController(cc) {
  val loginForm: Form[UserLogin] = Form[UserLogin](
    mapping(
      "email" -> text,
      "password" -> text
    )(UserLogin.apply)(UserLogin.unapply)
  )

  val userRegisterForm: Form[UserRegister] = Form[UserRegister](
    mapping(
      "email" -> text,
      "password" -> text
    )(UserRegister.apply)(UserRegister.unapply)
  )

  def login: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def validateLogin: Action[AnyContent] = Action.async { implicit request =>
    val userLoginForm = loginForm.bindFromRequest()
    userLoginForm.fold(
      hasErrors = { form =>
        Future {}(dec) map (_ => Redirect(routes.LoginController.login()))
      },
      success = { user =>
        userService.validateUser(User(user.email, user.password)) map {
          case Some(u) => {
            val token = Jwt.encode(JwtHeader(JwtAlgorithm.HS256), JwtClaim(s"""{"user_id":${u.id}, "status":${u.status}, "email":${u.email} """), "abcdef")
            Redirect(routes.HomeController.index())
              .withSession("email" -> user.email,
                "user_id" -> u.id.toString,
                "status" -> u.status.toString)
              .withHeaders("bearer" -> token)
          }
          case None => Redirect(routes.LoginController.login())
            .flashing("login_message" -> "Email or password is invalid")
        }
      }
    )
  }

  def logout: Action[AnyContent] = Action { implicit request =>
    Redirect(routes.HomeController.index()).withNewSession
  }

  def register: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.register(userRegisterForm))
  }

  def validateRegister: Action[AnyContent] = Action.async { implicit request =>
    userRegisterForm.bindFromRequest().fold(
      hasErrors = { form =>
        Future {}(dec).map(_ => Redirect(routes.LoginController.register()).flashing("error_message" -> "Error"))
      },
      success = { user =>
        userService.insert(User(user.email, user.password, 1)).map {
          case Some(_) => Redirect(routes.HomeController.index()).withSession("email" -> user.email)
          case None => Redirect(routes.LoginController.register()).flashing("error_message" -> "Error")
        }
      }
    )
  }
}
