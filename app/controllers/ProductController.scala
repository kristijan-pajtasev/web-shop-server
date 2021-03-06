package controllers

import db.DBUtil
import javax.inject._
import models.Product
import play.api.libs.json.Json
import play.api.mvc._
import play.api.db._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class ProductController @Inject()(
    db: Database,
    val controllerComponents: ControllerComponents)
    extends BaseController {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(views.html.index())
  }

  def products(page: Int, search: String): Action[AnyContent] = Action {
    val products = DBUtil.getAllProductsWithTotal(db, page - 1, search)
    Ok(Json.toJson(products))
  }

  def product(id: String): Action[AnyContent] = Action {
    val product = DBUtil.getProductById(db, id)
    Ok(Json.toJson(product))
  }
}
