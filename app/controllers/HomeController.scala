package controllers

import javax.inject._
import models.Item
import play.api.libs.json.Json
import play.api.mvc._
import play.api.db._
import anorm._
import anorm.SqlParser._


/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(db: Database, val controllerComponents: ControllerComponents) extends BaseController {
  db.withConnection { implicit c =>
    val res2 = SQL("SELECT * FROM test").as((int("id") ~ str("name")).*).map { case n ~ p => Item(n, p) }
    res2
  }

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def test() = Action {
    Ok(Json.toJson(List(1, 2)))
  }

  def items() = Action {
    Ok(Json.toJson(List(Item(1, "test display"))))
  }

  def item(id: Int) = Action {
    Ok(Json.toJson(Item(id, "test display")))
  }
}
