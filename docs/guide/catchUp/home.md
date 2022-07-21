# Introducción

Como lo dice en su nombre este Plugin está enfocado en Gradle. Se generó en arquitectura en búsqueda de centralizar
todas las variables utilizadas para configurar un repositorio y sus módulos concluyendo en un repositorio que como base
compile, también brindando funcionalidades tales como la de jacoco, linteo, etc. esto complementando el entorno de
trabajo para los desarrolladores y sus librerías.

----

# Responsabilidades

##### ¿Para qué es cada Plugin?

El proyecto contiene 3 Plugins que están pensados para captar todos los casos de uso en donde se necesita configurar un
proyecto.

Siendo el primero el `BasePlugin`, que se agrega al `Settings.gradle` y al `Build.gradle` del Root Project, el segundo
siendo `BaseLibraryPlugin` encargado de configurar los módulos que contengan librerías y por último el `BaseAppPlugin`
encargado de configurar los módulos que contengan Apps, ya sean productivas o de testing.

##### ¿Cómo veo que aplican los plugins?

Para reconocer las funciones que implementan los Plugins a los módulos o proyectos que aplican
brindamos la tasks llamada mediante el siguiente comando `./gradlew pluginDescription`
que muestra enumera las acciones que realiza y donde podrás informarte de los últimos valores.

----

# Empezando con los Plugins de Gradle

## Requisitos

Se necesita de Gradle 7 para ser capaces de implementar el plugin de gradle.

## Implementación

Para poder utilizar alguno de los Plugin de gradle deberemos agregar sus classpath al build.gradle (En el caso del BasePlugin también en el Settings.gradle)
El BasePlugin se encarga de configurar el Root, luego dependiendo de los módulos que contengan agregaremos el BaseAppPlugin y el BaseLibraryPlugin, el primero siendo para TestApps y el segundo para Librerías.

```gradle
buildscript {
  dependencies {
    classpath "com.mercadolibre.android.gradle:base:<latest_version>"
    classpath "com.mercadolibre.android.gradle:app:<latest_version>"
    classpath "com.mercadolibre.android.gradle:library:<latest_version>"
  }
}
```

### BasePlugin
El BasePlugin deberá ser aplicado en el Settings.gradle y el Build.gradle del proyecto root
```gradle
apply plugin: 'mercadolibre.gradle.config.settings'
```

### BaseAppPlugin
Siendo el caso de que exista un módulo de App o Test App se deberá aplicar en su respectivo Build.gradle
```gradle
apply plugin: 'mercadolibre.gradle.config.app'
```

### BaseLibraryPlugin
Siendo el caso de que exista un módulo de Librería se deberá aplicar en su respectivo Build.gradle
```gradle
apply plugin: 'mercadolibre.gradle.config.library'
```

----

# Funcionamiento

Los plugins utilizan una arquitectura la cual plantea diferentes Configurers, encargados de configurar un aspecto del 
módulo o proyecto en donde están siendo aplicados. Esto nos permite saber a donde a apuntar nuestros cambios al momento
de agregar contenido al Plugin, a continuación una descripción de cada uno.

##### Android Configurer
`AndroidConfigurer` aplica las variables necesarias para que un módulo Android pueda compilar.

    - Variables
        - Api Sdk Level
        - Min Sdk Level
        - Build Tools Version
        - Java Version

##### Basics Configurer
`BasicsConfigurer` brinda los repositorios necesarios para que el proyecto obtenga sus dependencias
y configura la forma en que se administrarán las versiones dinámicas.

##### Module Configurer
`ModuleConfigurer` llama a los módulos indicados para cada tipo de módulo o proyecto para que brinden su funcionalidad.

##### Extensions Configurer
`ExtensionConfigurer` genera las extensiones que los módulos le solicitan para funcionar correctamente.

##### Plugin Configurer
`PluginConfigurer` aplica los plugins en los módulos para que puedan cumplir su rol correctamente, siendo el caso de una
librería o de una aplicación.