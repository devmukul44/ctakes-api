package models

import org.apache.uima.fit.factory.{AnalysisEngineFactory, JCasFactory}
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory._
import org.apache.ctakes.typesystem.`type`.textsem.IdentifiedAnnotation
import org.apache.uima.fit.util.JCasUtil

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
/**
  * Created by Mukul Dev on 14/4/17.
  */
class CTakesLocal {
  val pipeline = getFastPipeline()
  val analysisEngineDescription = AnalysisEngineFactory.createEngineDescription(pipeline)
  val engine = AnalysisEngineFactory.createEngine(analysisEngineDescription)
  val jcasInstance = JCasFactory.createJCas()

  def getCodeMap(clinicalText: String) = {
    jcasInstance.setDocumentText(clinicalText)
    engine.process(jcasInstance)
    val identifiedAnnotationList = JCasUtil.select(jcasInstance, new IdentifiedAnnotation(jcasInstance).getClass).iterator().asScala.toList
    val outputMap = identifiedAnnotationList.filter(annotation => annotation.getOntologyConceptArr != null)
      .map { annotation =>
        val coveredText = annotation.getCoveredText
        val textType = annotation.getType.getShortName match {
          case "ProcedureMention" => "Procedure"
          case "DiseaseDisorderMention" => "Disease Disorder"
          case "MedicationMention" => "Medication"
          case "AnatomicalSiteMention" => "Anatomical Site"
          case "SignSymptomMention" => "Sign Symptom"
          case "EntityMention" => "Entity"
          case _ => annotation.getType.getShortName
        }
        val polarity = if (annotation.getPolarity.toString == "-1") "Negative" else "Positive"
        val subject = annotation.getSubject match {
          case "patient" => "Patient"
          case "family_member" => "Family Member"
          case "other" => "Other"
          case _ => annotation.getSubject
        }
        val endAddress = annotation.getEnd
        val beginAddress = annotation.getBegin
        val featureStructureArray = annotation.getOntologyConceptArr
        val codeList = featureStructureArray.toArray
          .map { featureStructure =>
            val featureList = featureStructure.getType.getFeatures.asScala.toList
            val temp: (String, String) = ("", "")
            featureList.foldLeft(temp) { (x, y) =>
              if (y.getShortName.equals("codingScheme"))
                (featureStructure.getStringValue(y), x._2)
              else if (y.getShortName.equals("code"))
                (x._1, featureStructure.getStringValue(y))
              else
                x
            }
          }
        val preferredTextList = featureStructureArray.toArray
          .flatMap{featureStructure =>
            val featureList = featureStructure.getType.getFeatures.asScala.toList
            featureList.map{feature =>
              feature.getShortName match {
                case "preferredText" => (feature.getShortName, featureStructure.getStringValue(feature))
                case _ => null
              }}
          }.filter(_ != null).toList
        val preferredText = preferredTextList.groupBy(_._1).map(preferredTextTuple => (preferredTextTuple._1, preferredTextList.map(_._2).distinct))
        val preferredTextString = preferredText.map { map => (map._1, map._2.mkString(", ")) }
        val codeMap = codeList.groupBy(codeTuple => codeTuple._1).map(groupedCodes => (groupedCodes._1, groupedCodes._2.map(y => y._2).distinct))
        val codeStringMap = codeMap.map { map => (map._1, map._2.mkString(", ")) }
        val addressMap = mapAsJavaMap(Map("start" -> beginAddress, "end" -> endAddress))
        val combinedMap = Map("entity" -> coveredText, "entity_type" -> textType, "polarity" -> polarity, "subject" -> subject, "position" -> addressMap) ++ codeStringMap ++ preferredTextString
        mapAsJavaMap(combinedMap)
      }
    jcasInstance.reset()
    outputMap
  }

  def getSchemaMap = {
    Array(Map("name" -> "subject", "display_name" -> "Subject"),
      Map("name" -> "entity_type", "display_name" -> "Entity Type"),
      Map("name" -> "entity", "display_name" -> "Entity"),
      Map("name" -> "polarity", "display_name" -> "Polarity"),
      Map("name" -> "preferredText", "display_name" -> "Standardized Text"),
      Map("name" -> "ICD9CM_2014", "display_name" -> "ICD9CM"),
      Map("name" -> "ICD10CM_2017", "display_name" -> "ICD10CM"),
      Map("name" -> "ICD10PCS_2017", "display_name" -> "ICD10PCS"),
      Map("name" -> "CPT2016", "display_name" -> "CPT"),
      Map("name" -> "HCPCS2016", "display_name" -> "HCPCS"),
      Map("name" -> "LNC256", "display_name" -> "LOINC"),
      Map("name" -> "RXNORM_16AA_160906F", "display_name" -> "RXNORM")
    ).map { map => mapAsJavaMap(map) }
  }
}

/** Factory for CTakesLocal
  *
  */
object CTakesLocal{
  val ctakesLocal = new CTakesLocal
  def apply(): CTakesLocal = ctakesLocal
}