package controllers

import db.DBUtil
import javax.inject._
import models.Item
import play.api.libs.json.Json
import play.api.mvc._
import play.api.db._


/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(db: Database, val controllerComponents: ControllerComponents) extends BaseController {


  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def test(): Action[AnyContent] = Action {
    Ok(Json.toJson(List(1, 2)))
  }

  def items(): Action[AnyContent] = Action {
    val items = DBUtil.getAllItems(db)
    Ok(Json.toJson(items))
  }

  def item(id: Int): Action[AnyContent] = Action {
    val item = DBUtil.getItemById(db, id)
    Ok(Json.toJson(item))
  }
}
