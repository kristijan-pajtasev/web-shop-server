package db

import anorm.{SQL, ~}
import anorm.SqlParser.{int, str, double, bool, long, get}
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
        SQL(s"SELECT * FROM product LIMIT 10 OFFSET $offset")
          .as(
            (int("id") ~ str("productName") ~ double("price") ~ int(
              "categoryId") ~ str("klass") ~ long("modifyDate") ~ get[Option[
              String]]("resistant") ~ get[Option[Boolean]]("isAlergic") ~ get[
              Option[Int]]("vitalityDays")).*)
          .map {
            case i ~ p ~ price ~ categoryId ~ klass ~ modifyDate ~ resistant ~ isAlergic ~ vitalityDays =>
              Product(i,
                      p,
                      price,
                      categoryId,
                      klass,
                      new DateTime(modifyDate),
                      resistant,
                      isAlergic,
                      vitalityDays)
          }
      res2
    }
  }

  def getProductById(db: Database, id: Int): Product = {
    db.withConnection { implicit c =>
      val res2 =
        SQL(s"SELECT * FROM product WHERE id=$id")
          .as(
            (int("id") ~ str("productName") ~ double("price") ~ int(
              "categoryId") ~ str("klass") ~ long("modifyDate") ~ get[Option[
              String]]("resistant") ~ get[Option[Boolean]]("isAlergic") ~ get[
              Option[Int]]("vitalityDays")).*)
          .map {
            case i ~ p ~ price ~ categoryId ~ klass ~ modifyDate ~ resistant ~ isAlergic ~ vitalityDays =>
              Product(i,
                      p,
                      price,
                      categoryId,
                      klass,
                      new DateTime(modifyDate),
                      resistant,
                      isAlergic,
                      vitalityDays)
          }
      res2.head
    }
  }

}
