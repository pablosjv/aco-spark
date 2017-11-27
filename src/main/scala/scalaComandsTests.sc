import scala.io.Source

val workDir = "src/main/scala/"
val trainsFile = workDir + "horariosTrenes.txt"
val resourcesFile = workDir + "recursosBase.txt"

println(System.getProperty("user.dir"))

//val time = "8:30:00"
//
//val timeInt = time.split(":").map(_.toInt)
//
//var timeInSeconds: Int = 0
//var factor = 3600
//for (figure <- timeInt){
//  timeInSeconds = timeInSeconds + figure*factor
//  factor = factor/60
//}
//
//println(timeInSeconds)

var hello = List[String]()

for(a <- 0 until hello.length ){
  println(hello(a))
}

hello = hello :+ "hey"
hello = hello :+ "uo"
hello = hello :+ "lol"

hello = List[String]()
hello

println("SUCCESS")
