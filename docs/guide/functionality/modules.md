# Los Módulos

Los módulos complementan el entorno de desarrollo, agregando funcionalidades, siendo enlistados por el `ModuleProvider`
y ejecutados por el `ModuleConfigurer` de cada plugin.

Cada uno de los módulos hereda de la clase `Module` que brinda el funcionamiento de generar una extensión de tipo
`ModuleOnOffExtension` permitiendonos bloquear la ejecución de un modulo totalmente, para encontrar el nombre de la extensión
de cada uno de los módulos podemos ejecutar la tarea:

```gradle
./gradlew pluginDescription
```

Para luego dentro del build.gradle del proyecto que queramos afectar agreguemos ese nombre con la flag `enabled` en false

```gradle
    anyModuleExtension {
        enabled = false
    }
```

A continuación brindamos una lista con la descripción de cada uno:

### Build Scan

Este módulo brinda la funcionalidad que Gradle Enterprise nos permite, publicar y almacenar los `builds`, para que
nosotros u otros desarrolladores puedan acceder a ellos y analizarlos en remoto.

### Jacoco

Este módulo brinda la funcionalidad de los reportes de coverage, que podemos utilizar nosotros y también se van a revisar
en los checks de Github, se crearán diferentes tasks pero la principal será la de `jacocoFullReport` que ejecuta todos
los tests posibles (para cada buildType/flavor/sourceSet) y genera los reportes en formatos detectables por las
aplicaciones de coverage.

### Lint

Este módulo brinda la funcionalidad de verificación de dependencias, teniendo en cuenta esto somos capaces de tener una
Allow List donde tendremos todas los package y módulos que son admitidos dentro de los repositorios de meli. Siendo así
También contamos con las variables para desactivar el linteo y cambiar el archivo de donde traemos la Allow List.

```groovy
lintGradle {
    dependenciesLintEnabled = true
    releaseDependenciesLintEnabled = true
    dependencyAllowListUrl = "https://raw.githubusercontent.com/mercadolibre/mobile-dependencies_whitelist/master/android-whitelist.json" // Si alguien distinto a Meli quiere su whitelist, deberia cambiar esto
}
```

### Publishable

Este módulo brinda la funcionalidad de realizar publicaciones locales, para uso únicamente en tu terminal, y Experimentales
siendo estos capaces de ser ejecutados en otras terminales.

Para ser capaces de publicar nuestros módulos debemos agregar en cada proyecto publicable la property `group` y `versión`.
El artifact va a ser el nombre del módulo (podríamos tranquilamente cambiarlo tambien, pero si va a ser el mismo no hace falta)

Una forma para configurar todos los módulos una única vez, teniendo en cuenta que todos tienen la misma versión es en el root `build.gradle`:
```
allprojects {
  group = libraryGroup
  version = libraryVersion
  // Si queremos cambiar los names también, por default agarra el nombre de cada módulo:
  name = mapOfModuleToArtifact[name]
}
```

El nombre de las tasks utilizadas para publicar los módulos se genera de la siguiente forma:
- :publish\<Packaging>\<Type>\<BuildType>\<Flavor> // Si no hay flavors, va a ser publish\<Packaging>\<Type>\<BuildType>
- :publish\<Packaging>\<Type> // Va a publicar para el BuildType **release** todos los flavors que haya
- :publish\<Type> // Va a publicar para el BuildType **release** todos los flavors que haya, detecta solo el packaging

Teniendo en cuenta los siguientes valores:

- \<Packaging>: aar - jar
- \<Type>: Local - Release - Experimental
- \<Flavor>: Definido por cada uno, si no tiene la aplicación, desestimarse
- \<BuildType>: Debug - Release. El developer puede agregar más, así que también es definido por cada uno.

Es importante tener en cuenta lo siguiente:
- Podemos correr sobre el root cualquiera de ellos, y si hay más de un módulo que lo tiene va a publicar ambos. Es decir, si corremos `./gradlew publishRelease` va a publicar todos los módulos en todos los flavors en Release.
- Soporta tanto proyectos AAR como JAR.
- Soporta flavors, buildTypes dentro de cada uno para proyectos de Android. Para proyectos Java soporta sourceSets (serían los flavors de Java)
- Soporta cualquier superset de java, es decir que si tenemos código Kotlin, lo va a agregar.
- En caso de tener dependencias locales, agregarlas como `implementation project(path: '...')` y automáticamente nosotros vamos a referenciarlas como corresponde.
- En caso de haber productFlavors, los mismos para diferenciarlos en una misma publicación van a tener su nombre como prefijo en la version "Ejemplo com.mercadolibre.group:artifact:flavorA-1.0.0"

### Bugsnag

Este módulo brinda la funcionalidad de implementar el detector de errores bugs nag para que luego podremos analizar los
problemas que surgen.

### DexCount

Este módulo brinda la funcionalidad de enumerar las funciones, clases y otros valores mediante el plugin de DexCount.

### List Projects

Este módulo brinda la funcionalidad de `listProjects` que enlista los proyectos que contiene el repositorio, para que
frameworks externos al proyecto puedan reconocerlos.

### Project Version

Este módulo brinda la funcionalidad de `getProjectVersion` que genera un archivo con la versión del proyecto, para que
frameworks externos al proyecto puedan reconocerlos.

### List Variants

Este módulo brinda la funcionalidad de `listVariants` que enlista las variantes que tiene cada módulo dentro del
repositorio.

### Project Info

Este módulo brinda la funcionalidad de `projectInfo` que enlista las configuraciones aplicadas en el proyecto y en cada
uno de los módulos.

### Plugin Description

Este modulo brinda la funcionalidad de `pluginDescription` que enlista las configuraciones y módulos que son aplicados
por cada plugin dentro del proyecto. Para tener más contexto de la funcionalidad que manejan los plugins.

# Herencia de Módulos

Existen casos donde se necesita que un módulo sea aplicado de una forma particular pero respete la misma funcionalidad
siendo así podemos heredar de los `Módulos` que necesitemos dentro del `Plugin` que necesite su propia implementación y
agregarlo a su propia lista de módulos.