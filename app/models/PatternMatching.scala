package models
import scala.util.matching.Regex
import scala.collection.JavaConversions._
/**
  * Created by Mukul Dev on 23/4/17.
  */
object PatternMatching {
  def getValueApendedMap(inputClinicalText: String) = {
    val clinicalTextArray = inputClinicalText.split('.')
    var padLength = 0
    clinicalTextArray.flatMap{ clinicalText =>
      val pattern = new Regex("""\d+""")
      val valueString = pattern.findAllIn(clinicalText).mkString(",")
      val valueMap =  mapAsJavaMap(Map("value" -> valueString))
      val dummyString = "".padTo(padLength, ' ')
      val ctakesOutputList = CTakesLocal().getCodeMap(dummyString + clinicalText)
      padLength = padLength + clinicalText.length + 1
      ctakesOutputList.map{map =>
        mapAsJavaMap(map ++ valueMap)
      }
    }
  }
  def getLVEFOutputMap(inputClinicalText: String) ={
    val clinicalTextArray = inputClinicalText.split('.')
    var padLength = 0
    val outputMap = clinicalTextArray.map{ct =>
      val dummyString = "".padTo(padLength, ' ')
      val clinicalText = dummyString + ct
      if (clinicalText.toLowerCase.indexOf("lvef") >= 0){
        val startIndex = clinicalText.toLowerCase.indexOf("lvef")
        val endIndex = clinicalText.toLowerCase.indexOf("lvef") + "lvef".length
        val positionMap = mapAsJavaMap(Map("start" -> startIndex.toString, "end" -> endIndex.toString))
        val entity = clinicalText.substring(startIndex, endIndex)
        val pattern = new Regex("""\d+""")
        val valueString = pattern.findAllIn(clinicalText).mkString(",")
        val tempMap = Map("entity" -> entity, "entity_type" -> "Entity", "polarity" -> "Positive", "subject" -> "Patient", "position" -> positionMap, "LNC256" -> "10230-1", "value" -> valueString, "preferredText" -> "Left ventricular ejection fraction")
        padLength = padLength + ct.length + 1
        mapAsJavaMap(tempMap)
      }
      else {
        padLength = padLength + ct.length + 1
        mapAsJavaMap(Map())
      }
    }
    outputMap.filter(_.nonEmpty)
  }
}