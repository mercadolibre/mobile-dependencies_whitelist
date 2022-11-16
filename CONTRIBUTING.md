# Contribuir en MercadoLibre Mobile Gradle Plugin

¡Gracias por tu interés en contribuir en los Plugins de Gradle!

Si necesitas ayuda para cualquier situación puedes buscar ayuda en los canales [#help-android](https://meli.slack.com/archives/CSKLKAGC8) o contactarte
directamente con los colaboradores que mantienen esta librería mediante el mail [mobile-arq@mercadolibre.com](mobile-arq@mercadolibre.com).

## Cómo contribuir

Toda ayuda es más que bienvenida, existiendo los casos de mejora de codigo, propuestas para eliminar configuraciones
de los repositorios para que sean abarcadas por los diferentes plugins como también nuevos features para el entorno de
de desarrollo de un dev.

Usamos el modelo de "Fork and Pull" que se describe [aquí](https://help.github.com/articles/about-collaborative-development-models/),
en el que los colaboradores envían cambios a su **Fork** personal y crean solicitudes de extracción para llevar esos
cambios al repositorio de origen.

Sus pasos básicos para ponerse en marcha:

- Hacer un Fork de este repositorio y cree una rama desde master para agregar lo que está trabajando.
- Comprométete sobre la marcha siguiendo nuestras convenciones de git.
- Incluya Test non-trivial que cubran todo el código. Las pruebas existentes deben proporcionar una plantilla sobre cómo probar correctamente.
- Asegúrate de que todas las pruebas pasen.
- Se espera que todos los cambios de código cumplan con el formato sugerido por KtLint y la configuración de DeteKt actualmente en uso.
- Envíe sus confirmaciones a GitHub y cree PR que apunte a master.
- Si toma demasiado tiempo entregar el código, siempre cambie la base hacia el maestro antes de solicitar una revisión y evite las confirmaciones de combinación inversa.

* **Feature request**: Las solicitudes serán atendidas dependiendo el músculo disponible como también la necesidad que tenga el cambio.
* **Bug reports**: Cualquier error que sea reportado debe incluir el "Output" del problema qué sucede si califica, el entorno donde fue ecuado
  (CI, Local, Producción, etc) conjunto a la versión del plugin que se está utilizando.
* **Pull Request**: Toda propuesta podrá ser enviada por un PR para que la podamos analizar como también challengear.

### Feature request

Todo lo que necesitas hacer es crear un issue en Github desde este [Link](https://github.com/mercadolibre/fury_mobile-gradle-android/issues/new/choose)
Haciendo que las discusiones tengan lugar antes de que se escriba cualquier código. Una vez que el diseño y la dirección
estén totalmente acordados, el colaborador puede trabajar tranquilamente sabiendo que su cambio será aceptado.

### Bug reports

Si encontraste algún error te invitamos a que nos lo informes en este [Link]([aquí](https://github.com/mercadolibre/fury_mobile-gradle-android/issues/new/choose)).
Si no estás seguro de si algo es un error o no, no dudes en presentarlo de todos modos. Antes de informar un error,
busca los problemas existentes y los PRs, ya que es posible que alguien más ya lo haya reportado.

El Bug debe contener un título y una descripción clara del problema. También debe incluir tanta información relevante
como sea posible y una muestra de código que evidencie el problema. El objetivo de un bug report es facilitar
la reproducción del mismo y el desarrollo de una solución.

Los reportes de errores también se pueden realizar a través de un [PR](#Pull-request) que contenga un test fallido.

### Pull request

En este caso se debe indicar la naturaleza del mismo (Feature/Bug/etc.) para ayudar al equipo a evaluarlo.
Si estás entusiasmado con un feature en particular o si lo necesitas a corto plazo, un PR es usualmente la forma más rápida de
conseguir el cambio requerido.

En el caso de los PRs, siempre es mejor consultar con el owner team antes de afrontar uno, especialmente si es muy grande.
Pasar tiempo en un PR que luego podría ser rechazado debido a discrepancias importantes con la visión o contribuciones en
competencia es una pérdida de tiempo para todas las personas involucradas.

## Coding guidelines

Todos los contribuyentes *deben* seguir los [Coding Guidelines](CODING_GUIDELINES.md).
Las contribuciones que no sigan estos lineamientos no serán tenidas en cuenta hasta que se realicen las modificaciones necesarias.

## Trusted Committers

Los Trusted Committers (TC) son aquellos miembros de nuestro grupo de trabajo que tienen derechos elevados y acceso de escritura directo a este repositorio.

> Los Trusted Committers actúan como administradores del grupo de trabajo y la comunidad. Su objetivo es tomar decisiones basadas en el consenso en el mejor interés del grupo de trabajo.
También actúan como guardianes de este repositorio: los TC reaccionan, arbitran y brindan comentarios sobre las contribuciones entrantes.

Para obtener más información sobre el concepto, consulte también el [Patrón de Trusted Committer](https://github.com/InnerSourceCommons/InnerSourcePatterns/blob/main/patterns/2-structured/trusted-committer.md).

Grupo de confirmadores de confianza Contactos

### [@mmanzanzani](https://app.slack.com/client/T02AJUT0S/D02UDSW04S0)
### [@blaardanaz](https://meli.slack.com/archives/D0317786ACV)
### [@mafunes](https://meli.slack.com/archives/D033Q9JQ4KT)


