# ¿Cómo empiezo?

#### ¿Dónde agrego mis cambios?
El primer paso que debemos dar es conocer si necesitamos un nuevo configurer o un nuevo módulo, teniendo en cuenta que
Los `Configurer` se encargan de las configuraciones y los `Módulos` agregan funcionalidades. Segundo debemos encontrar
el plugin que queremos modificar en base al impacto que buscamos, por ejemplo si necesitamos una configuración en una
librería iremos al BaseLibraryPlugin, si es de una app al BaseAppPlugin y si es del root o respecta a libreria y app
iría al BasePlugin.

----

## Crear un configurador
Para que podamos ejecutar el nuevo contenido mediante uno de los Plugins deberemos heredar de la clase `Configurer`.
Luego dentro de la ruta `core/action/configurers` (BasePlugin por ejemplo) podremos generar el archivo.

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
Dentro de la ruta core/action/modules (BasePlugin por ejemplo) generamos una carpeta y agregamos los archivos que sean
necesarios.

```kotlin
interface Module {
    fun configure(project: Project) // Esta sera la funcion llamada por el plugin y donde podrás agregar la funcionalidad
}
```

Proximamente agregaremos el módulo a su lista respectiva lo que hará que cuando el `ModuleConfigurer` configure un módulo o proyecto
sea ejecutada su configuración. Por último deberemos agregar los test de la funcionalidad para cumplir con el coverage.

## ¿Cómo se manejan las variables?

Teniendo en cuenta que muchas de las configuraciones o funcionalidades necesitan `Strings`, `Ints`, `Booleans`, etc.
dentro del `BasePlugin` existe un archivo llamado `CONSTANS.kt` donde podrás agregar tus variables para luego importarlas
en el plugin que necesites.


