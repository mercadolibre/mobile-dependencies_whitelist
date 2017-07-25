MercadoLibre Mobile Gradle Plugin
==============================

[![Build Status](https://travis-ci.com/mercadolibre/mobile-android_gradle.svg?token=cqMzpxLsVioEuXgqEi7v&branch=develop)](https://travis-ci.com/mercadolibre/mobile-android_gradle) 

Plugin de gradle que configura y agregar tasks para hacerle la vida mas facil al developer mobile.

## Getting started

Agregamos al classpath de android el plugin con la ultima version (ver los releases de GH)

```java
buildscript {
  dependencies {
    classpath "com.mercadolibre.android.gradle:base:<latest_version>"
  }
}
```

Y lo aplicamos en el root `build.gradle` del proyecto
```gradle
apply plugin: 'mercadolibre-mobile'
```

Listo. Con eso el plugin ya va a hacer lo suyo.

## Modulos

El plugin trae muchos modulos internos con distintos features, algunos de ellos tienen un poco de customizacion para que el user aplique.

### Publicacion

Debemos agregar en cada proyecto publicable la property `group` y `version`. El artifact va a ser el nombre del modulo (podriamos tranquilamente cambiarlo tambien, pero si va a ser el mismo no hace falta)

Una forma para hacerlo de una en todos, si todos tienen la misma version es en el root `build.gradle`:
```
allprojects {
  group = libraryGroup
  version = libraryVersion
  // Si queremos cambiar los names tambien, por default agarra el nombre de cada modulo:
  name = mapOfModuleToArtifact[name]
}
```

Las tasks creadas son de la forma:
- :publish\<Packaging>\<Type>\<Variant>\<Flavor> // Si no hay flavors, va a ser publishAar\<X>\<Variant>
- :publish\<Packaging>\<Type> // Va a publicar para el variant **release** todos los flavors que haya
- :publish\<Type> // Va a publicar para el variant **release** todos los flavors que haya, detecta solo el packaging

Donde:
\<Packaging>: aar - jar
\<Type>: Local - Release - Alpha - Experimental
\<Flavor>: Definido por cada uno, si no tiene la aplicacion, desestimarlo
\<Variant>: Debug - Release. El developer puede agregar mas, asi que tambien es definido por cada uno.

Notas a tener en cuenta:
- Podemos correr sobre el root cualquiera de ellos, y si hay mas de un modulo que lo tiene va a publicar ambos. Es decir, si corremos `./gradlew publishRelease` va a publicar todos los modulos en todos los flavors en Release.
- Soporta tanto proyectos AAR como JAR. 
- Soporta flavors, variants y buildTypes para proyectos de Android. Para proyectos Java soporta sourceSets (serian los flavors de Java)
- Soporta cualquier superset de java, es decir que si tenemos codigo Kotlin, lo va a agregar.
- En caso de tener dependencias locales, agregarlas como `compile project(path: '...')` y automaticamente nosotros vamos a referenciarlas como corresponde.
- En caso de haber productFlavors, los mismos para diferenciarlos en una misma publicacion van a tener su nombre como prefijo en la version "Ejemplo com.mercadolibre.group:artifact:flavorA-1.0.0"

### Lint

Se agregan lints especificos. Hay un closure sobre cada proyecto donde podemos habilitarlo o deshabilitarlo (y tiene ciertas configuraciones especificas de cada lint)

```gradle
lintConfiguration {
  enabled = true
  dependencyWhitelistUrl = "https://raw.githubusercontent.com/mercadolibre/mobile-dependencies_whitelist/master/android-whitelist.json" // Si alguien distinto a Meli quiere su whitelist, deberia cambiar esto
}
```

Este modulo genera la task `lintGradle`, la cual aplica todos los lints. De ser ejecutada especificamente va a Fallar en caso de error. Sino (por ejemplo corriendo `assemble`/`lint`/`bundleRelease`, si falla no va a parar la ejecucion)

### Jacoco

Se agrega plugin de jacoco, el mismo permite tener reportes para cada task de test del proyecto. El mismo creara una task `jacocoFullReport` la cual corre todos los tests posibles (para cada variant/buildType/flavor/sourceSet) y genera los reportes en formatos detectables por las aplicaciones de coverage.

### Locks

Se agrega una task `lockVersions` para coder lockear versiones dinamicas a estaticas correctamente.

Ademas, el mismo generara ciertas tasks para manipular los locks de manera mas facil, por ejemplo `updateLock`/`deleteLock`/etc. Para mas informacion de las mismas se puede ver el plugin de Nebula.
