package models

import org.joda.time.DateTime
import play.api.libs.json._

/**
  * Created by Kristijan Pajtasev
  * 04/04/2019.
  */
//ProductID;ProductName;Price;CategoryID;Class;ModifyDate;Resistant;IsAllergic;VitalityDays

case class Product(id: Int,
                   productName: String,
                   price: Double,
                   categoryId: Int,
                   klass: String,
                   modifyDate: DateTime,
                   resistant: Option[String],
                   isAllergic: Option[Boolean],
                   vitalityDays: Option[Int])

object Product {

  implicit object ItemFormat extends Format[Product] {

    // convert from Item object to JSON (serializing to JSON)
    def writes(item: Product): JsValue = {
      val isAllergic = item.isAllergic match {
        case Some(i) => i.toString
        case _       => ""
      }
      val vitalityDays = item.vitalityDays match {
        case Some(i) => i.toString
        case _       => ""
      }

      val itemSeq = Seq(
        "id" -> JsNumber(item.id),
        "productName" -> JsString(item.productName),
        "price" -> JsNumber(item.price),
        "categoryId" -> JsNumber(item.categoryId),
        "klass" -> JsString(item.klass),
        "modifyDate" -> JsString(item.modifyDate.toString()),
        "resistant" -> JsString(item.resistant.getOrElse("")),
        "isAllergic" -> JsString(isAllergic),
        "vitalityDays" -> JsString(vitalityDays)
      )
      JsObject(itemSeq)
    }

    // convert from JSON string to a Item object (de-serializing from JSON)
    // (i don't need this method; just here to satisfy the api)
    def reads(json: JsValue): JsResult[Product] = {
      JsSuccess(
        Product(1,
                "",
                1,
                1,
                "",
                DateTime.now(),
                Option(""),
                Option(true),
                Option(1)))
    }

  }

}
