package controllers

import javax.inject._
import models.ScalaApplicationDatabase
import play.api._
import play.api.db.Database
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val db: Database,
                               val cc: MessagesControllerComponents,
                               val dbService: ScalaApplicationDatabase)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(): Action[AnyContent] = Action { implicit request =>
    request.session.get("email") match {
      case Some(email) => Ok(views.html.index())
      case None => Redirect(routes.LoginController.login())
    }
  }

  def testDatabase: Action[AnyContent] = Action.async { implicit request =>
    dbService.showTables().map(x => Ok(views.html.tables(x)))
  }

  def testAnorm: Action[AnyContent] = Action.async { implicit request =>
    dbService.showTablesAnorm().map(x => Ok(views.html.tables(x.map(_.name))))
  }

  def testUser = Action.async { implicit request =>
    dbService.getUser().map {
      case Some(id) => Ok(id.toString)
      case None => Ok("Not exist")
    }
  }
}
