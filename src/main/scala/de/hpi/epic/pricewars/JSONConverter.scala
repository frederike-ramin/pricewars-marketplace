package de.hpi.epic.pricewars

import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object JSONConverter extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val shippingTimeFormat = jsonFormat2(ShippingTime.apply)
  implicit val offerFormat = jsonFormat10(Offer.apply)
  implicit val offerPatchFormat = jsonFormat8(OfferPatch)
  implicit val merchantFormat = jsonFormat4(Merchant.apply)
  implicit val consumerFormat = jsonFormat4(Consumer.apply)
  implicit val productFormat = jsonFormat3(Product.apply)
  implicit val buyRequestFormat = jsonFormat4(BuyRequest)
  //TODO: write generic formatter
  implicit val offerFailureFormat = jsonFormat2(Failure[Offer])
  implicit val offerSeqFailureFormat = jsonFormat2(Failure[Seq[Offer]])
  implicit val merchantFailureFormat = jsonFormat2(Failure[Merchant])
  implicit val merchantSeqFailureFormat = jsonFormat2(Failure[Seq[Merchant]])
  implicit val consumerFailureFormat = jsonFormat2(Failure[Consumer])
  implicit val consumerSeqFailureFormat = jsonFormat2(Failure[Seq[Consumer]])
  implicit val productFailureFormat = jsonFormat2(Failure[Product])
  implicit val productSeqFailureFormat = jsonFormat2(Failure[Seq[Product]])
  implicit val unitFailureFormat = jsonFormat2(Failure[Unit])
}
