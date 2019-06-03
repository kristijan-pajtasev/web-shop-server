package db

import anorm.{SQL, ~}
import anorm.SqlParser.{int, str}
import models.{CartProduct, Product, ProductWrapper, ShoppingItem}
import play.api.db.Database
import anorm._
import java.util.UUID.randomUUID

import db.DBUtil.getAllProducts

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
        val orderId = randomUUID().toString
        addOrder(db,
                 customerId,
                 Product.fromCartProduct(product),
                 index,
                 orderId)
        index = index + 1
      })
      emptyCart(db, customerId)
    }
  }

  def emptyCart(db: Database, customerId: Int) = {
    db.withConnection { implicit c =>
      SQL(s"""DELETE FROM olist.shopping_cart WHERE customer_id=$customerId""")
        .executeUpdate()
    }
  }

  def addOrder(db: Database,
               customerId: Int,
               product: Product,
               index: Int,
               orderId: String) = {
    db.withConnection { implicit c =>
      val sql =
        s"""
           INSERT INTO olist.orders(
               order_id,
               order_item_id,
               product_id,
               seller_id,
               shipping_limit_date,
               price,
               freight_value)
           VALUES ('${orderId}', ${index}, '${product.product_id}', '${randomUUID()}', NOW(), 0, 0)"""
      SQL(sql)
        .executeInsert(SqlParser.scalar[String].singleOpt)
    }
  }

  def getQueryString(query: String): String = {
    if (query.matches("\\d+") || query.matches("\\d+\\.\\d+"))
      s"""WHERE 
      product_weight_g=$query OR
      product_length_cm=$query OR
      product_height_cm=$query OR
      product_width_cm=$query"""
    else
      s"""WHERE product_category_name='$query' OR
      product_id='$query'"""
  }

  def getAllProducts(db: Database, page: Int): List[Product] =
    getAllProducts(db, page, "")

  def getRecommendedProducts(db: Database): List[Product] =
    getAllProducts(db, 1)

  def getTotalProductsCount(db: Database, search: String): Int = {
    db.withConnection { implicit c =>
      val sql =
        if (search.equals("")) s"SELECT count(*) as total FROM olist.products"
        else {
          val query = getQueryString(search)
          s"SELECT count(*) as total FROM olist.products $query"
        }
      val res2 =
        SQL(sql)
          .as(int("total").*)
          .head
      return res2
    }
  }

  def getAllProductsWithTotal(db: Database,
                              page: Int,
                              search: String): ProductWrapper = {
    val products = getAllProducts(db, page, search)
    val total = getTotalProductsCount(db, search)
    ProductWrapper(products, total, page + 1)
  }

  def getAllProducts(db: Database, page: Int, search: String): List[Product] = {
    val offset = page * 10
    db.withConnection { implicit c =>
      val sql =
        if (search.equals(""))
          s"SELECT * FROM olist.products LIMIT 10 OFFSET $offset"
        else {
          val query = getQueryString(search)
          s"SELECT * FROM olist.products $query  LIMIT 10 OFFSET $offset"
        }
      val res2 =
        SQL(sql)
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

  def getShoppingCart(db: Database, customer_id: Int): List[CartProduct] = {
    db.withConnection { implicit c =>
      val res2 =
        SQL(s"""
          SELECT olist.products.*, olist.shopping_cart.amount
          FROM olist.shopping_cart
          JOIN olist.products
          ON olist.products.product_id=olist.shopping_cart.product_id
          WHERE olist.shopping_cart.customer_id=$customer_id""")
          .as(
            (str("product_id") ~
              str("product_category_name") ~
              int("product_name_length") ~
              int("product_description_lenght") ~
              int("product_photos_qty") ~
              int("product_weight_g") ~
              int("product_length_cm") ~
              int("product_height_cm") ~
              int("product_width_cm") ~
              int("amount")).*)
          .map {
            case product_id ~
                  product_category_name ~
                  product_name_length ~
                  product_description_lenght ~
                  product_photos_qty ~
                  product_weight_g ~
                  product_length_cm ~
                  product_height_cm ~
                  product_width_cm ~
                  amount =>
              CartProduct(
                product_id,
                product_category_name,
                product_name_length,
                product_description_lenght,
                product_photos_qty,
                product_weight_g,
                product_length_cm,
                product_height_cm,
                product_width_cm,
                amount
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
            DELETE FROM olist.shopping_cart
            WHERE customer_id=${item.customer_id}
              AND product_id='${item.product_id}'""")
          .executeUpdate()
    }
    getProductById(db, item.product_id)
  }

  def getProductsByIds(db: Database, ids: List[String]): List[Product] = {
    ids.map(id => getProductById(db, id))
  }
}
