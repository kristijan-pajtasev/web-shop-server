package models

import org.joda.time.DateTime
import play.api.libs.json._

/**
  * Created by Kristijan Pajtasev
  * 04/04/2019.
  */
//ProductID;ProductName;Price;CategoryID;Class;ModifyDate;Resistant;IsAllergic;VitalityDays

case class Item(id: Int,
                productName: String,
                price: Float,
                categoryId: Int,
                klass: String,
                modifyDate: DateTime,
                resistant: String,
                isAllergic: Boolean,
                vitalityDays: Int)

object Item {

  implicit object ItemFormat extends Format[Item] {

    // convert from Item object to JSON (serializing to JSON)
    def writes(item: Item): JsValue = {
      val itemSeq = Seq(
        "id" -> JsNumber(item.id),
        "productName" -> JsString(item.productName)
      )
      JsObject(itemSeq)
    }

    // convert from JSON string to a Item object (de-serializing from JSON)
    // (i don't need this method; just here to satisfy the api)
    def reads(json: JsValue): JsResult[Item] = {
      JsSuccess(Item(1, "", 1, 1, "", DateTime.now(), "", true, 1))
    }

  }

}
