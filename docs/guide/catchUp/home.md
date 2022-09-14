# Introducción

Este repositorio contiene varios Plugins enfocados en Gradle, que nos permiten centralizar configuraciones para que no
tengan que estar en las librerías y aplicaciones.
La arquitectura que se utiliza en los plugins se basa en que cada Plugin tiene una lista de `Configurers` los cuales se
encargar de administrar un aspecto básico del proyecto, existiendo uno que se encarga de los `Modules`, siendo estos
clases encargadas de configurar funcionalidades dentro de los proyectos.

----

# Responsabilidades

##### ¿A que apunta cada plugin?


Dentro del repositorio contamos con varios Plugins, a continuación te comentamos donde se aplica cada uno en caso de ser
necesario:

##### Root

- Base Plugin
  - Este plugin está encargado de configurar el proyecto Root, desde el `/build.gradle`
- Settings Plugin
  - Este plugin está encargado de configurar el proyecto Root, desde el `/settings.gradle`

##### Módulos

- Library
  - Este plugin es el encargado de configurar los módulos de tipo Library, desde su `/module/build.gradle`
- App
  - Este plugin es el encargado de configurar los módulos de tipo App o Test App, desde su `/module/build.gradle`
- Dynamic Features
  - Este plugin es el encargado de configurar los módulos de tipo DynamicFeatures, desde su `df/module/build.gradle`

##### ¿Cómo veo que aplican los plugins?

Para reconocer las funciones que implementan los Plugins a los módulos o proyectos que aplican
brindamos la tasks llamada mediante el siguiente comando `./gradlew pluginDescription`
que muestra enumera las acciones que realiza y donde podrás informarte de los últimos valores.

----

# Empezando con los Plugins de Gradle

## Requisitos

Es necesario contar con Gradle 7 y kotlin 1.5 para implementar los Plugins de Gradle.

## Implementación

Para realizar la implementacion de los Plugins de Gradle como primer paso debemos agregar el `classpath` de los que
que vamos a utilizar, siendo el `Base` y el `Settings` siempre requeridos, y dependiendo los módulos del repositorio podremos
deducir que otro más vamos a declarar.

##### `/build.gradle`
```gradle
buildscript {
  dependencies {
    classpath "com.mercadolibre.android.gradle:base:<latest_version>"
    classpath "com.mercadolibre.android.gradle:app:<latest_version>"
    classpath "com.mercadolibre.android.gradle:library:<latest_version>"
    classpath "com.mercadolibre.android.gradle:dynamicfeatures:<latest_version>"
  }
}
```

##### `/settings.gradle`
```gradle
buildscript {
  dependencies {
    classpath "com.mercadolibre.android.gradle:base:<latest_version>"
  }
}
```

### BasePlugin `/build.gradle`
El Base Plugin se aplica dentro de la siguiente forma:
```gradle
apply plugin: 'mercadolibre.gradle.config.base'
```

### SettingsPlugin `/build.gradle`
El Settings Plugin se aplica dentro de la siguiente forma:
```gradle
apply plugin: 'mercadolibre.gradle.config.settings'
```

### AppPlugin `/module/build.gradle`
El App Plugin se aplica dentro de la siguiente forma:
```gradle
apply plugin: 'mercadolibre.gradle.config.app'
```

### LibraryPlugin `/module/build.gradle`
El Library Plugin se aplica dentro de la siguiente forma:
```gradle
apply plugin: 'mercadolibre.gradle.config.library'
```

### DynamicFeaturesPlugin `df/module/build.gradle`
El Dynamic Features Plugin se aplica dentro de la siguiente forma:
```gradle
apply plugin: 'mercadolibre.gradle.config.dynamicfeatures'
```
----

# Funcionamiento

Existiendo los Configurers que planteamos con la arquitectura podemos saber a donde a apuntar nuestros cambios al momento
de agregar contenido al Plugin, a continuación una descripción de cada uno.

##### Android Configurer
Es el encargado de aministrar la extensión `android` ya sea de un módulo librería, app, o dynamic features. De esta forma
aplicando estas variables para que los repositorios no tengan que hacerlo:

    - Variables
        - Api Sdk Level
        - Min Sdk Level
        - Build Tools Version
        - Java Version

##### Basics Configurer
Es el encargado de brindar los repositorios necesarios para que el proyecto envie informacion al igual que configura
la forma en que se administran las versiones dinámicas.

##### Module Configurer
Es el encargado de llamar a cada uno de los módulos que ingeniamos para que realice la configuración de su funcionalidad
dentro del proyecto al que está apuntando este configurer consiguiendo así complementar el entorno de trabajo del
repositorio.

##### Extensions Configurer
Es el encargado de generar las extensiones de On Off de los módulos para que estos sean capaces de funcionar de una forma
correcta y en caso de que sea necesario se puedan apagar o customizar según las variables que se pasen por su extensión.

##### Plugin Configurer
Es el encargado de aplicar los plugins en los proyectos para que puedan cumplir su rol correctamente. Siendo el caso de una
librería, una app o un dynamicfeature.