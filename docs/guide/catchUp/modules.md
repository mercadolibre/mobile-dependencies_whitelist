# Los Módulos

Los módulos aportan herramientas al entorno de desarrollo del developer. A continuación brindamos una lista con cada uno
y sus funcionalidades.

### Build Scan

Gradle Enterprise nos brinda la posibilidad de publicar los `builds` de forma que podamos acceder a ellos y analizarlos
de forma remota como también compartirlos con otros colaboradores. Este Módulo se encarga de agregar esta funcionalidad
al repositorio.

### Jacoco

Se agrega plugin de jacoco, el mismo permite tener reportes para cada task de test del proyecto. El mismo creará una
task `jacocoFullReport` la cual corre todos los tests posibles (para cada buildType/flavor/sourceSet) y genera los
reportes en formatos detectables por las aplicaciones de coverage.

### Lint
Se agregan lints específicos encargados de verificar si existen librerías deprecadas, expiradas o inválidas que están
siendo implementadas en el proyecto. Hay un closure sobre cada proyecto donde podemos habilitar o deshabilitar. (y tiene ciertas configuraciones específicas de cada lint)

```groovy
    lintGradle {
    enabled = true
    dependenciesLintEnabled = true
    releaseDependenciesLintEnabled = true
    dependencyAllowListUrl = "https://raw.githubusercontent.com/mercadolibre/mobile-dependencies_whitelist/master/android-whitelist.json" // Si alguien distinto a Meli quiere su whitelist, deberia cambiar esto
    }
```

### Publishable

Los diferentes plugins te permiten realizar publicaciones ya sean Locales, para uso únicamente en tu pc, o Experimentales
siendo estos capaces de ser ejecutados en otras pc.

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

### List Projects

Este módulo se encarga de generar la task `listProjects` que enlista los proyectos que contiene el repositorio, para que
frameworks externos al proyecto puedan reconocerlos.

### Project Version

Este módulo se encarga de generar la task `getProjectVersion` que genera un archivo con la versión del proyecto, para que
frameworks externos al proyecto puedan reconocerlos.

### List Variants

Este módulo se encarga de generar la task `listVariants` que enlista las variantes que tiene cada modulo dentro del
repositorio.

### Plugin Description

Este módulo se encarga de generar la task `pluginDescription` que enlista las configuraciones y módulos que son aplicados
por cada plugin dentro del proyecto. Para tener más contexto de la funcionalidad que manejan los plugins.

# Herencia de Módulos

Existen casos donde se necesita que un módulo sea aplicado de una forma particular pero respete la misma funcionalidad
siendo así podemos heredar de los `Módulos` que necesitemos dentro del `Plugin` que necesite su propia implementación y
agregarlo a su propia lista de módulos.


