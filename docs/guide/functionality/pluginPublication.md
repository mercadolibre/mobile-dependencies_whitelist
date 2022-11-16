## ¿El plugin se auto implementa?

Si, siempre existen dos versiones de los `Plugins` el primero es repositorio donde estamos haciendo nuestros cambios,
y el segundo es el que lo configura. Debido a que el `Módulo` de publicación está dentro del repositorio genera esta
necesidad de auto implementarse para poder publicar versiones.

----

## ¿Cómo detecto cual es cual?

Dentro del `gradle.properties` tendremos la variable `library.version` que contiene la versión de nuestros cambios, y las
variables que contienen el `Plugin` que está configurando el repositorio está en la variable `innerProjectVersion` dentro
del `gradle.properties` y `libs.version.toml`.

----
## Casos importantes

#### Necesito hacer cambios en el `BasePlugin` y eso afecta a los otros plugins o a todo el repositorio

Al momento de necesitar que el segundo `Plugin` (El que configura el repositorio) lo que deberemos hacer es crear un
`experimental` y ponerlo en la variable `innerProjectVersion` asi consiguiendo que el repositorio contenga los cambios que
estamos haciendo debido a que la versión que se implementa es la nuestra.

#### Mi variable `innerProjectVersion` tiene un experimental y la branch `master` cambio

En este caso deberemos hacer un `git pull` para traer los cambios de master y no pisarlos al mergear el PR, para luego
generar un nuevo `experimental` y ponerlo en la variable `innerProjectVersion`.

#### Estoy cambiando los módulos de publicación y además necesito un experimental en `innerProjectVersion`

Hay que tener cuidado de no romper la publicación en la versión que configura el repositorio donde estamos haciendo los cambios
ya que si lo hacemos deberemos volver a una versión que tal vez no tenga los cambios que hicimos haciendo que rompan algunos
de los nuevos `imports` por lo que es recomendable primero hacer los cambios que necesitamos, luego un `experimental`
que nos sirva de seguridad para volver a él cuando necesitemos, y recien ahi empezar a hacer cambios en los módulos de
publicación.



