package db

import anorm.{SQL, ~}
import anorm.SqlParser.{int, str}
import models.Item
import play.api.db.Database
import anorm._
import anorm.SqlParser._

/**
  * Created by Kristijan Pajtasev 
  * 07/04/2019.
  */
object DBUtil {
  def getAllItems(db: Database) :List[Item] = {
    db.withConnection { implicit c =>
      val res2 = SQL("SELECT * FROM test").as((int("id") ~ str("name")).*).map { case n ~ p => Item(n, p) }
      res2
    }
  }

  def getItemById(db: Database, id: Int) :Item = {
    db.withConnection { implicit c =>
      val res2 = SQL(s"SELECT * FROM test WHERE id=$id").as((int("id") ~ str("name")).*).map { case n ~ p => Item(n, p) }
      res2.head
    }
  }

}
