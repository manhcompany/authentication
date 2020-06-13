package models

import anorm._
import javax.inject.{Inject, Singleton}
import play.api.db.Database

import scala.concurrent.Future

@Singleton
class ScalaApplicationDatabase @Inject()(db: Database, dec: DatabaseExecutionContext) {

  val parser: RowParser[Tables] = Macro.indexedParser[Tables]
  def showTables(): Future[Seq[String]] = {
    Future {
      db.withConnection { conn =>
        var results = Seq[String]()
        val stmt = conn.createStatement
        val rs = stmt.executeQuery("show tables;")
        while (rs.next()) {
          results = s"${rs.getString(1)}" +: results
        }
        results
      }
    }(dec)
  }

  def showTablesAnorm(): Future[List[Tables]] = {
    Future {
      db.withConnection { implicit c =>
        val x = SQL("show tables;").as(parser.*)
        x
      }
    }(dec)
  }

  def getUser() = {
    Future {
      db.withConnection { implicit conn =>
        SQL(
          """
            |select email, id_user
            |from   users
            |where  email = {email} and password = {password} and status = 1
            |""".stripMargin
        ).on("email" -> "manh", "password" -> "doi").as(SqlParser.long("id_user").singleOpt)
      }
    }(dec)
  }
}