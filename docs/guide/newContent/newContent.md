# ¿Cómo empiezo?

#### ¿Dónde agrego mis cambios?
El primer paso que debemos dar es conocer si necesitamos un nuevo configurer o un nuevo módulo, teniendo en cuenta que
Los `Configurer` se encargan de las configuraciones y los `Módulos` agregan funcionalidades. Segundo debemos encontrar
el plugin que queremos modificar en base al impacto que buscamos, por ejemplo si necesitamos una configuración en una
librería iremos al Library Plugin, si es de una app al App Plugin y si es del root o respecta a libreria y app
iría al BasePlugin.

----

## Crear un configurador
En caso de que queremos apuntar a configurar un aspecto del repositorio que no tiene un configurer podemos generar el 
nuevo contenido dentro de `anyPlugin/core/action/configurers` para luego heredar de la clase `Configurer` y empezar a 
crear.

```kotlin
interface Configurer {
    fun getDescription(): String // Este método nos brinda una descripción del nuevo funcionamiento que se propone
    fun configureProject(project: Project) // Esta será la función llamada por el plugin y donde podrás agregar la funcionalidad
}
```

Próximamente agregarlo a una de las listas `Configurers` que
contiene cada uno de los plugins, para esto deberemos saber qué impacto buscamos, siendo el base el proyecto root,
Library para librerías y app para aplicaciones productivas como también test apps.

----

## Crear un módulo
Para que podamos ejecutar el nuevo contenido mediante el `ModuleConfigurer` deberemos heredar de la clase `Module`.
Dentro de la ruta `anyPlugin/core/action/modules` generamos una carpeta y agregamos los archivos que sean necesarios.

```kotlin
class Module {
    fun executeInAfterEvaluate(): Boolean = true // Esta es la función que nos permite cambiar el tiempo de ejecución de nuestro módulo.
    fun configure(project: Project) // Esta será la función llamada por el plugin y donde podrás agregar la funcionalidad.
}
```

Próximamente debemos buscar el `ModuleProvider` del plugin, dentro de él existe una lista donde se recorren todos los
módulos, solicitando que apliquen su funcionalidad. Para que el nuevo contenido sea llamada deberá estar en esa lista.

```kotlin
  internal object ModuleProvider {

  private val androidPluginModules =
      listOf(
          NewModule() // Agregamos el nuevo modulo a la lista
      )
```