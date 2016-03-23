package controllers

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.filters.csrf.{CSRFAddToken, CSRFCheck}
import services.login._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Login controller.
  *
  * @param cSRFAddToken     CSRF add token.
  * @param cSRFCheck        CSRF check.
  * @param messagesApi      messages API.
  * @param executionContext implicit default executor.
  */
@Singleton
class LoginController @Inject()(cSRFAddToken: CSRFAddToken,
                                cSRFCheck: CSRFCheck,
                                loginServiceComponent: LoginServiceComponent,
                                val messagesApi: MessagesApi)(
                                 implicit executionContext: ExecutionContext)
  extends Controller with I18nSupport {
  private val logger = Logger(this.getClass)

  /**
    * Login form.
    */
  private val loginForm = Form(
    mapping(
      "username" -> nonEmptyText(minLength = 1, maxLength = 50),
      "password" -> nonEmptyText(minLength = 8, maxLength = 20)
    )(LoginForm.apply)(LoginForm.unapply)
  )

  /**
    * Login page.
    *
    * @return
    */
  def login = cSRFAddToken {
    Action { implicit request =>
      Ok(views.html.login.login(loginForm))
    }
  }

  /**
    * Authentication.
    *
    * @return
    */
  def authenticate = cSRFCheck {
    Action.async { implicit request =>
      loginForm.bindFromRequest.fold(
        hasErrors = errorForm =>
          Future.successful(BadRequest(views.html.login.login(errorForm))),
        success = successForm => {
          loginServiceComponent.validate(successForm.username, successForm.password) map {
            case ValidSuccess(message) =>
              Redirect(request.session.get("parent").getOrElse(routes.HomeController.index().url))
            case UserNotExists(message) =>
              BadRequest(views.html.login.login(
                loginForm.withError(key = "username", message = messagesApi("auth.unknown", successForm.username))))
            case PasswordInvalid(message) =>
              BadRequest(views.html.login.login(
                loginForm.withError(key = "password", message = messagesApi("password.unknown", successForm.password))))
            case UserNotUnique(message) =>
              BadRequest(views.html.login.login(loginForm.withError(key = "username", message = "User not unique.")))
          }
        }
      )
    }
  }

  /**
    * Logout.
    *
    * @return
    */
  def logout = Action { implicit request =>
    Redirect(routes.LoginController.login()).removingFromSession(Security.username)
  }
}

/**
  * Login form.
  *
  * @param username username
  * @param password password
  */
case class LoginForm(username: String, password: String)
