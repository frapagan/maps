package org.gbif.maps.spark

import org.gbif.maps.io.PointFeature
import org.apache.spark.sql.Row

import scala.collection.mutable

/**
  * TODO: Consider moving much of this to Java and into the common project
  */
object MapUtils {
  // Dictionary of map types
  val MAPS_TYPES = Map(
    "ALL" -> 0,
    "TAXON" -> 1,
    "DATASET" -> 2,
    "PUBLISHER" -> 3,
    "COUNTRY" -> 4,
    "PUBLISHING_COUNTRY" -> 5)

  // Dictionary mapping the GBIF API BasisOfRecord enumeration to the Protobuf versions
  val BASIS_OF_RECORD = Map(
    "UNKNOWN" -> PointFeature.PointFeatures.Feature.BasisOfRecord.UNKNOWN,
    "PRESERVED_SPECIMEN" -> PointFeature.PointFeatures.Feature.BasisOfRecord.PRESERVED_SPECIMEN,
    "FOSSIL_SPECIMEN" -> PointFeature.PointFeatures.Feature.BasisOfRecord.FOSSIL_SPECIMEN,
    "LIVING_SPECIMEN" -> PointFeature.PointFeatures.Feature.BasisOfRecord.LIVING_SPECIMEN,
    "OBSERVATION" -> PointFeature.PointFeatures.Feature.BasisOfRecord.OBSERVATION,
    "HUMAN_OBSERVATION" -> PointFeature.PointFeatures.Feature.BasisOfRecord.HUMAN_OBSERVATION,
    "MACHINE_OBSERVATION" -> PointFeature.PointFeatures.Feature.BasisOfRecord.MACHINE_OBSERVATION,
    "MATERIAL_SAMPLE" -> PointFeature.PointFeatures.Feature.BasisOfRecord.MATERIAL_SAMPLE,
    "LITERATURE" -> PointFeature.PointFeatures.Feature.BasisOfRecord.LITERATURE)

  // Encodes the type and value into the HBase table key
  def toMapKey(mapType: Int, key: Any) : String = {
    mapType + ":" + key;
  }

  // Encodes the XYZ into a string
  def toZXY(z: Byte, x: Long, y: Long) : String = {
    z + ":" + x + ":" + y;
  }

  // Decodes the XYZ from a string
  def fromZXY(encoded: String) : (Short,Long,Long) = {
    val zxy = encoded.split(":")
    (zxy(0).toShort, zxy(1).toLong, zxy(2).toLong)
  }

  // Encodes the Pixel into an EncodedPixel
  def encodePixel(p: Pixel) : EncodedPixel = {
    p.x.toInt << 16 | p.y
  }

  // Decodes the EncodedPixel into a Pixel
  def decodePixel(encp: EncodedPixel) : Pixel = {
    Pixel((encp >> 16).asInstanceOf[Short], encp.asInstanceOf[Short])
  }

  // Encodes an EncodedPixel and Year into a Long
  def encodePixelYear(p: EncodedPixel, year: Year) : EncodedPixelYear = {
    p.toLong << 32 | year
  }

  // Decodes an EncodedPixelYear into an EncodedPixel and Year pair
  def decodePixelYear(py: EncodedPixelYear) : (EncodedPixel, Year) = {
    ((py >> 32).asInstanceOf[Int], py.asInstanceOf[Short])
  }

  // Returns all the map keys for the given row in an immutable Set
  def mapKeysForRecord(row: Row) : Set[String] = {
    val datasetKey = row.getString(row.fieldIndex("datasetkey"))
    val publisherKey = row.getString(row.fieldIndex("publishingorgkey"))
    val country = row.getString(row.fieldIndex("countrycode"))
    val publishingCountry = row.getString(row.fieldIndex("publishingcountry"))

    var taxonIDs = Set[Int]()
    if (!row.isNullAt(row.fieldIndex("kingdomkey"))) taxonIDs+=row.getInt(row.fieldIndex("kingdomkey"))
    if (!row.isNullAt(row.fieldIndex("phylumkey"))) taxonIDs+=row.getInt(row.fieldIndex("phylumkey"))
    if (!row.isNullAt(row.fieldIndex("classkey"))) taxonIDs+=row.getInt(row.fieldIndex("classkey"))
    if (!row.isNullAt(row.fieldIndex("orderkey"))) taxonIDs+=row.getInt(row.fieldIndex("orderkey"))
    if (!row.isNullAt(row.fieldIndex("familykey"))) taxonIDs+=row.getInt(row.fieldIndex("familykey"))
    if (!row.isNullAt(row.fieldIndex("genuskey"))) taxonIDs+=row.getInt(row.fieldIndex("genuskey"))
    if (!row.isNullAt(row.fieldIndex("specieskey"))) taxonIDs+=row.getInt(row.fieldIndex("specieskey"))
    if (!row.isNullAt(row.fieldIndex("taxonkey"))) taxonIDs+=row.getInt(row.fieldIndex("taxonkey"))

    val res = mutable.Set(
      toMapKey(MAPS_TYPES("ALL"), 0),
      toMapKey(MAPS_TYPES("DATASET"), datasetKey),
      toMapKey(MAPS_TYPES("PUBLISHER"), publisherKey),
      toMapKey(MAPS_TYPES("COUNTRY"), country),
      toMapKey(MAPS_TYPES("PUBLISHING_COUNTRY"), publishingCountry)
    )
    taxonIDs.foreach(id => {
      res += toMapKey(MAPS_TYPES("TAXON"), id)
    })
    res.toSet // immutable
  }
}
