### Changelog

#### v3.1.1
- Arreglo la publicación a EXPERIMENTAL/LOCAL por que **no** estaba subiendo el archivo .aar.

#### v3.1.0
- Logueamos por consola el *groupId:module:version* de lo que se esta publicando, tanto para local, como experimental o release.

#### v3.0.x
- Cuando publicamos local o experimental compilamos la version release en lugar de la debug.
- Mejoramos la manera es que se obtiene el número de version del módulo a publicar por que podía romperse dependiendo de la configuración del cliente.
- Agregamos soporte para publicar automáticamente las bibliotecas ([HowTo](https://github.com/mercadolibre/mobile-cd/wiki/Publicación-de-libs-en-Android)).