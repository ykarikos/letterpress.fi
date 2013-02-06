package models

object TileOwner extends Enumeration {
	type TileOwner = Value
	val Neither = Value("0")
	val PlayerOne = Value("1")
	val PlayerTwo = Value("2")
	val PlayerOneLocked = Value("3")
	val PlayerTwoLocked = Value("4")
	
}

import TileOwner._

case class Tile(letter: Char, id: Int, owner: TileOwner)