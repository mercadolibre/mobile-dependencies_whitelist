### Changelog

#### v3.4.2
- Se agrega las tasks de lock como modo de publicacion activa
- Se modifica en el pom las dependencias para que hagan un mirror a las del lock (si el plugin de lock esta presente)

#### v3.4.1
- Mejoras en los mensajes de error mostrados cuando falla el linteo de dependencias.
- Arreglamos la publicación local por variant. Estábamos publicando binarios del variant `release` pero con los sources de `debug`.

#### v3.4.0
- Se fixea bug al resolver el nombre de una version alpha.
- Se agrega feature e integracion con el plugin de nebula para hacer locks a las dependencias y poder usarlas de forma dinamica en develop, pero estatica en release.
- Se fixea bug para saber si se esta publicando y no se agrega un prefijo.
- Se agrega modulo para hacer lint sobre los gradle.
- Se agrega lint para las dependencias permitidas de un proyecto, aceptando unicamente las existentes en https://github.com/mercadolibre/mobile-dependencies\_whitelist
