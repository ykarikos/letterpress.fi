# Letterpress.fi

A Finnish language [Letterpress](http://www.letterpressapp.com/) clone.

## Requirements

* [Leiningen](https://leiningen.org/) for Development
* JVM for running it
* [PostgreSQL](https://www.postgresql.org/)

## Environment variables

* `DATABASE_URL` – PostgreSQL URI, defaults to `"postgres://postgres:secret@localhost:5432/letterpress"`

## Development mode

To start the Figwheel compiler, navigate to the project folder and run the following command in the terminal:

```
lein figwheel
```

Figwheel will automatically push cljs changes to the browser. The server will be available at [http://localhost:3449](http://localhost:3449) once Figwheel starts up.

Figwheel also starts `nREPL` using the value of the `:nrepl-port` in the `:figwheel`
config found in `project.clj`. By default the port is set to `7002`.

The figwheel server can have unexpected behaviors in some situations such as when using
websockets. In this case it's recommended to run a standalone instance of a web server as follows:

```
lein do clean, run
```

The application will now be available at [http://localhost:3000](http://localhost:3000).

### Style compilation
To compile [sass](https://github.com/Deraen/sass4clj) sources and then watch for changes and recompile until interrupted, run
```
lein sass4clj auto
```

### Optional development tools

Start the browser REPL:

```
$ lein repl
```
The Jetty server can be started by running:

```clojure
(start-server)
```
and stopped by running:
```clojure
(stop-server)
```

## Starting the database

### Postgresql

```
docker run -d -e POSTGRES_PASSWORD=secret --name letterpress-possu -p 5432:5432 postgres
```

### Mongo

```
docker run -d -p 27017:27017 --name letterpress-mongo mongo
```

## Building for release

```
lein do clean, uberjar
```

## Deploying to Heroku

Make sure you have [Git](http://git-scm.com/downloads) and [Heroku toolbelt](https://toolbelt.heroku.com/) installed, then simply follow the steps below.

Optionally, test that your application runs locally with foreman by running.

```
foreman start
```

Now, you can initialize your git repo and commit your application.

```
git init
git add .
git commit -m "init"
```
create your app on Heroku

```
heroku create
```

The connection settings can be found at your [Heroku dashboard](https://dashboard.heroku.com/apps/) under the add-ons for the app.

deploy the application

```
git push heroku master
```

Your application should now be deployed to Heroku!
For further instructions see the [official documentation](https://devcenter.heroku.com/articles/clojure).


## Licenses

Letterpress.fi's source code is licensed with the MIT License, see [LICENSE.txt](LICENSE.txt)

Varela Round font is obtained from [Google Web Fonts](http://www.google.com/webfonts)
and is licensed with SIL Open Font License, see [OFL.txt](OFL.txt).

Finnish word list is obtained from [Kotimaisten kielten keskus](http://kaino.kotus.fi/sanat/nykysuomi/)
and is licensed with [GNU LGPL](http://www.gnu.org/licenses/lgpl.html)

## Thanks

This project is a grateful recipient of the [Futurice Open Source sponsorship program](http://futurice.com/blog/sponsoring-free-time-open-source-activities?utm_source=github&utm_medium=spice) 🌶🦄.
