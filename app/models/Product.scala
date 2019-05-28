package models

import org.joda.time.DateTime
import play.api.libs.json._

/**
  * Created by Kristijan Pajtasev
  * 04/04/2019.
  */
//ProductID;ProductName;Price;CategoryID;Class;ModifyDate;Resistant;IsAllergic;VitalityDays

case class Product(product_id: String,
                   product_category_name: String,
                   product_name_length: Int,
                   product_description_lenght: Int,
                   product_photos_qty: Int,
                   product_weight_g: Int,
                   product_length_cm: Int,
                   product_height_cm: Int,
                   product_width_cm: Int)

object Product {

  implicit object ItemFormat extends Format[Product] {

    // convert from Item object to JSON (serializing to JSON)
    def writes(item: Product): JsValue = {
      val itemSeq = Seq(
        "product_id" -> JsString(item.product_id),
        "product_category_name" -> JsString(item.product_category_name),
        "product_name_length" -> JsNumber(item.product_name_length),
        "product_description_lenght" -> JsNumber(
          item.product_description_lenght),
        "product_photos_qty" -> JsNumber(item.product_photos_qty),
        "product_weight_g" -> JsNumber(item.product_weight_g),
        "product_length_cm" -> JsNumber(item.product_length_cm),
        "product_height_cm" -> JsNumber(item.product_height_cm),
        "product_width_cm" -> JsNumber(item.product_width_cm)
      )
      JsObject(itemSeq)
    }

    // convert from JSON string to a Item object (de-serializing from JSON)
    // (i don't need this method; just here to satisfy the api)
    def reads(json: JsValue): JsResult[Product] = {
      JsSuccess(Product("1", "", 1, 1, 1, 1, 1, 1, 1))
    }

  }

}
