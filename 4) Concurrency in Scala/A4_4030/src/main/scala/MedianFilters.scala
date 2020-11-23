import java.awt.Color
import java.awt.image.BufferedImage
import java.util

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

import scala.concurrent._
import ExecutionContext.Implicits.global

object  MedianFilters {

  //to calculate run time
  private def time[R](block: => R): (R, Double) = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    val diff = t1-t0
    (result, diff/ 1E9)
  }

  // i <- 1 until img.getWidth/2 ; j <- 1 until img.getHeight - 1

  private def filterRange(img:BufferedImage, starti:Int, endi:Int, startj:Int, endj:Int) = {
    val pixelWindow = new Array[Color](9) //save all color
    val redWindow = new Array[Int](9)
    val greenWindow = new Array[Int](9)
    val blueWindow = new Array[Int](9)

    //i = row       j = columns
    for (i <- starti until endi; j <- startj until endj) {

      //neighbor pixel values are stored in window including this pixel
      pixelWindow(0) = new Color(img.getRGB(i - 1, j - 1))
      pixelWindow(1) = new Color(img.getRGB(i - 1, j))
      pixelWindow(2) = new Color(img.getRGB(i - 1, j + 1))
      pixelWindow(3) = new Color(img.getRGB(i, j - 1))
      pixelWindow(4) = new Color(img.getRGB(i, j))
      pixelWindow(5) = new Color(img.getRGB(i, j + 1))
      pixelWindow(6) = new Color(img.getRGB(i + 1, j - 1))
      pixelWindow(7) = new Color(img.getRGB(i + 1, j))
      pixelWindow(8) = new Color(img.getRGB(i + 1, j + 1))


      for (k <- 0 until pixelWindow.length) {
        redWindow(k) = pixelWindow(k).getRed
        blueWindow(k) = pixelWindow(k).getBlue
        greenWindow(k) = pixelWindow(k).getGreen
      }
      //uses quicksort to sort window array
      util.Arrays.sort(redWindow)
      util.Arrays.sort(blueWindow)
      util.Arrays.sort(greenWindow)

      ////Applies the median to the new array
      img.setRGB(i, j, new Color(redWindow(4), greenWindow(4), blueWindow(4)).getRGB)
    }

  }

  //processes the image using Scala parallel collections
  def medianFilterParallel(img:BufferedImage): Future[(BufferedImage, Double)] = Future {
    time{
      //
      val part1 = Future {
        filterRange(img, 1, img.getWidth/2, 1, img.getHeight/2)
      }
      val part2 = Future {
        filterRange(img, img.getWidth/2, img.getWidth - 1, 1, img.getHeight/2)
      }
      val part3 = Future {
        filterRange(img, 1, img.getWidth/2, img.getHeight/2, img.getHeight - 1)
      }
      val part4 = Future {
        filterRange(img, img.getWidth/2, img.getWidth - 1, img.getHeight/2, img.getHeight - 1)
      }

      Await.ready(part4, Duration.Inf)

      img
    }
  }
  //processes the image with a serial implementation of the median filter
  def medianFilterSimple(img:BufferedImage): Future[(BufferedImage, Double)] = Future {
    time {
      filterRange(img, 1, img.getWidth - 1, 1, img.getHeight - 1)
      img
    }
  }

}
