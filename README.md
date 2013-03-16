Letterpress.fi
==============

Attempt to create a clone for Letterpress (http://www.atebits.com/letterpress/)
in the web using Finnish language.

Try it out at http://letterpress.peruna.fi

Ideas for improvement:
 * authenticate players properly (e.g. via Google) and/or make
   sure that a game can not be hijacked
 * when another player has played a tile, reload the page less violently:
   preserve selected tiles 
 * use player gravatars instead of icons
 * language selection (e.g. de, sv, en)
 * list player's games
 * list leaderboard
 * show player names more clearly
 * show rules
 * Github banner

Current bugs:
 * Firefox: displays too bold font
 * Firefox: dragging a tile puts it back - jquery sortable triggers click


Requirements
------------
Letterpress.fi is created with Play Framework 2.1 and Scala 2.10.
See http://www.playframework.com/ and http://www.scala-lang.org/


Licenses
--------
Letterpress.fi (C) 2013 Yrjö Kari-Koskinen <ykk@peruna.fi>

Letterpress.fi's source code is licensed with the MIT License, see LICENSE.txt

Varela Round font is obtained from Google Web Fonts http://www.google.com/webfonts
and is licensed with SIL Open Font License, see OFL.txt.

Finnish word list is obtained from Kotimaisten kielten keskus 
http://kaino.kotus.fi/sanat/nykysuomi/ and is licensed with GNU LGPL, see 
http://www.gnu.org/licenses/lgpl.html
