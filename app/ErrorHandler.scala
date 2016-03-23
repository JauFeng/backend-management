import javax.inject.{Inject, Provider}

import play.api._
import play.api.http.DefaultHttpErrorHandler

import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router

import scala.concurrent.Future

/**
  * Error handler.
  */
class ErrorHandler @Inject()(environment: Environment,
                             configuration: Configuration,
                             sourceMapper: OptionalSourceMapper,
                             router: Provider[Router])
    extends DefaultHttpErrorHandler(
        environment, configuration, sourceMapper, router) {
  val logger = Logger(this.getClass)

  override def onClientError(request: RequestHeader,
                             statusCode: Int,
                             message: String): Future[Result] = {
    logger.error(
        s"A client error occurred: $message, with statusCode: $statusCode, request: $request.")
//    Future.successful[Result](Status(statusCode)(s"A client error occurred: $message"))
    super.onClientError(request, statusCode, message)
  }

  override protected def onBadRequest(
      request: RequestHeader, message: String): Future[Result] = {
    logger.error(s"Bad request: $message, with request: $request.")
//    Future.successful[Result](BadRequest(s"Bad request: $message"))
    super.onBadRequest(request, message)
  }

  override protected def onForbidden(
      request: RequestHeader, message: String): Future[Result] = {
    logger.error(s"Forbidden: $message, with request: $request.")
//    Future.successful(Forbidden(s"Forbidden: $message"))
    super.onForbidden(request, message)
  }

  override protected def onNotFound(
      request: RequestHeader, message: String): Future[Result] = {
    logger.error(s"Not found: $message, with request: $request.")
//    Future.successful[Result](NotFound(s"Not found: $message"))
    super.onNotFound(request, message)
  }

  override protected def onOtherClientError(
      request: RequestHeader,
      statusCode: Int,
      message: String): Future[Result] = {
    logger.error(
        s"Other client error: $message, with statusCode: $statusCode, request: $request.")
//    Future.successful[Result](Status(statusCode)(s"Other client error: $message"))
    super.onOtherClientError(request, statusCode, message)
  }

  override def onServerError(
      request: RequestHeader, exception: Throwable): Future[Result] = {
    logger.error(s"A Server error occurred, with request: $request", exception)
//    Future.successful[Result](InternalServerError(s"A server error occurred: $exception."))
    super.onServerError(request, exception)
  }

  override protected def logServerError(
      request: RequestHeader, usefulException: UsefulException): Unit = {
    logger.error("A log server error occurred, with request: $request.",
                 usefulException)
//    Future.successful(InternalServerError(s"A log server error occurred: ${usefulException.getMessage}"))
    super.logServerError(request, usefulException)
  }

  override protected def onDevServerError(
      request: RequestHeader, exception: UsefulException): Future[Result] = {
    logger
      .error("A server error occurred on dev time, with request: $request.",
             exception)
//    Future.successful(InternalServerError(s"A server error occurred on dev time: ${exception.getMessage}"))
    super.onDevServerError(request, exception)
  }

  override protected def onProdServerError(
      request: RequestHeader, exception: UsefulException): Future[Result] = {
    logger
      .error("A server error occurred on prod time, with request: $request.",
             exception)
//    Future.successful(InternalServerError(s"A server error occurred on prod time: ${exception.getMessage}"))
    super.onProdServerError(request, exception)
  }
}
