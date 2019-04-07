package db

import anorm.{SQL, ~}
import anorm.SqlParser.{int, str}
import models.Item
import play.api.db.Database
import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime

/**
  * Created by Kristijan Pajtasev
  * 07/04/2019.
  */
object DBUtil {
  def getAllItems(db: Database, page: Int): List[Item] = {
    val offset = page * 10
    db.withConnection { implicit c =>
      val res2 =
        SQL(s"SELECT * FROM test LIMIT 10 OFFSET $offset")
          .as((int("id") ~ str("productName")).*)
          .map {
            case n ~ p => Item(n, p, 1, 1, "", DateTime.now(), "", true, 1)
          } // TODO complete
      res2
    }
  }

  def getItemById(db: Database, id: Int): Item = {
    db.withConnection { implicit c =>
      val res2 =
        SQL(s"SELECT * FROM test WHERE id=$id")
          .as((int("id") ~ str("productName")).*)
          .map {
            case n ~ p => Item(n, p, 1, 1, "", DateTime.now(), "", true, 1)
          } // TODO complete
      res2.head
    }
  }

}
