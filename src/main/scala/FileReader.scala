/**
  * Created by Pablo on 15/6/17.
  *
  *
  */

import scala.io.Source
import data.railway.Train
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

class FileReader {

  /**
    * Funcion que lee el fichero de texto con el parametro especificado por le nombre. El fichero debe tener el formato
    * correspondiente a los trenes y horarios
    *
    * @param name
    */
  def readTrains(name: String): Array[Train] = {

    val bufferedSource = Source.fromFile(name)
    var trains: Array[Train] = new Array[Train](0)

    try {
      for (linesPair <- bufferedSource.getLines().sliding(2, 2)) {

        //          val schedules = bufferAsJavaList(linesPair.head)
        trains = trains :+ new Train(linesPair.head.split(" ").map(x => toTime(x)), linesPair.last.split(" "))
      }
    } finally {
      bufferedSource.close

    }

    return trains

  }

  /**
    * Funcion que lee el fichero de texto con el parametro especificado por le nombre. El fichero debe tener el formato
    * correspondiente a los recursos
    *
    * @param name
    */
  def readResources(name: String) = {
    val bufferedSource = Source.fromFile(name)

    var stations: Array[String] = new Array[String](0)
    val resourcesStation = ListBuffer[Int]()

    for (linesPair <- bufferedSource.getLines().sliding(2, 2)) {
      stations = linesPair.head.split(" ")

      // DEBUG
      //stations.foreach(x => print(x + " "))
      //println("")

      var stationIndex = 0
      var totalResources = 0
      //      var x = 0
      for (numberOfResources <- linesPair.last.split(" ")) {
        //***** DEBUG *****
        //println("Total resources: " + totalResources)
        //println("Number of resources in station " + stations(stationIndex) + " : " + numberOfResources.toInt)
        for (x <- totalResources to (numberOfResources.toInt + totalResources -1 )) {
          //resourcesStation = resourcesStation :+ i
          resourcesStation += stationIndex
        }
        totalResources = numberOfResources.toInt + totalResources
        stationIndex += 1
      }
      //println("Final Total resources: " + totalResources)
    }

    bufferedSource.close

    //DEBUG
    //println(resourcesStation.toArray.length)

    (stations, resourcesStation.toArray)
  }

  /**
    * Transform a string in a number in time format or other manageable number
    *
    * @param timeString
    */
  def toTime(timeString: String): Int = {

    val timeIntList = timeString.split(":").map(_.toInt)

    var timeInMinutes: Int = 0
    var factor = 60
    for (figure <- timeIntList) {
      timeInMinutes = timeInMinutes + figure * factor
      factor = factor / 60
    }

    return timeInMinutes
  }
}
