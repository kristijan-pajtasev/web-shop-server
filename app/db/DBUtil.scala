package db

import anorm.{SQL, ~}
import anorm.SqlParser.{int, str}
import models.Product
import play.api.db.Database
import anorm._
import org.joda.time.DateTime

/**
  * Created by Kristijan Pajtasev
  * 07/04/2019.
  */
object DBUtil {
  def getAllProducts(db: Database, page: Int): List[Product] = {
    val offset = page * 10
    db.withConnection { implicit c =>
      val res2 =
        SQL(s"SELECT * FROM test LIMIT 10 OFFSET $offset")
          .as((int("id") ~ str("productName")).*)
          .map {
            case n ~ p => Product(n, p, 1, 1, "", DateTime.now(), "", true, 1)
          } // TODO complete
      res2
    }
  }

  def getProductById(db: Database, id: Int): Product = {
    db.withConnection { implicit c =>
      val res2 =
        SQL(s"SELECT * FROM test WHERE id=$id")
          .as((int("id") ~ str("productName")).*)
          .map {
            case n ~ p => Product(n, p, 1, 1, "", DateTime.now(), "", true, 1)
          } // TODO complete
      res2.head
    }
  }

}
