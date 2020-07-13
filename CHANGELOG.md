# 8.1.1
## Arreglado
- Workaround to 'No classes configured for SpotBugs analysis'

# 8.1.0
## Arreglado
- Stop being eager about task configuration

## Agregado
- Los repositorios inyectados por default ahora incluyen el content filtering que recomendamos
- Se agrega la task `listProjects` para listar los modulos de todo el proyecto

# 8.0.0
## Actualizado
- Volvemos agregar nueva version del plugin de gradle enterprise a [3.3.2](https://gradle.com/enterprise/releases/2020.2/#background-build-scan-publication) para poder tener scan en background

# 7.1.1
## Eliminado
- Revertimos [#175](https://github.com/mercadolibre/mobile-android_gradle/pull/175)

# 7.1.0
## Actualizado
- Se actualiza la version del plugin de gradle enterprise a [3.3.2](https://gradle.com/enterprise/releases/2020.2/#background-build-scan-publication) para poder tener scan en background

# v7.0.3
## Agregado
- Se agrega la configuración de stale para github

# v7.0.2
## Arreglado
- Se arregla el path del export de los reports de Detekt para que salgan en los PRs

# v7.0.1
## Arreglado
- Se arregla el path de los sources que lintea Detekt para que no corra sobre todos los files en cualquier ejecucion

# v7.0.0
## Agregado
- Se agrega KtLint junto a Detekt para detección de errores en las librerías con Kotlin
- Bump de Gradle a 5.6.4 

# v6.3.0
## Agregado
- Se agrega soporte a tasks de test genéricas para flavors (ej: test${buildType}UnitTest)

# v6.2.0
## Actualizado
- Se agrega un repositorio para soporte a las library templates

# v6.1.1
## Actualizado
- Se actualiza la key de bintray read

# v6.1.0
## Actualizado
- Se migra el plugin a Gradle 5.5.1

## Eliminado
- Se eliminaron las tasks para generar Alphas, junto a código que servía para mantener este flujo

# v6.0.2
## Arreglado
- Se fixea lintGradle no soportando depth>1 (ejemplo :inner1:inner2)
- Se fixea lintGradle no reconociendo modulos locales con distinto group

# v6.0.1
## Agregado
- Se agrega el soporte para publicar los builds a Gradle Enterprise

# v6.0.0
## Agregado
- Se Agrega el soporte para publicar los builds a Gradle Enterprise

# v5.10.0
## Cambiado
- Se migra el plugin a Gradle 4

# v5.9.0
## Actualizado
- Se acutualiza la versión de Jacoco a la 0.8.2
- Se incluyen clases Kotlin en el informe de cobertura

# v5.8.0
## Arreglado
- Cobertura con plugin de android 3 y kotlin

# v5.7.0
## Arreglado
- Fix keystore unpacking, making it a task

# v5.6.0
## Arreglado
- Se cambia la key de bintray read por la nueva

# v4.5.1
## Cambiado
- Se cambia la key de bintray read por la nueva

# v5.5.1
## Arreglado
- Se agrega el prefijo dynamic_ al application_id por problemas con el lint de Android

# v5.5.0
## Agregado
- Se embebe un keystore de debug para todas las aplicaciones por default. De esta forma todas las aplicaciones van a tener las mismas credenciales para debug.
- Se evita utilizar el keystore embebido en caso de encontrar una configuracion para firmar en debug ya seteada.
- Se agrega modulo para el package. Generamos dinamicamente una entry en los resources para el applicationId. Asi es accesible desde los XML (No es agregado via codigo, ya que android ya genera el `BuildConfig.APPLICATION_ID` analogo automaticamente)

# v5.4.0
## Agregado
- Se sube el tiempo que duran las caches de dependencias dinámicas

# v5.3.0
## Arreglado
- Se fixea el fail del lock en caso de que no pueda resolver las dependencias

# v5.2.0
## Arreglado
- Se resuelve un cambio en el nombre de una task que cambio a partir de la version 3.2.0+ del plugin android de gradle

# v5.1.7
## Arreglado
- Se mueve el chequeo de las Publish Task post generacion del TaskGraph debido a que en el build se corria y no se podia obtener las tasks

# v5.1.6
## Arreglado
- Se arregla el chequeo de las Publish Task al final de la ejecucion para poder publicar los artifacts

# v5.1.5
## Arreglado
- Se arregla una dependencia haciendo que Groovy la infiera

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
