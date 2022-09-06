# 14.3.0
- TODO

# 14.2.1
- Se cambia la carpeta donde se almacenan los reportes de linteo para que CI los detecte
- Se elimina el estilo que aplica el Linteo a los output ya que CI los formatea de una forma diferente

# 14.2.0
- Se agrega la configuracion Default de Proguard para los modulos
- Se modifica el tiempo en el que se aplican los plugins para eliminar posibles errores

# 14.1.0
- Se especifica la version disponible al mostrar un error en Linteo
- Ya que el KeystoreModule no se ejecuta como default se evita que envie la notificacion de desactivado manualmente
- Se excluye los test en jdk.internal para que no fallen los test en Java 11

# 14.0.0
- Se divide el BasePlugin en los Plugins BasePlugin para el root, y el BaseSettingsPlugin para las settings del root

# 13.2.0
- Se agrega el linteo para los modulos java conjunto a su extension
- Se modifica la extesnsion del KeyStore Module
- Se modifica la interface Module para que se pueda modificar su funcionamiento
- Se agrega el AppBasicsConfigurer que fuerza dependencias para que no se genere output

# 13.1.3
- Se agrega la configuracion para agregar el versionName Automaticamente a los proyectos
- Se modifica el tiempo de ejecucion de los modulos jacoco para que puedan leer los productFlavor

# 13.1.2
- Se agrega las configuraciones necesarias para que Jacoco encuentre todas las clases a testear

# 13.1.1
- Se modifica el nombre que imprimen los modulos al ser desactivados, se cambio el nombre de su clase por el nombre de su extension 

# 13.1.0
- Se genera el Plugin de Dynamic Feature para configurar los modulos de este estilo
- Se genera el DynamicFeatureAndroidConfigurer que configurara los modulos para que sean capaces de compilar
- Se agregaron los test del nuevo plugin Dynamic Feature
- Se agrega la task projectInfo para saber las versiones de Android que utiliza un proyecto
- Se agrega la funcionalidad a los test de mockear proyectos completos con sus subprojectos, y extensiones
- Se mejora la calidad de los test para que verifiquen el funcionamiento correctamente 
- Se configura el Build cache para que el proyecto Root no tenga que hacerlo
- Se configura el Bugsnag para que el proyecto Root no tenga que hacerlo
- Se agrega el Badge de InnerSourceReady
- Se cambia la interface Module a la clase Module que automaticamente genera y verifica una extension de prendido y apagado de todos los modulos
- Se le da la funcionalidad al BuildScanModule de aplicar el Plugin de Gradle Enterprise
- Se agrega el modulo DexCount para eliminarlo de los repositorios y configurarlo en los plugins

# 13.0.10
- Se agregan las bases para apuntar a InnerSource Ready
- Usamos libraryName en lugar de versionName
- Se solucionan los problemas al linetar multiples modulos al mismo tiempo
- Se divide la responsabilida del Lintable module de lintear Apps y Librerias llevandola a su modulo correspondiente
- Al momento de generar los Publish se almaceda el timestamp para que todas las versiones tengan el mismo
- Se agrega la documentacion para eliminar los issues de DeteKt
- Se modifica el pre-commit-config.yaml segun nos comenta el equipo de Code Quality por un issue de que DeteKt no genera reporte
- Se hace que el archivo de detekt se genere dentro del build
- Se detecta automaticamente si los test estan en CI o en Local

# 13.0.9 (unpublished)
- Se mejora el rendimiento de los tests

# 13.0.8
- Se elimina el operador !! del group al almacenar una dependencia 
- Se reinicia la flag de Lint cada vez que se llama la task
- Se agrega documentacion al codigo para cumplir con el DeteKt

# 13.0.7
- Se elimina el operador !! de la version al almacenar una dependencia ya que los bundles no tienen version

# 13.0.6
- Por problemas al publicar la version debemos agregar la variable "libraryVersion"

# 13.0.5 (unpublished)
- Soporte para evaluación de Regrex de la allowlist

# 13.0.4
- Se agrega un fix al Linteo del Plugin
- Se evita que la task pluginDescription se ejecute sola
- Se eliman las variables duplicadas de Gradle
- Se evita que el settings gradle tenga dependencias
- Se cambia el termino WhiteList a AllowList

# 13.0.3 (unpublished)
- se elimina las experimentales en 13.0.1 y 13.0.2
- se agrega webhook code quality

# 13.0.2
- Se genera la version que depende 13.0.1 elimanando los implementation que tienen experimental transitivos

# 13.0.1
- Se genera la version que depende 13.0.0 eliminando los implementation que tienen experimental explicitos

# 13.0.0
- Requiere Gradle 7.+
- Se agrega kotlin DSL a todos los modulos
- Se agregan los skeletons de los modulos para App y Library, con sus correspondientes android apply 
- Migro build.gradle del modulo Base a .kts
- Muevo afuera logica relacionada al alta de los repositorios del BasePlugin 
- Agrego el file lib.versions.toml para declaracion de variables en el repo, y cambio todas las referencias para usarlo.
- Agrego el extension de configuracion para la app
- Migracion Groovy a Kotlin
- Se generan 3 mudulos que contienen plugins, baseplugin, app y library
- Se agregan test unitarios y de integracion

# 12.2.0
- Se corrige el path del resource del keystore para no romper en Windows.
- 
# 12.1.0
- Se agregar check para "compile", "implementation" y api en variant para soportar AGP7

# 12.0.1
- Se genera nuevamente el release por un fallo en ci que no soportaba gradle 7.
- Update gradle 7.4

