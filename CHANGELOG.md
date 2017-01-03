### Changelog

#### v3.4.0
- Se fixea bug al resolver el nombre de una version alpha.
- Se agrega feature e integracion con el plugin de nebula para hacer locks a las dependencias y poder usarlas de forma dinamica en develop, pero estatica en release.
- Se fixea bug para saber si se esta publicando y no se agrega un prefijo.
- Se agrega modulo para hacer lint sobre los gradle.
- Se agrega lint para las dependencias permitidas de un proyecto, aceptando unicamente las existentes en https://github.com/mercadolibre/mobile-dependencies_whitelist
