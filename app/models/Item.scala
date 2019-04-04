package models

import play.api.libs.json._

/**
  * Created by Kristijan Pajtasev 
  * 04/04/2019.
  */
case class Item(id: Int, displayName: String);

object Item {

  implicit object ItemFormat extends Format[Item] {

    // convert from Item object to JSON (serializing to JSON)
    def writes(item: Item): JsValue = {
      //  itemSeq == Seq[(String, play.api.libs.json.JsString)]
      val itemSeq = Seq(
        "id" -> JsNumber(item.id),
        "displayName" -> JsString(item.displayName)
      )
      JsObject(itemSeq)
    }

    // convert from JSON string to a Item object (de-serializing from JSON)
    // (i don't need this method; just here to satisfy the api)
    def reads(json: JsValue): JsResult[Item] = {
      JsSuccess(Item(1, ""))
    }

  }

}