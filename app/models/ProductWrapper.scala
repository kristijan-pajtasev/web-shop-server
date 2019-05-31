package models

import org.joda.time.DateTime
import play.api.libs.json._

/**
  * Created by Kristijan Pajtasev
  * 04/04/2019.
  */
//ProductID;ProductName;Price;CategoryID;Class;ModifyDate;Resistant;IsAllergic;VitalityDays

case class ProductWrapper(products: List[Product], total: Int, page: Int)

object ProductWrapper {

  implicit object ItemFormat extends Format[ProductWrapper] {

    // convert from Item object to JSON (serializing to JSON)
    def writes(item: ProductWrapper): JsValue = {
      val itemSeq = Seq(
        "products" -> JsArray(
          item.products.map(product => Product.ItemFormat.writes(product))),
        "total" -> JsNumber(item.total),
        "page" -> JsNumber(item.page)
      )
      JsObject(itemSeq)
    }

    // convert from JSON string to a Item object (de-serializing from JSON)
    // (i don't need this method; just here to satisfy the api)
    def reads(json: JsValue): JsResult[ProductWrapper] = {
      JsSuccess(ProductWrapper(List(), 1, 1))
    }
  }

}
