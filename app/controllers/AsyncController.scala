package controllers

import javax.inject._

import actions.AuthenticatedAction
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api._
import play.api.http.ContentTypes
import play.api.libs.{Comet, EventSource}
import play.api.mvc._
import play.api.libs.json.Json
import services.user.UserServiceComponent

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * This controller creates an `Action` that demonstrates how to write
  * simple asychronous code in a controller. It uses a timer to
  * asynchronously delay sending a response for 1 second.
  *
  * @param actorSystem We need the `ActorSystem`'s `Scheduler` to
  *                    run code after a delay.
  * @param exec        We need an `ExecutionContext` to execute our
  *                    asynchronous code.
  */
@Singleton
class AsyncController @Inject()(actorSystem: ActorSystem,
                                materializer: Materializer,
                                userServiceComponent: UserServiceComponent)(
                                 implicit exec: ExecutionContext) extends Controller {

  /**
    * Create an Action that returns a plain text message after a delay
    * of 1 second.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/message`.
    */
  def message = Action.async {
    getFutureMessage(1.second).map { msg =>
      Ok(msg)
    }
  }

  def authenticatedTest = AuthenticatedAction { implicit request =>
    Ok(s"Hello ${request.user.username}")
  }

  import modals.UserJsonFormat._

  def users = Action.async { implicit request =>
    userServiceComponent
      .users()
      .map { users =>
        Ok(Json.toJson(users))
      }
  }

  def comet = Action {
    Ok(views.html.test())
  }

  def cometTest = Action { implicit request =>
    implicit val m = materializer
    val df = DateTimeFormat.forPattern("HH mm ss")
    def stringSource = Source.tick(0 millis, 100 millis, "TICK").map((tick) => DateTime.now().toString())
    Ok.chunked(stringSource via EventSource.flow).as(ContentTypes.EVENT_STREAM)
  }

  private def getFutureMessage(delayTime: FiniteDuration): Future[String] = {
    val promise: Promise[String] = Promise[String]()

    actorSystem.scheduler.scheduleOnce(delayTime) {
      promise.success("Hi!")
    }
    promise.future
  }
}
