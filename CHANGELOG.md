# v5.1.4
## Agregado
- Se agrega la ejecucion de la tarea 'bintrayPublish' para asegurar que todos los artifacts se publiquen

# v5.1.3
## Revertido
- Se revierte el auto publish de bintray

# v5.1.1
## Arreglado
- Se arregla el auto publish de bintray

# v5.1.0
- Se fixea las tareas de linteo para que corran en gradle 4
- Se agrega soporte configuracion de linteo por modulo

# v5.0.9
- Arreglamos la publicacion de variants

# v5.0.8
- Solo habilitamos el lock de dependencias si es un task que lo necesita o si los files existen

# v5.0.7
- Agregamos una variable de configuracion que faltaba para los experimental 

# v5.0.6
- Fixeamos la publicacion de libs con variants que subian solo el ultimo

# v5.0.5
- Activamos dependencyLocking siempre

# v5.0.4
- Quedo mal creado el release

# v5.0.3
## Arreglado
- No se estaban agregando las deps por variant al pom
- No se podia modificar un solo modulo del lock
- No se estaban aplicando los locks a los poms

# v5.0.2
## Arreglado
- Arreglamos que no se estaban tomando los alphas en develop

# v5.0.1
- No cambia nada, fue necesario porque el 5.0.0 se publico mal

# v5.0.0
## Agregado
- Soporte a gradle 4

# v4.5.1
## Arreglado
- Se valida por nombre de repo al rejectear alphas tambien

# v4.5.0
## Agregado
- Agregamos la url de nuestro repo publico

# v4.4.6
## Cambiado
- Typo de la 4.4.5

# v4.4.5
## Cambiado
- Se desactiva findbugs de todos los proyectos automaticamente

# v4.4.4
## Cambiado
- Se fixea el aar que se provee al publicar, ya que en las publicaciones flavored el name viene en camelcase y necesitamos hypencase (ver https://github.com/mercadolibre/mobile-android_gradle/pull/131)

# v4.4.3
## Cambiado
- Se fixea issue en el pom, no agregando correctamente nodos de exclusiones.

# v4.4.2
## Cambiado
- Se arregla validacion de si una dependencia es local o no cuando tienen groups distintos dentro del mismo repositorio
- Se arregla conflicto entre las versiones de cada tipo de publicacion

# v4.4.1
## Cambiado
- Se arregla pom con dependencias locales dinamicas que quedaban fijas

# v4.4.0
## Cambiado
- Se refactoriza la creacion de los poms
- Se saca findbugs ya que figura como 'compile' para proyectos de java puros
- Se da soporte para poms con configuration de gradle 4.X

# v4.3.1
## Cambiado
- Se fixea issue en el pom, no agregando los nodos de exclusiones.

# v4.3.0
## Nuevo
- Se refactorea el JSON para el lint de dependencias
- Se agrega campo `expires` para cada dependencia de la whitelist

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
