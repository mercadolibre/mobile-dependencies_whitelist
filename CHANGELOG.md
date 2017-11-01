# v4.2.2
## Cambiado
- Se fixea issue en lockVersions, cambiando configuraciones ya resueltas - #116
- Se fixea una race condition con la task check al agregarle una dependencia - #117
- Se fixea NPE con jars locales como dependencias en el lintReleaseDependencies - #118

# v4.2.1
## Nuevo
- Se fixea hook de la task lintGradle. #115

# v4.2.0
## Nuevo
- Agregamos lint para dependencias de release
- Agregamos excludes para un bug que tiene findbugs con jsr305 en las aplicaciones

# v4.1.6
## Arreglado
- Arreglamos las task de locks que generaban falsos positivos en dependencias no locales pero con mismo group

# v4.1.5
## Arreglado
- Arreglamos las task de publish con modulos que tienen activado el publishNonDefault

# v4.1.4
## Arreglado
- Arreglamos la task lintGradle que fallaba al detectar repositorios locales, pensando que eran externos.

# v4.1.3
## Arreglado
- Arreglamos el formateo de versiones en POM que siempre estaba entrando a un mismo clause
- Arreglamos la publicacion de jars locales ya que no se prefijaba la version correcta

# v4.1.2
## Arreglado
- Arreglamos las versiones locales para que sean de la forma LOCAL-:version-TIMESTAMP. [PullRequest](https://github.com/mercadolibre/mobile-android_gradle/pull/103)

# v4.1.1
## Arreglado
- Arreglamos un error que hacia que las otras libs con el mismo groupId queden con la version propia

# v4.1.0
## Cambiado
- Dejamos de depender de `check` para todas las task de release

# v4.0.1
## Arreglado
- Los .jar de alpha, experimental y release estaban siendo pisados por los sources.

## Cambiado
- Dejamos de correr `check` al publicar versiones experimentales de los .jar.

## Eliminado
- Dejamos de publicar los sources de los .jar.

# v4.0.0
- Se tiro abajo lo anterior y se hizo de nuevo
- Se agrega un solo plugin con modulos independientes adentro
- Se da soporte a publicacion de AAR y JAR
- Se da soporte a flavors, variants y buildTypes
- Se da soporte a sourceSets
- Se cambia nombre del plugin a 'mercadolibre.mobile'
- Se shippea lints de gradle dentro del plugin
- Se agregan reportes de jacoco para cada flavor, variant y buildTypes
- Se agrega task 'jacocoFullReport' que permite generar todos los reportes posibles
- Se agrega modulo para lockear dependencias
- Se le da soporte a kotlin o cualquier superset de java
