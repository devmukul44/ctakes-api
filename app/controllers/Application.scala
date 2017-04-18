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
  }

  def postTest = Action { implicit request =>
    val clinicalJson = request.body.asJson.get
    val clinicalText = (clinicalJson \ "text").as[String]
    val outputMapList = CTakesLocal().getCodeMap(clinicalText)
    println(outputMapList)
    val schemaMapArray = CTakesLocal().getSchemaMap
    val outputMap = mapAsJavaMap(Map("data" -> outputMapList.toArray, "schema" -> schemaMapArray))
    Ok(Json.stringify(Json.toJson(outputMap)))
  }
}