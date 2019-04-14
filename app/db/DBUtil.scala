package db

import anorm.{SQL, ~}
import anorm.SqlParser.{bool, double, get, int, long, str}
import models.{Product, ShoppingItem}
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
              String]]("resistant") ~ get[Option[Boolean]]("isAllergic") ~ get[
              Option[Int]]("vitalityDays")).*)
          .map {
            case i ~ p ~ price ~ categoryId ~ klass ~ modifyDate ~ resistant ~ isAllergic ~ vitalityDays =>
              Product(i,
                      p,
                      price,
                      categoryId,
                      klass,
                      new DateTime(modifyDate),
                      resistant,
                      isAllergic,
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
              String]]("resistant") ~ get[Option[Boolean]]("isAllergic") ~ get[
              Option[Int]]("vitalityDays")).*)
          .map {
            case i ~ p ~ price ~ categoryId ~ klass ~ modifyDate ~ resistant ~ isAllergic ~ vitalityDays =>
              Product(i,
                      p,
                      price,
                      categoryId,
                      klass,
                      new DateTime(modifyDate),
                      resistant,
                      isAllergic,
                      vitalityDays)
          }
      res2.head
    }
  }

  def getShoppingCart(db: Database, customer_id: Int): List[Product] = {
    db.withConnection { implicit c =>
      val res2 =
        SQL(s"""
          SELECT product.*
          FROM shopping_cart
          JOIN product
          ON product.id=shopping_cart.customer_id
          WHERE shopping_cart.customer_id=$customer_id""")
          .as(
            (int("id") ~ str("productName") ~ double("price") ~ int(
              "categoryId") ~ str("klass") ~ long("modifyDate") ~ get[Option[
              String]]("resistant") ~ get[Option[Boolean]]("isAllergic") ~ get[
              Option[Int]]("vitalityDays")).*)
          .map {
            case i ~ p ~ price ~ categoryId ~ klass ~ modifyDate ~ resistant ~ isAllergic ~ vitalityDays =>
              Product(i,
                      p,
                      price,
                      categoryId,
                      klass,
                      new DateTime(modifyDate),
                      resistant,
                      isAllergic,
                      vitalityDays)
          }
      res2
    }
  }

  def addToShoppingCart(db: Database, item: ShoppingItem): Product = {
    db.withConnection { implicit c =>
      val res2 =
        SQL(s"""
            INSERT INTO shopping_cart(customer_id, product_id, amount)
            VALUES (${item.customer_id}, ${item.product_id}, 1)
            ON CONFLICT (customer_id, product_id) DO UPDATE SET amount = shopping_cart.amount + 1""")
          .executeInsert()
    }
    getProductById(db, item.product_id)
  }

  def removeFromShoppingCart(db: Database, item: ShoppingItem): Product = {
    db.withConnection { implicit c =>
      val res2 =
        SQL(s"""
            DELETE FROM shopping_cart
            WHERE customer_id=${item.customer_id}
              AND product_id=${item.product_id}""")
          .executeUpdate()
    }
    getProductById(db, item.product_id)
  }
}
