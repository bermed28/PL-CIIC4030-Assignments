import java.io.{FileNotFoundException, IOException}

import akka.actor.{ActorSystem, Props}
import javax.imageio.IIOException


object Client extends App{
    val server = ActorSystem("Server")
    val actor = server.actorOf(Props[Server], name="medianFilterSim")

    actor ! "parallel"
    actor ! "simple"

}
