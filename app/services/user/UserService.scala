package services.user

import javax.inject.{Inject, Singleton}

import modals.User
import repositories.user.UserRepoComponent
import play.api.Logger
import akka.actor.ActorSystem

import scala.concurrent.{ExecutionContext, Future}

trait UserServiceComponent {

  /**
    * Find users.
    * @return
    */
  def users(): Future[Seq[User]]
}

@Singleton
class UserService @Inject()(userRepoComponent: UserRepoComponent,
                            actorSystem: ActorSystem)(
    implicit executionContext: ExecutionContext) extends UserServiceComponent {
  private val logger = Logger(this.getClass)

  /**
    * Find users.
    * @return
    */
  override def users(): Future[Seq[User]] = userRepoComponent.users()
}
