MercadoLibre Mobile Gradle Plugin
==============================

[![Build Status](https://travis-ci.com/mercadolibre/mobile-android_gradle.svg?token=cqMzpxLsVioEuXgqEi7v&branch=develop)](https://travis-ci.com/mercadolibre/mobile-android_gradle) 

Los Plugins de gradle son los encargados de realizar las configuraciones que deberia tener todo repositorio asi consigueindo centralizar funcionalidades como tambien facilitar la vida de los desarrolladores.

## Getting started

Para podes utilizar alguno de los Plugin de gradle deberemos agregar sus classpath al build.gradle (En el caso del BasePlugin tambien en el Settings.gradle)
El BasePlugin se encarga de configurar el Root siempre es necesario, luego dependendiendo de los modulos que contengan agregaremos el BaseAppPlugin y el BaseLibraryPlugin, el primero siendo para TestApps y el segundo para Librarias Android.

```java
buildscript {
  dependencies {
    classpath "com.mercadolibre.android.gradle:BasePlugin:<latest_version>"
    classpath "com.mercadolibre.android.gradle:baseAppPlugin:<latest_version>"
    classpath "com.mercadolibre.android.gradle:baseLibraryPlugin:<latest_version>"
  }
}
```

### BasePlugin
El BasePlugin debera ser aplicado en el Settings.gradle y el Build.gradle del proyecto root
```gradle
apply plugin: 'mercadolibre.gradle.config.settings'
```

### BaseAppPlugin
Siendo el caso de que exista un modulo de App o Test App se debera aplicar en su respectivo Build.gradle
```gradle
apply plugin: 'mercadolibre.gradle.config.app'
```

### BaseLibraryPlugin
Siendo el caso de que exista un modulo de Libreria se debera aplicar en su respectivo Build.gradle
```gradle
apply plugin: 'mercadolibre.gradle.config.library'
```

Listo. Con eso el plugin ya va a hacer lo suyo.

## Modulos

El plugin trae muchos modulos internos con distintos features, algunos de ellos tienen un poco de customizacion para que el user aplique, en caso de querer usarlos.

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
- :publish\<Packaging>\<Type>\<BuildType>\<Flavor> // Si no hay flavors, va a ser publish\<Packaging>\<Type>\<BuildType>
- :publish\<Packaging>\<Type> // Va a publicar para el BuildType **release** todos los flavors que haya
- :publish\<Type> // Va a publicar para el BuildType **release** todos los flavors que haya, detecta solo el packaging

Donde:

- \<Packaging>: aar - jar
- \<Type>: Local - Release - Experimental
- \<Flavor>: Definido por cada uno, si no tiene la aplicacion, desestimarlo
- \<BuildType>: Debug - Release. El developer puede agregar mas, asi que tambien es definido por cada uno.

Notas a tener en cuenta:
- Podemos correr sobre el root cualquiera de ellos, y si hay mas de un modulo que lo tiene va a publicar ambos. Es decir, si corremos `./gradlew publishRelease` va a publicar todos los modulos en todos los flavors en Release.
- Soporta tanto proyectos AAR como JAR. 
- Soporta flavors, buildTypes dentro de cada uno para proyectos de Android. Para proyectos Java soporta sourceSets (serian los flavors de Java)
- Soporta cualquier superset de java, es decir que si tenemos codigo Kotlin, lo va a agregar.
- En caso de tener dependencias locales, agregarlas como `implementation project(path: '...')` y automaticamente nosotros vamos a referenciarlas como corresponde.
- En caso de haber productFlavors, los mismos para diferenciarlos en una misma publicacion van a tener su nombre como prefijo en la version "Ejemplo com.mercadolibre.group:artifact:flavorA-1.0.0"

### Lint

Se agregan lints especificos. Hay un closure sobre cada proyecto donde podemos habilitarlo o deshabilitarlo (y tiene ciertas configuraciones especificas de cada lint)

```gradle
lintGradle {
  enabled = true
  dependenciesLintEnabled = true
  releaseDependenciesLintEnabled = true
  dependencyAllowListUrl = "https://raw.githubusercontent.com/mercadolibre/mobile-dependencies_whitelist/master/android-whitelist.json" // Si alguien distinto a Meli quiere su allowlist, deberia cambiar esto
}
```

Este modulo genera la task `lintGradle`, la cual aplica todos los lints. La misma se hookea a la task `check`.

### Jacoco

Se agrega plugin de jacoco, el mismo permite tener reportes para cada task de test del proyecto. El mismo creara una task `jacocoFullReport` la cual corre todos los tests posibles (para cada buildType/flavor/sourceSet) y genera los reportes en formatos detectables por las aplicaciones de coverage.
