package de.hpi.epic.pricewars.connectors

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import de.hpi.epic.pricewars.data.{Merchant, Offer}
import de.hpi.epic.pricewars.services.DatabaseStore
import spray.can.Http
import spray.http.HttpMethods._
import spray.http._

import scala.concurrent.duration._

object MerchantConnector {

  implicit val system: ActorSystem = ActorSystem()
  implicit val timeout: Timeout = Timeout(15.seconds)
  import system.dispatcher // implicit execution context

  val config: Config = ConfigFactory.load
  val remove_merchant = config.getBoolean("remove_merchant_on_notification_error")

  def notifyMerchant(merchant: Merchant, offer_id: Long, amount: Int, price: BigDecimal, offer: Offer) = {
    val json = s"""{"offer_id": $offer_id, "uid": ${offer.uid}, "product_id": ${offer.product_id}, "quality": ${offer.quality}, "amount_sold": $amount, "price_sold": $price, "price": ${offer.price}, "merchant_id": "${merchant.merchant_id.get}", "amount": ${offer.amount}}"""
    val request = (IO(Http) ? HttpRequest(POST,
      Uri(merchant.api_endpoint_url + "/sold"),
      entity = HttpEntity(MediaTypes.`application/json`, json)
    )).mapTo[HttpResponse]
    def errorHandler(): Unit = {
      if (remove_merchant) {
        println("merchant not responding, killing: " + merchant.algorithm_name)
        DatabaseStore.deleteMerchant(merchant.merchant_id.get, delete_with_token = false)
      } else {
        println("merchant not responding, not killing: " + merchant.algorithm_name)
      }
    }
    request.onFailure {
      case t: Throwable =>
        println(t.getMessage)
        println(t)
        errorHandler()
      case _ =>
        errorHandler()
    }
    request.onSuccess{ case HttpResponse(status, _, _, _) => {
      if (status == StatusCodes.PreconditionRequired) {
        println("merchant requested to be deleted: " + merchant.algorithm_name)
        DatabaseStore.deleteMerchant(merchant.merchant_id.get, delete_with_token = false)
      }
    }}
  }
}
