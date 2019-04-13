package models

import play.api.data._
import play.api.data.Forms._

/**
  * Created by Kristijan Pajtasev
  * 04/04/2019.
  */
case class ShoppingItem(customer_id: Int, product_id: Int);

object ShoppingItem {
  val shoppingItemForm = Form(
    mapping(
      "customer_id" -> number,
      "product_id" -> number
    )(ShoppingItem.apply)(ShoppingItem.unapply)
  )
}
