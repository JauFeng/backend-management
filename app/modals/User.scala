package modals

import play.api.libs.json.{Format, Json}

final case class User(id: Option[Long] = None,
                      name: String,
                      password: String,
                      fullName: Option[String] = None,
                      email: Option[String] = None,
                      isAdmin: Boolean = false,
                      age: Option[Int] = None)

object UserJsonFormat {
  implicit val userJsonFormat: Format[User] = Json.format[User]
}
