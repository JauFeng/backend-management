package actions

import play.api.mvc.Results.Unauthorized
import play.api.mvc.Security
import play.api.mvc.Security.AuthenticatedBuilder

/**
  * Authenticated Action.
  *
  * @example
  * {{{
  *  def authenticatedTest =
  *   AuthenticatedAction { implicit request =>
  *   Ok("hello: " + request.user.username)
  *   }
  * }}}
  */
object AuthenticatedAction
    extends AuthenticatedBuilder[UserInfo](
        userinfo = request =>
          request.session.get(Security.username).map(UserInfo(_)),
        onUnauthorized = request =>
          Unauthorized(views.html.defaultpages.unauthorized()))

final case class UserInfo(username: String, userRole: Option[String] = None)
