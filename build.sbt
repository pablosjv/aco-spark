name := "aco-cp-spark"

version := "1.0"

scalaVersion := "2.11.8"

val sparkVersion = "1.6.1"

val chocoVersion = "4.0.4"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion
)

//libraryDependencies += "org.choco-solver" % "choco-solver" % chocoVersion
libraryDependencies += "org.choco-solver" % "choco-sat" % "1.0.2"
libraryDependencies += "org.choco-solver" % "cutoffseq" % "1.0.2"
libraryDependencies += "org.testng" % "testng" % "6.11"
libraryDependencies += "net.sf.trove4j" % "trove4j" % "3.0.3"
libraryDependencies += "dk.brics.automaton" % "automaton" % "1.11-8"
libraryDependencies += "org.javabits.jgrapht" % "jgrapht-core" % "0.9.3"
libraryDependencies += "com.github.cp-profiler" % "cpprof-java" % "1.3.0"


// https://mvnrepository.com/artifact/com.esotericsoftware/kryo
libraryDependencies += "com.esotericsoftware" % "kryo" % "4.0.0"


retrieveManaged := true
