package controllers

import java.util
import java.util.HashMap

import play.api.mvc.{Action, Controller}
import play.libs.Json
import models.CTakesLocal
import play.api.libs.json.{JsObject, JsResult, JsValue}
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

  def postTest = Action { implicit request =>
    val clinicalJson = request.body.asJson.get
    val clinicalText = (clinicalJson \ "text").as[String]
    val outputMapList = CTakesLocal().getCodeMap(clinicalText)
    println(outputMapList)
    val schemaMapArray = CTakesLocal().getSchemaMap
    val outputMap = mapAsJavaMap(Map("data" -> outputMapList.toArray, "schema" -> schemaMapArray))
    Ok(Json.stringify(Json.toJson(outputMap)))
      .withHeaders("Access-Control-Allow-Origin" -> "*",
      "Access-Control-Expose-Headers" -> "WWW-Authenticate, Server-Authorization",
      "Access-Control-Allow-Methods" -> "POST, GET, OPTIONS, PUT, DELETE",
      "Access-Control-Allow-Headers" -> "x-requested-with,content-type,Cache-Control,Pragma,Date,Authorization")
  }
}