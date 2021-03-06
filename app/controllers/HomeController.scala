package controllers

import javax.inject._

import play.api.mvc._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.cache.Cached
import play.api.cache.NamedCache

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(@NamedCache("session-cache") cached: Cached, val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index = cached((_: RequestHeader) => "index", 5) {
    Action { implicit request =>
      Ok(views.html.index("Your new application is ready."))
    }
  }


}
