package models

import anorm.{Macro, ToParameterList}
import javax.inject.Inject
import play.api.db.DBApi
import anorm._


import scala.concurrent.Future

case class User(email: String, password: String, status: Int = 1, id: Long = 0)

object User {
  implicit def toParameters: ToParameterList[User] = Macro.toParameters[User]
}

class UserRepository @Inject() (dbApi: DBApi)(implicit dec: DatabaseExecutionContext) {
  private val db = dbApi.database("default")

  private val parser : RowParser[User] = Macro.namedParser[User]

  def insert(user: User): Future[Option[Long]] = {
    Future {
      db.withConnection { implicit connection =>
        val id: Option[Long] = SQL(
          """
            |insert into  users(email, password, status) values (
            | {email}, {password}, {status}
            |)
            |""".stripMargin).bind(user).executeInsert()
        id
      }
    }(dec)
  }

  def validateUser(user: User): Future[Option[User]] = {
    Future {
      db.withConnection { implicit connection =>
        SQL(
          """
            |select email, id_user as id, password, status
            |from   users
            |where  email = {email} and password = {password} and status = 1
            |""".stripMargin
        ).on("email" -> user.email, "password" -> user.password).as(parser.singleOpt)
      }
    }
  }
}
