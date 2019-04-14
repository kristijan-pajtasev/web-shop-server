package controllers

import db.DBUtil
import javax.inject._
import models.ShoppingItem
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

//  fetch("http://localhost:9000/shopping-cart", {method: "post", headers: {
//    "Content-Type": "application/json",
//    //             "Content-Type": "application/x-www-form-urlencoded",
//  }, body: JSON.stringify({product_id: 1, customer_id: 1})})

  def addToCart =
    Action(parse.form(ShoppingItem.shoppingItemForm)) { implicit request =>
      {
        val itemData = request.body
        val item = ShoppingItem(itemData.customer_id, itemData.product_id)
        val product = DBUtil.addToShoppingCart(db, item)
        Ok(Json.toJson(product))
      }
    }
}
