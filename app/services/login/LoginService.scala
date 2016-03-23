package services.login

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.Logger
import repositories.user.UserRepoComponent

import scala.concurrent.{ExecutionContext, Future}

trait LoginServiceComponent {
  /**
    * Validate username and password.
    *
    * @param username
    * @param password
    */
  def validate(username: String, password: String): Future[ValidaResult]
}

@Singleton
class LoginService @Inject()(
                              userRepoComponent: UserRepoComponent, actorSystem: ActorSystem)(
                              implicit executionContext: ExecutionContext)
  extends LoginServiceComponent {
  private val logger = Logger(this.getClass)

  /**
    * Validate username and password.
    *
    * @param username
    * @param password
    */
  override def validate(username: String, password: String): Future[ValidaResult] = {
    userRepoComponent.findUserByName(username).map {
      case Nil => UserNotExists()
      case Seq(user) => if (password.equals(user.password)) ValidSuccess() else PasswordInvalid()
      case _ => UserNotUnique()
    }
  }
}

sealed abstract class ValidaResult

final case class ValidSuccess(message: Option[String] = None) extends ValidaResult

final case class UserNotExists(message: Option[String] = None) extends ValidaResult

final case class PasswordInvalid(message: Option[String] = None) extends ValidaResult

final case class UserNotUnique(message: Option[String] = None) extends ValidaResult

