package models

import play.api.libs.ws._
import javax.inject.Inject

import scala.concurrent.Await._
import play.api.Play.current
import play.api.libs.json.{Format, Json, Reads}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
/**
  * Created by mukul on 28/4/17.
  */
class UMLSCodeMap @Inject() (ws: WSClient) {
  def getUMLSCodeMap(entity: String): List[(String, String)] = {
    val lowerCaseEntity = entity.toLowerCase
    val stringQuery = s"SELECT code, text, source FROM umls_mrconso WHERE text LIKE '%$lowerCaseEntity%' LIMIT 20"
    val responseObject = ws
      .url("http://localhost:19200/_sql")
      .withHeaders("Content-Type" -> "text/plain")
      .post(stringQuery)

    implicit val formats = Json.format[ESResponse]

    (result(responseObject, Duration.Inf).json \ "hits" \"hits").as[List[ESResponse]].map { x =>
      val source = x._source.getOrElse("source", "")
      val code = x._source.getOrElse("code", "")
      println(source, code)
      (source, code)
    }
  }
}

object UMLSCodeMap{
  val umlsCodeMap = new UMLSCodeMap(WS.client)
  def apply(): UMLSCodeMap = umlsCodeMap
}

case class ESResponse(_index: String, _type: String, _id: String, _score: Int, _source: Map[String, String])