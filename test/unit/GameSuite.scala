package unit

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import models._
import models.TileOwner._
import models.PlayerTurn

@RunWith(classOf[JUnitRunner])
class GameSuite extends FunSuite {
  def createTiles(owners: List[TileOwner]) = {
    var i = 0
    for (o <- owners)
      yield { val t = Tile(('A' + i).toChar, i, o); i += 1; t }
  }
  
	test("serializeTilesNeither") {
	  val tiles = createTiles(List.fill(6)(Neither))
	  assert(Game.serializeTiles(tiles) === "A0B0C0D0E0F0")
	}
	
	test("serializeTiles") {
	  val tiles:List[Tile] = createTiles(List(PlayerOne, PlayerTwo, PlayerOne, PlayerTwo, Neither, PlayerOneLocked, PlayerTwoLocked)) 
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
	
	test("neighrbors0") {
	  val tiles = createTiles(List.fill(25)(Neither))
	  val n0 = Tile.neighbors(tiles, tiles(0))
	  assert(n0.length === 2)
	  assert(n0.contains(tiles(1)) === true)
	  assert(n0.contains(tiles(5)) === true)
	}
	
	test("neighrbors1") {
	  val tiles = createTiles(List.fill(25)(Neither))
	  val n = Tile.neighbors(tiles, tiles(1))
	  assert(n.length === 3)
	  assert(n.contains(tiles(0)) === true)
	  assert(n.contains(tiles(2)) === true)
	  assert(n.contains(tiles(6)) === true)
	}

	test("neighrbors6") {
	  val tiles = createTiles(List.fill(25)(Neither))
	  val n = Tile.neighbors(tiles, tiles(6))
	  assert(n.length === 4)
	  assert(n.contains(tiles(1)) === true)
	  assert(n.contains(tiles(5)) === true)
	  assert(n.contains(tiles(7)) === true)
	  assert(n.contains(tiles(11)) === true)
	}
	
	test("normalizeNeither") {
	  val tiles = createTiles(List.fill(25)(Neither))
	  val normalized = Tile.normalize(tiles)
	  tiles.foreach(t => assert(t === normalized(t.id)))
	}
	/*
11000 => 31000
11200    31200
12220    12420
00202    00202
22002    22002
	 */
	test("normalize") {
	  val owners = List(
	      1,1,0,0,0,
	      1,1,2,0,0,
	      1,2,2,2,0,
	      0,0,2,0,2,
	      2,2,0,0,2)
	  val expectedOwners = List(
	      3,1,0,0,0,
	      3,1,2,0,0,
	      1,2,4,2,0,
	      0,0,2,0,2,
	      2,2,0,0,2)
	  val tiles = createTiles(owners map { o => TileOwner.withName(o.toString) })
	  val expectedTiles = createTiles(expectedOwners map { o => TileOwner.withName(o.toString) })
	  val normalized = Tile.normalize(tiles)
	  expectedTiles.foreach(t => assert(t === normalized(t.id)))
	  
	}
	
	test("selectWord") {
	  val tiles = createTiles(List.fill(25)(Neither))
	  val word = List(0, 2, 4)
	  val afterWord = Tile.selectWord(word, tiles, PlayerTurn.PlayerOne)
	  
	  List(0,2,4).foreach(i => assert(afterWord(i).owner === PlayerOne))
	  List(1,3).foreach(i => assert(afterWord(i).owner === Neither))
	  5 to 24 foreach(i => assert(afterWord(i).owner === Neither))
	  
	  val secondWord = List(0, 1, 2)
	  val afterSecondWord = Tile.selectWord(secondWord, afterWord, PlayerTurn.PlayerTwo)
	  
	  List(0,1,2).foreach(i => assert(afterSecondWord(i).owner === PlayerTwo))
	  assert(afterSecondWord(3).owner === Neither)
	  assert(afterSecondWord(4).owner === PlayerOne)
	  5 to 24 foreach(i => assert(afterSecondWord(i).owner === Neither))
	}
	
	test("changeTile") {
	  val owners = List(Neither, PlayerOne, PlayerTwo)
	  val turns = List(PlayerTurn.PlayerOne, PlayerTurn.PlayerTwo)
	  owners.foreach(o =>
	    turns.foreach(t => assert(Tile.changeTile(t, 1)(Tile('A', 1, o)) === Tile('A', 1, Game.turn2Owner(t))))
	  )
	      
	  turns.foreach(t => assert(Tile.changeTile(t, 1)(Tile('A', 1, PlayerOneLocked)) === Tile('A', 1, PlayerOneLocked)))
	  turns.foreach(t => assert(Tile.changeTile(t, 1)(Tile('A', 1, PlayerTwoLocked)) === Tile('A', 1, PlayerTwoLocked)))
	}
	
}