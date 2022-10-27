# Tests

Actualmente estamos probando como un plugin de gradle se aplica dentro de un proyecto por lo que es necesario tener acceso
a uno de prueba, siendo asi se brindan las siguientes funciones con diferentes particularidades para lograr el objetivo:


## Primer paso

Es necesario que la clase de testing herede de `AbstractPluginManager` para que tengas acceso a estos métodos.

## Proyecto Real

Con un Gradle Runner y las propiedades de este proyecto se genera un proyecto real capaz de hacer un build, contener,
extensiones y todas las funcionalidades de uno real. Con la siguiente línea podrán generar uno:

```kotlin
    root = moduleManager.createSampleRoot(ROOT_PROJECT, tmpFolder)
```

#### Subprojects

Para que podamos simular que el proyecto root tenga diferentes módulos dentro de él podemos utilizar este funcionamiento:

```kotlin
    projects[APP_PROJECT] = moduleManager.createSampleSubProject(APP_PROJECT, tmpFolder, root)
```

Debemos acceder a la propiedad `projects` y elegir un nombre de módulo, por ejemplo `APP_PROJECT` que es igual a `p2`
luego utilizando el objeto `moduleManager` llamaremos a la función que se muestra pasando nuevamente el nombre del proyecto,
la carpeta temporal donde existe el proyecto y el proyecto root de este módulo.

Por último si queremos acceder al proyecto podemos llamarlo de la forma que lo seteamos:

```kotlin
    projects[APP_PROJECT]!!
```

## Proyecto Mock

Para que podamos generar un mock y luego por ejemplo hacer verificaciones a las funciones con `verify` podremos utilizar
la siguiente función:

```kotlin
    mockedRoot = mockRootProject(listOf())
```

Esta función retorna un objeto de tipo `MockedProjectContent` una clase donde podras acceder a cada una de las propiedades
de un proyecto mockeado y poder modificar funcionamiento con la función `every` siendo asi podremos lograr los test que
necesitemos.

#### Subprojects

Para que podamos simular que el proyecto root tenga diferentes módulos dentro de él podemos agregar el nombre de los módulos a
la lista que solicita el método:

```kotlin
    mockedRoot = mockRootProject(listOf(LIBRARY_PROJECT))
```

Una vez generado podemos llamar al subproyecto de la siguiente forma:

```kotlin
     mockedRoot.subProjects[LIBRARY_PROJECT]!!
```

Y utilizarlo para lograr los test que necesitemos.
