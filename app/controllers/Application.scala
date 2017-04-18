package controllers

import play.api.mvc.{Action, Controller}
import play.libs.Json
import models.{CTakesLocal, PatternMatch}

import scala.collection.JavaConversions._
import models.PatternMatch._

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
    val patternMatchMap = PatternMatch.getPatternMatchMap(clinicalText)
    val combinedMapList = outputMapList ++ patternMatchMap
    println(patternMatchMap)
    val schemaMapArray = CTakesLocal().getSchemaMap
    val outputMap = mapAsJavaMap(Map("data" -> combinedMapList.toArray, "schema" -> schemaMapArray))
    Ok(Json.stringify(Json.toJson(outputMap)))
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