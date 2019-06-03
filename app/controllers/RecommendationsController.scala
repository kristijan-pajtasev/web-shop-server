package controllers

import scala.util.{Failure, Success}
import db.DBUtil
import javax.inject._
import play.api.db._
import play.api.libs.json.Json
import play.api.libs.ws
import play.api.mvc._
import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class RecommendationsController @Inject()(
    db: Database,
    ws: WSClient,
    ec: ExecutionContext,
    val controllerComponents: ControllerComponents)
    extends BaseController {

  def similar(productId: String) = Action.async {
    val idsFuture: Future[Result] = ws
      .url("http://127.0.0.1:8000/similar/1e9e8ef04dbcff4541ed26657ea517e5")
      .get()
      .map { response =>
        val ids = (response.json \ "ids").as[List[String]]
        Ok(Json.toJson(DBUtil.getProductsByIds(db, ids)))

      }
    idsFuture
  }
}
