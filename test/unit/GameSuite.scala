package unit

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import models._
import models.TileOwner._

@RunWith(classOf[JUnitRunner])
class GameSuite extends FunSuite {
	test("serializeTilesNeither") {
	  var i = 0
	  val tiles:List[Tile] = for (c <- "ABCDEF".toList) 
	    yield { val t = Tile(c, i, Neither); i += 1; t }
	  assert(Game.serializeTiles(tiles) === "A0B0C0D0E0F0")
	}
	
	test("serializeTiles") {
	  var i = 0
	  val tiles:List[Tile] = for (o <- List(PlayerOne, PlayerTwo, PlayerOne, PlayerTwo, Neither, PlayerOneLocked, PlayerTwoLocked)) 
	    yield { val t = Tile(('A' + i).toChar, i, o); i += 1; t }
	  assert(Game.serializeTiles(tiles) === "A1B2C1D2E0F3G4")
	}
	
	test("deserializeTilesNeither") {
	  val tiles = Game.deserializeTiles("A0B0C0D0E0")
	  assert(tiles.length === 5)
	  var i = 0
	  tiles.foreach(t => {
		  assert(t.letter === ('A' + i).toChar)
		  assert(t.owner === Neither)
		  i += 1
	  })
	}
	
	test("deserializeTiles") {
	  val tiles = Game.deserializeTiles("A3B2C1D0E4")
	  val owners = List(PlayerOneLocked, PlayerTwo, PlayerOne, Neither, PlayerTwoLocked)
	  assert(tiles.length === 5)
	  var i = 0
	  tiles.foreach(t => {
		  assert(t.letter === ('A' + i).toChar)
		  assert(t.owner === owners(i))
		  i += 1
	  })
	}
}