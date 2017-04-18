package models
import scala.collection.JavaConversions._
/**
  * Created by mukul on 19/4/17.
  */
object PatternMatch {
  def getPatternMatchMap(clinicalText: String) ={
    val patternList = List("lvef",
      "lv",
      "left ventricular ejection fraction",
      "left ventricle ejection fraction",
      "left ventricle",
      "low ejection fraction",
      "cardiac ejection fraction",
      "ejection fraction",
      "left ventricular ejaculation")

    val outputMap = patternList.map{pattern =>
      if (clinicalText.toLowerCase.indexOf(pattern) >= 0){
        val startIndex = clinicalText.toLowerCase.indexOf(pattern)
        val endIndex = clinicalText.toLowerCase.indexOf(pattern) + pattern.length
        val positionMap = mapAsJavaMap(Map("start" -> startIndex.toString, "end" -> endIndex.toString))
        val entity = clinicalText.substring(startIndex, endIndex)
        val tempMap = Map("entity" -> entity, "entity_type" -> "Disease Disorder", "polarity" -> "Positive", "subject" -> "Patient", "position" -> positionMap, "LOINC" -> "10230-1, 18043-0, 18044-8, 18045-5, 18046-3, 18047-1, 18048-9, 18049-7, 8806-2, 8807-0, 8808-8, 8809-6, 8810-4, 8811-2, 8812-0")
        mapAsJavaMap(tempMap)
      }
      else
        mapAsJavaMap(Map())
    }
    outputMap.filter(_.nonEmpty)
  }
}