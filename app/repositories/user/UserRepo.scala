package repositories.user

import javax.inject.{Inject, Singleton}

import modals.User
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile
import slick.lifted.{ProvenShape, TableQuery}

import scala.concurrent.{ExecutionContext, Future}

trait UserRepoComponent {

  /**
    * Create User.
    * @param user user
    * @return
    */
  def createUser(user: User): Future[User]

  /**
    * Modify User.
    * @param user user
    * @return
    */
  def modifyUser(user: User): Future[Int]

  /**
    * Delete User by ID.
    * @param id user's ID
    * @return
    */
  def deleteUser(id: Long): Future[Int]

  /**
    * Find users.
    * @return
    */
  def users(): Future[Seq[User]]

  /**
    * Find user by username.
    * @param userName username
    * @return
    */
  def findUserByName(userName: String): Future[Seq[User]]

  /**
    * Find user by user ID.
    * @param id userID
    * @return
    */
  def findUserById(id: Long): Future[User]
}

@Singleton
class UserRepo @Inject()(
    @NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider)(
    implicit executionContext: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile] with UserRepoComponent {
  private val logger = Logger(this.getClass)

  /**
    * User query.
    */
  private val userQuery = TableQuery[UserTable]

  /**
    * Create User.
    *
    * @param user user
    * @return
    */
  override def createUser(user: User): Future[User] = db.run {
    val action =
      (userQuery returning userQuery.map(_.id) into
          ((u, id) => u.copy(id = id))) += user
    logger.info(s"Create user SQL statements: ${action.statements}")
    action
  }

  /**
    * Modify User.
    *
    * @param user user
    * @return
    */
  override def modifyUser(user: User): Future[Int] = db.run {
    val action = userQuery.filter(_.id === user.id).update(user)
    logger.info(s"Modify user SQL statements: ${action.statements}")
    action
  }

  /**
    * Delete User by ID.
    *
    * @param id user's ID
    * @return
    */
  override def deleteUser(id: Long): Future[Int] = db.run {
    val action = userQuery.filter(_.id === id).delete
    logger.info(s"Delete user SQL statements: ${action.statements}")
    action
  }

  /**
    * Find user by username.
    *
    * @param userName username
    * @return
    */
  override def findUserByName(userName: String): Future[Seq[User]] = db.run {
    val action = userQuery.filter(_.name === userName).result
    logger.info(s"Find user by name SQL statements: ${action.statements}")
    action
  }

  /**
    * Find user by user ID.
    *
    * @param id userID
    * @return
    */
  override def findUserById(id: Long): Future[User] = db.run {
    val action = userQuery.filter(_.id === id).result.head
    logger.info(s"Find user by ID SQL statements: ${action.statements}")
    action
  }

  /**
    * Find users
    * @return
    */
  override def users(): Future[Seq[User]] = db.run {
    val action = userQuery.result
    logger.info(s"Find users SQL statements: ${action.statements}")
    action
  }

  /**
    * User table.
    *
    * @param tag name of table which in the database
    */
  private class UserTable(tag: Tag) extends Table[User](tag, "USER") {
    def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def password = column[String]("PASSWORD")

    def fullName = column[Option[String]]("FULL_NAME")

    def email = column[Option[String]]("EMAIL")

    def isAdmin = column[Boolean]("IS_ADMIN")

    def age = column[Option[Int]]("AGE")

    override def * : ProvenShape[User] =
      (id, name, password, fullName, email, isAdmin, age) <>
      ((User.apply _).tupled, User.unapply)
  }
}
