package models

import java.io.FileNotFoundException
import java.util.HashMap

import org.apache.ctakes.assertion.medfacts.cleartk._
import org.apache.uima.fit.factory.{AggregateBuilder, AnalysisEngineFactory, ExternalResourceFactory, JCasFactory}
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory._
import org.apache.ctakes.core.resource.{FileLocator, FileResourceImpl}
import org.apache.ctakes.dependency.parser.ae.ClearNLPDependencyParserAE
import org.apache.ctakes.dictionary.lookup2.ae.{AbstractJCasTermAnnotator, DefaultJCasTermAnnotator, JCasTermAnnotator}
import org.apache.ctakes.typesystem.`type`.textsem.IdentifiedAnnotation
import org.apache.uima.fit.util.JCasUtil
import org.apache.uima.resource.ResourceInitializationException

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
/**
  * Created by Mukul Dev on 14/4/17.
  */
class CTakesLocal {
  val analysisEngineDescription = getPipeline
  val analysisEngine=analysisEngineDescription.createAggregate()
  val jcasInstance = analysisEngine.newJCas()

  def getCodeMap(clinicalText: String) = {
    jcasInstance.setDocumentText(clinicalText)
    analysisEngine.process(jcasInstance)

    val identifiedAnnotationList = JCasUtil.select(jcasInstance, new IdentifiedAnnotation(jcasInstance).getClass).iterator().asScala.toList
    val outputMap = identifiedAnnotationList.filter(annotation => annotation.getOntologyConceptArr != null)
      .map(annotation => {
        val coveredText = annotation.getCoveredText
        val textType = annotation.getType.getShortName
        val polarity = annotation.getPolarity.toString
        val subject = annotation.getSubject
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
        val codeMap = codeList.groupBy(codeTuple => codeTuple._1).map(groupedCodes => (groupedCodes._1, groupedCodes._2.map(y => y._2)))
        val codeStringMap = codeMap.map{map => (map._1, map._2.mkString(","))}
        val filteredCodeMap = codeStringMap.filter(x => x._1 != "SNOMEDCT")
        if(filteredCodeMap.nonEmpty) {
          val addressMap = mapAsJavaMap(Map("start" -> beginAddress, "end" -> endAddress))
          val combinedMap = Map("entity" -> coveredText, "entity_type" -> textType, "polarity" -> polarity, "subject" -> subject, "position" -> addressMap) ++ filteredCodeMap
          mapAsJavaMap(combinedMap)
        }
        else
          mapAsJavaMap(Map())
      })
    jcasInstance.reset()
    outputMap.filter(_.nonEmpty)
  }
  def getSchemaMap = {
    Array(Map("name" -> "subject", "display_name" -> "Subject"),
      Map("name" -> "position", "display_name" -> "Position"),
      Map("name" -> "entity_type", "display_name" -> "Entity Type"),
      Map("name" -> "entity", "display_name" -> "Entity"),
      Map("name" -> "polarity", "display_name" -> "Polarity"),
      Map("name" -> "ICD10PCS", "display_name" -> "ICD10PCS"),
      Map("name" -> "ICD9CM", "display_name" -> "ICD9CM"),
      Map("name" -> "RXNORM", "display_name" -> "RXNORM")
    ).map{map => mapAsJavaMap(map)}
  }
  def getPipeline={
    val builder = new AggregateBuilder
    builder.add(getTokenProcessingPipeline)
    try
      builder.add(AnalysisEngineFactory.createEngineDescription(classOf[DefaultJCasTermAnnotator], AbstractJCasTermAnnotator.PARAM_WINDOW_ANNOT_PRP, "org.apache.ctakes.typesystem.type.textspan.Sentence", JCasTermAnnotator.DICTIONARY_DESCRIPTOR_KEY, ExternalResourceFactory.createExternalResourceDescription(classOf[FileResourceImpl], FileLocator.locateFile("org/apache/ctakes/dictionary/lookup/fast/cTakesHsql.xml"))))
    catch {
      case e: FileNotFoundException =>
        e.printStackTrace()
        throw new ResourceInitializationException(e)
    }
    builder.add(ClearNLPDependencyParserAE.createAnnotatorDescription)
    builder.add(PolarityCleartkAnalysisEngine.createAnnotatorDescription)
    builder.add(UncertaintyCleartkAnalysisEngine.createAnnotatorDescription)
    builder.add(HistoryCleartkAnalysisEngine.createAnnotatorDescription)
    builder.add(ConditionalCleartkAnalysisEngine.createAnnotatorDescription)
    builder.add(GenericCleartkAnalysisEngine.createAnnotatorDescription)
    builder.add(SubjectCleartkAnalysisEngine.createAnnotatorDescription)
    builder.createAggregateDescription
    builder
  }
}
object CTakesLocal{
  val ctakesLocal = new CTakesLocal
  def apply(): CTakesLocal = ctakesLocal
}