# 12.0.0
- Se migra a gradle 7.3.3
- remove smartlook repo

# 11.5.0
## Modificado
- Se habilita por defecto la publicacion de los build en gradle enterprise.
- Config cleaning
- Pase el code-formatter en unos files, no lo hago en todos para que no sea tan grande el cambio

# 11.4.0
## Modificado
- Se reemplazan todos los repositorios externos por proxies de Nexus. Hacemos esto JCenter presentó downtime y nos sacó tiempo de desarrollo. Por eso vamos a usar la cache del Nexus y como fallback estos repositorios.

# 11.3.0
## Cambiado
- Remove WARNING: variant.getJavaCompile ()' is obsolete....
- Remoce WARNING: variantOutput.getPackageLibrary()' is obsolete...

# 11.2.1
## Modificado
- Se agrega com.mercadoenvios a los regex validos para busqueda en Nexus y local.

# 11.2.0
## Modificado
- Se utiliza una manera de obtener credenciales compatible con Gradle 5, que aún es utilizado por algunas aplicaciones Android

# 11.1.0
## Modificado
- Cleaning SCA and other lint configurations

# 11.0.1
## Cambiado
- Se utilizan correctamente las credenciales propias de Experimental. Ya no es necesario setear la variable de entorno NEXUS_DEPLOYER_USER y NEXUS_DEPLOYER_PASSWORD.

# 11.0.0
## Cambiado
- Se cambia la manera de obtener las credenciales de los repositories de read. Ahora se requiere de las credenciales seteadas en el gradle.properties global.

# 10.6.0
## Cambiado
- Fix comparacion por Gstring vs String. Esto hacia que las dependencia tipo variant no se agreguen al pom xml. Ejemplo `releaseImplementation group:artifact:version`

# 10.5.0
## Modificado
- Se deshabilita la publicacion de los build en gradle enterprise.

# 10.4.0
## Agregado
- Se agrega la posibilidad de excluir files para el reporte de JaCoCo a través de un DSL declarado en el `build.gradle` del módulo:
```groovy
jacocoConfiguration {
    excludeList = ['**/YourClass**']
}
```

# 10.3.0
## Eliminado
- Se elimina plugin 'com.github.dcendents.android-maven' y repositorio de datami que nos son usados.

# 10.2.0
## Cambiado
- Rotación de passwords de Nexus por leak

# 10.1.0
## Cambiado
- Se vuelve a ser compliance con Configuration Avoidance en la lógica de exclusión de Findbugs y Spotbugs
- se agrega el nuevo repo nexus de ML y se elimina el obsoleto bintray
## Agregado
- Validacion y warning de libs que estan deprecadas en la whitelist 

# 10.0.7
## Agregado
- Se deshabilita Spotbugs y Findbugs dado que no funcionan correctamente. Ésta última ya estaba previamente deshabilitada

# 10.0.6
## Eliminado
- Se elimina el flujo que tenia problemas con gradle 4.2 en apps, que se usaba para fontela.

# 10.0.5
## Arreglado
- Se agrega temporalmente el disable de 'LintError' para las lintOptions de AGP dado que hay un problema de compatibilidad entre AGP y SCA en sus archivos de configuración del lint

# 10.0.4
## Arreglado
- Se agrega temporalmente el disable de 'LintError' para las lintOptions de AGP dado que hay un problema de compatibilidad entre AGP y SCA en sus archivos de configuración del lint

# 10.0.3
## Arreglado
- Se agrega temporalmente el disable de 'LintError' para las lintOptions de AGP dado que hay un problema de compatibilidad entre AGP y SCA en sus archivos de configuración del lint

# 10.0.2
## Arreglado
- Se arrelgan los paths de los repositories dado que estaban invertidos

# 10.0.1
- Mal deploy, re run create-version

# 10.0.0
## Eliminado
- Se elimina el Bintray Gradle Plugin junto a sus implementaciones dado que el 01/05/2021 Bintray da de baja sus servicios

## Cambiado
- Se modifican la publicación para soportar el Maven Gradle Plugin hacia un servicio Nexus privado interno
- Se modifican los repositorios de Bintray que se inyectan a los repositorios, reemplazandolos por los del Nexus

# 9.4.0
## Agregado
- Bump gradle 6.7
- FS Watch enabled by default

# 9.3.0
## Agregado
- Se agregó la task `publishPublicRelease`
- Se agregó la task `publishPrivateRelease` que reemplaza a `publishRelease` aunque mantenemos ambas por retrocompatibilidad.

# 9.2.0
## Migrado
- Release process migration

# 9.0.0
- Migracion a Gradle 6. Ahora gradle entreprise plugin tiene que ser aplicado en settings para proyectos con gradle 6.

# 8.6.0
## Agregado
- Se agrega un repositorio para resolver el SDK de Smartlook

# 8.5.2
## Arreglado
- Se cambio la evaluacion de la regex de dependencias de la whitelist

# 8.5.1
## Arreglado
- Se hace re-deploy del plugin porque estabamos deployando con un Java mas nuevo del que necesitabamos

# 8.5.0
## Agregado
- Se agrega filtro por tipo de projecto para la task `listProjects`

# 8.4.0
## Arreglado
- Se agrega lint checkDependencies para los application modules, asi podemos tener un report mejor de lint. ej UnusedResources

# 8.3.0
- Fix shadowing de la variable de instancia

# 8.2.0
## Arreglado
- Fix task unpackKeystoreTask. Se agrega output para poder aprovechar incremental builds: Más [info](https://blog.gradle.org/introducing-incremental-build-support)

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
