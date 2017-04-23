package controllers

import play.api.mvc.{Action, Controller}
import play.libs.Json
import models.{CTakesLocal, PatternMatching}
import scala.collection.JavaConversions._

object Application extends Controller {

  def index = Action {
    val checkMap = mapAsJavaMap(Map("ping" -> "pong"))
    Ok(Json.stringify(Json.toJson(checkMap)))
      .withHeaders("Access-Control-Allow-Origin" -> "*",
      "Access-Control-Expose-Headers" -> "WWW-Authenticate, Server-Authorization",
      "Access-Control-Allow-Methods" -> "POST, GET, OPTIONS, PUT, DELETE",
      "Access-Control-Allow-Headers" -> "x-requested-with,content-type,Cache-Control,Pragma,Date,Authorization")
  }

  def ctakesRoute = Action { implicit request =>
    val clinicalJson = request.body.asJson.get
    val clinicalText = (clinicalJson \ "text").as[String]
//    val outputMapList = CTakesLocal().getCodeMap(clinicalText)
    val outputMapList = PatternMatching.getValueApendedMap(clinicalText) ++ PatternMatching.getLVEFOutputMap(clinicalText)
    val schemaMapArray = CTakesLocal().getSchemaMap
    println(outputMapList.toList)

    val orderList = List("Procedure", "Disease Disorder", "Medication", "Anatomical Site", "Sign Symptom")
    val orderedOutputMapList = orderList.flatMap{entityType =>
      outputMapList.filter(map => map.get("entity_type").equals(entityType))}
    val filteredOutMapList = outputMapList.filter(map => !orderList.contains(map.get("entity_type")))
    val finalOrderedMapList = orderedOutputMapList ++ filteredOutMapList

    val combinedOutputMap = mapAsJavaMap(Map("data" -> finalOrderedMapList.toArray, "schema" -> schemaMapArray))
    Ok(Json.stringify(Json.toJson(combinedOutputMap)))
      .withHeaders("Access-Control-Allow-Origin" -> "*",
      "Access-Control-Expose-Headers" -> "WWW-Authenticate, Server-Authorization",
      "Access-Control-Allow-Methods" -> "POST, GET, OPTIONS, PUT, DELETE",
      "Access-Control-Allow-Headers" -> "x-requested-with,content-type,Cache-Control,Pragma,Date,Authorization")
  }

  def preflight(all: String) = Action {
    Ok("").withHeaders("Access-Control-Allow-Origin" -> "*", "Allow" -> "*",
      "Access-Control-Allow-Methods" -> "POST, GET, PUT, DELETE, OPTIONS", "Access-Control-Allow-Headers" ->
        "Origin, X-Requested-With, Content-Type, Accept, Referrer, User-Agent, Authorization"
    )
  }
}