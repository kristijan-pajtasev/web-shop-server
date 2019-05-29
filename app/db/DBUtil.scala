package db

import anorm.{SQL, ~}
import anorm.SqlParser.{int, str}
import models.{Product, ShoppingItem}
import play.api.db.Database
import anorm._
import org.joda.time.DateTime

/**
  * Created by Kristijan Pajtasev
  * 07/04/2019.
  */
object DBUtil {
  def getRecommendedProducts(db: Database): List[Product] =
    getAllProducts(db, 1)

  def getAllProducts(db: Database, page: Int): List[Product] = {
    val offset = page * 10
    db.withConnection { implicit c =>
      val res2 =
        SQL(s"SELECT * FROM olist.products LIMIT 10 OFFSET $offset")
          .as(
            (
              str("product_id") ~
                str("product_category_name") ~
                int("product_name_length") ~
                int("product_description_lenght") ~
                int("product_photos_qty") ~
                int("product_weight_g") ~
                int("product_length_cm") ~
                int("product_height_cm") ~
                int("product_width_cm")
            ).*)
          .map {
            case product_id ~
                  product_category_name ~
                  product_name_length ~
                  product_description_lenght ~
                  product_photos_qty ~
                  product_weight_g ~
                  product_length_cm ~
                  product_height_cm ~
                  product_width_cm =>
              Product(
                product_id,
                product_category_name,
                product_name_length,
                product_description_lenght,
                product_photos_qty,
                product_weight_g,
                product_length_cm,
                product_height_cm,
                product_width_cm
              )
          }
      res2
    }
  }

  def getProductById(db: Database, id: String): Product = {
    db.withConnection { implicit c =>
      val res2 =
        SQL(s"SELECT * FROM olist.products WHERE product_id='$id'")
          .as(
            (
              str("product_id") ~
                str("product_category_name") ~
                int("product_name_length") ~
                int("product_description_lenght") ~
                int("product_photos_qty") ~
                int("product_weight_g") ~
                int("product_length_cm") ~
                int("product_height_cm") ~
                int("product_width_cm")
            ).*)
          .map {
            case product_id ~
                  product_category_name ~
                  product_name_length ~
                  product_description_lenght ~
                  product_photos_qty ~
                  product_weight_g ~
                  product_length_cm ~
                  product_height_cm ~
                  product_width_cm =>
              Product(
                product_id,
                product_category_name,
                product_name_length,
                product_description_lenght,
                product_photos_qty,
                product_weight_g,
                product_length_cm,
                product_height_cm,
                product_width_cm
              )
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
            (str("product_id") ~
              str("product_category_name") ~
              int("product_name_length") ~
              int("product_description_lenght") ~
              int("product_photos_qty") ~
              int("product_weight_g") ~
              int("product_length_cm") ~
              int("product_height_cm") ~
              int("product_width_cm")).*)
          .map {
            case product_id ~
                  product_category_name ~
                  product_name_length ~
                  product_description_lenght ~
                  product_photos_qty ~
                  product_weight_g ~
                  product_length_cm ~
                  product_height_cm ~
                  product_width_cm =>
              Product(
                product_id,
                product_category_name,
                product_name_length,
                product_description_lenght,
                product_photos_qty,
                product_weight_g,
                product_length_cm,
                product_height_cm,
                product_width_cm
              )
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
