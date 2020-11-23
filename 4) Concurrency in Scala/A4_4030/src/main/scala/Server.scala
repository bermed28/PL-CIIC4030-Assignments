import java.awt.Color
import java.awt.image.BufferedImage
import java.io.{File, FileNotFoundException}
import java.util

import akka.actor.Actor
import akka.event.{Logging, LoggingAdapter}
import javax.imageio.{IIOException, ImageIO}

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


class Server extends Actor {

  val log: LoggingAdapter = Logging(context.system, this)

  val path = scala.io.StdIn.readLine("Enter Image path to compute Median Filters: ")

  def receive : PartialFunction[Any, Unit] = {


    case "parallel" ⇒ {

      try {
        val imgP: BufferedImage = ImageIO.read(new File(path))
        //save Futures in variables
        val parallel = MedianFilters.medianFilterParallel(imgP)


        log.info("Applying Parallel Median Filter...")
        parallel.map {
          result =>
            var curr: BufferedImage = result._1
            val g = curr.getGraphics
            g.setFont(g.getFont.deriveFont(18f))
            g.setColor(Color.white)
            g.drawString(s"Applied Parallel Median Filter, elapsed time: ${result._2.toString} s", 100, 50)
            ImageIO.write(curr, "jpg", new File("./out/parallel.jpg"))
            log.info("Application successful, check your root directory for output named 'parallel.jpg'")

        }
      } catch{
        case e: FileNotFoundException ⇒ println("File not found,restart client and enter a valid path")
        case e: IIOException ⇒ println("File not found, restart client and enter a valid path")
          System.exit(0)
      }
    }

    case "simple" ⇒ {

      try {
        val imgS: BufferedImage = ImageIO.read(new File(path))
        val simple = MedianFilters.medianFilterSimple(imgS)
        log.info("Applying Simple Median Filter...")
        simple.map {
          result =>
            var curr: BufferedImage = result._1
            val g = curr.getGraphics
            g.setFont(g.getFont.deriveFont(18f))
            g.setColor(Color.white)
            g.drawString(s"Applied Simple Median Filter, elapsed time: ${result._2.toString} s", 100, 50)
            ImageIO.write(curr, "jpg", new File("./out/simple.jpg"))
            log.info("Application successful, check your 'out' folder for output named 'simple.jpg'")
            log.info("Press enter to exit program")

            val exit = scala.io.StdIn.readLine()
            if(exit == ""){
              println("Thank you for using the median filter simulator client!")
              System.exit(0)
            }

        }
      } catch{
        case e: FileNotFoundException ⇒ println("File not found,restart client and enter a valid path")
        case e: IIOException ⇒ println("File not found, restart client and enter a valid path")
        System.exit(0)
      }
    }

  }

}


