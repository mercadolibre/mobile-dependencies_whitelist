# Todas las dependencias a proponer
- - "com.somepackage.somelib:submodule:4.2.0"
...

### ¿Afecta al start-up time de alguna forma?
- _No, mi Lib no requiere inicialización en el `Application`_

### ¿Utiliza libs nativas? ¿Tiene soporte para las diferentes arquitecturas de devices?
- [ ]  _No, xxLib no tiene código con NDK_

### Versiones mínimas y máximas del sistema operativo soportadas
- [ ] _Tiene Min API level xx_

### Impacto en el peso de descarga e instalación de la app
- [ ] _Example module está pesando 14kb y xxLib para la versión 4.2.0 ~4 terabytes._

# Libs internas (borrar todo este bloque si el PR es para una lib externa)

### Contextos
- [ ] Ya agregué y/o actualicé el contexto en la [context-whitelist](https://github.com/mercadolibre/mobile-dependencies_whitelist/blob/master/context-whitelist.json)

### Configuración para el SLA de Crashes
El proyecto en Jira en el que se van a crear los crashes que ocurran es: **_${SPYN}_**
- [ ] Ya está la configuración hecha.
- [ ] Necesito que me ayuden a configurarlo.

[¿Qué es el SLA de Crashes?](https://sites.google.com/mercadolibre.com/mobile/release-process/seguimiento-de-errores)

# Libs externas
[Tienen que completar el form que esta en la wiki](https://sites.google.com/mercadolibre.com/mobile/arquitectura/libs-utilitarias/libs-externas)

### PRs abiertos con este Caso de uso
- [example PR](www.github.com/mercadolibre)

### Repositorios afectados
- [example fend](www.github.com/mercadolibre)

## En qué apps impacta mi dependencia
- [ ] Mercado Libre
- [ ] Mercado Pago
- [ ] SmartPOS
- [ ] Alicia: Flex / Logistics
- [ ] WMS
- [ ] Meli Store

## Documentación para otros equipos en la sección de libs 
Libs [internas](https://sites.google.com/mercadolibre.com/mobile/arquitectura/libs-utilitarias/libs-internas) o [externas](https://sites.google.com/mercadolibre.com/mobile/arquitectura/libs-utilitarias/libs-externas) en la wiki de Mobile

- [ ] Ya existe, no tengo que agregar ni modificar nada.
- [ ] Hay que agregar lo que pongo a continuación... 👇