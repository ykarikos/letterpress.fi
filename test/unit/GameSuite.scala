package unit

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import models._

@RunWith(classOf[JUnitRunner])
class GameSuite extends FunSuite {
	test("serializeTiles") {
	  var i = 0
	  val tiles:List[Tile] = for (c <- "ABCDEF".toList) 
	    yield { val t = Tile(c, i, None); i += 1; t }
	  assert(Game.serializeTiles(tiles) === "A0B0C0D0E0F0")
	}
}