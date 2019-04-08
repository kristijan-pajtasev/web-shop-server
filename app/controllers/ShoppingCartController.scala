package controllers

import db.DBUtil
import javax.inject._
import play.api.db._
import play.api.libs.json.Json
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class ShoppingCartController @Inject()(
    db: Database,
    val controllerComponents: ControllerComponents)
    extends BaseController {

  def shoppingCart(): Action[AnyContent] = Action {
    val products = DBUtil.getShoppingCart(db, 1) // TODO: use customer id
    Ok(Json.toJson(products))
  }
}
