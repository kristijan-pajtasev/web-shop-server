package db

import anorm.{SQL, ~}
import anorm.SqlParser.{int, str}
import models.{Product, ProductWrapper, ShoppingItem}
import play.api.db.Database
import anorm._
import java.util.UUID.randomUUID

/**
  * Created by Kristijan Pajtasev
  * 07/04/2019.
  */
object DBUtil {
  def purchase(db: Database, customerId: Int) = {
    db.withConnection { implicit c =>
      val shoppingCart = getShoppingCart(db, customerId)
      var index = 1
      shoppingCart.foreach(product => {
        addOrder(db, customerId, product, index)
        index = index + 1
      })
      emptyCart(db, customerId)
    }
  }

  def emptyCart(db: Database, customerId: Int) = {
    db.withConnection { implicit c =>
      SQL(s"""DELETE * FROM olist.orders WHERE customer_id='$customerId'""")
        .executeQuery()
    }
  }

  def addOrder(db: Database, customerId: Int, product: Product, index: Int) = {
    db.withConnection { implicit c =>
      SQL(s"""
           |INSERT INTO olist.orders(
           |    order_id
           |    order_item_id
           |    product_id
           |    seller_id
           |    shipping_limit_date
           |    price
           |    freight_value)
           |VALUES (${randomUUID()}, $index,'${product.product_id}', ${randomUUID()}, NOW(), 0, 0)""")
        .executeInsert()
    }
  }

  def getRecommendedProducts(db: Database): List[Product] =
    getAllProducts(db, 1)

  def getTotalProductsCount(db: Database): Int = {
    db.withConnection { implicit c =>
      val res2 =
        SQL(s"SELECT count(*) as total FROM olist.products")
          .as(int("total").*)
          .head
      return res2
    }
  }

  def getAllProductsWithTotal(db: Database, page: Int): ProductWrapper = {
    val products = getAllProducts(db, page)
    val total = getTotalProductsCount(db)
    ProductWrapper(products, total, page + 1)
  }

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
            INSERT INTO olist.shopping_cart(customer_id, product_id, amount)
            VALUES (${item.customer_id}, '${item.product_id}', 1)
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
