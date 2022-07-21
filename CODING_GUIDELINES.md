# Pautas

Creemos que las contribuciones de diferentes developers pueden mejorar la librería.

Sin embargo; absorber todas las contribuciones tal como están, aunque sea conveniente, podría generar dificultades en el
mantenimiento de la base de código se deja sin marcar. Las **Coding Guidelines** a menudo establecen pautas para
los contribuyentes para garantizar que el código permanezca mantenible en el tiempo.

El propósito de esta guía es establecer una línea base para las contribuciones. Estas pautas no pretenden limitar las herramientas
a su disposición ni para reconfigurar su forma de pensar, sino para fomentar el comportamiento de buen vecino.

## Lenguaje

Usamos el idioma **Español** en la documentación e  **Inglés** dentro del código.

Por lo tanto: código fuente, comentarios deberán estar en Inglés.

Los errores tipográficos son inevitables, pero trate de reducirlos usando un corrector ortográfico. La mayoría de los 
IDE se pueden configurar para ejecutar uno automáticamente.

## Código

En términos generales, sea consciente al contribuir e intente seguir el mismo estilo que el código en la aplicación ya
posee. Si tienes alguna duda, ¡Pregúntanos!

Estas reglas se aplicarán automáticamente al realizar una solicitud de extracción, y las comprobaciones fallarán si no las sigue.
resultando en que su contribución sea rechazada automáticamente hasta que se fije.

Utilizamos [Code Quality](https://furydocs.io/code-quality/latest/guide/#/) para mantener alineado el estilo de nuestros proyectos.
Le recomendamos que lea y siga la guía de estilo de [Kotlin](https://furydocs.io/code-quality/latest/guide/#/languages/kotlin).

## Comentarios

Con los comentarios buscamos brindar un contexto de la función, variable o clase que estamos agregado. La idea es que sea
algo resumido pero con el suficiente contenido para brindar un primer entendimiento.

### Branching

Actualmente `master` es nuestra única rama a largo plazo, a continuación algunas sugerencias de nombres de ramas a corto plazo:

* `fix/something-needs-fix`: pequeños parches de rutina en el código para funciones que ya existen.
* `hotfix/something-needs-fix`: parches de rutina en el código que se necesitan lo antes posible. Ej: errores de producción que hace que el usuario
  afectación.
* `feature/something-new`: Una característica nueva o un cambio en una característica existente. Tenga cuidado con los cambios bruscos que
  requieren un aumento de la versión principal.
* `enhancement/improves-documentation-for-this-feature`: si agrega o cambia la documentación sin
  impacto en el código fuente.

### Git

Cada vez que realizamos un commit es necesario tener en cuenta los siguientes puntos.

- El commit debe completar todos los issues de Pre Commit.
- Los nuevos cambios deben completar el coverage de los test.


