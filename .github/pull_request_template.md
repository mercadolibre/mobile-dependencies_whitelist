# Dependencias a proponer

- "com.squareup.okhttp3:okhttp:4.2.0"
- "com.mercadolibre.android.example:module:1.+"


### ¿Afecta al start-up time de alguna forma?

_No, OkHttp no requiere inicialización en el `Application`_

### ¿Utiliza libs nativas? ¿Tiene soporte para las diferentes arquitecturas de devices?

_No, OkHttp no tiene código con NDK_

### Versiones mínimas y máximas del sistema operativo soportadas

_Tiene Min API level 21_

### Impacto en el peso de descarga e instalación de la app

_Example module está pesando 14kb y okhttp para la versión 4.2.0 ~4 terabytes. Para descarga pesaría aproximadamente Xkb menos porque example app tiene Ykb de recursos que se splittean para cada densidad_

## Libs internas (borrar si el PR es para una lib externa)

### Contextos
- [ ] Ya agregué y/o actualicé el contexto en la [context-whitelist](https://github.com/mercadolibre/mobile-dependencies_whitelist/blob/master/context-whitelist.json)

### Configuración para el SLA de Crashes
El proyecto en Jira en el que se van a crear los crashes que ocurran es: **_${SPYN}_**
- [ ] Ya está la configuración hecha.
- [ ] Necesito que me ayuden a configurarlo.

[¿Qué es el SLA de Crashes?](https://sites.google.com/mercadolibre.com/mobile/release-process/seguimiento-de-errores)

## Libs externas (borrar si el PR es para una lib interna)

### Empresas conocidas que actualmente usan esta lib

_Si, OkHttp es actualmente usada por X, Y, Z, etc empresas._

### Madurez de la lib

_Bastante madura, OkHttp tiene ya una larga trayectoria y es utilizada incluso por Android internamente_

### Mantenimiento externo de la lib (A.K.A. ¿está en desarrollo activo?)

_Sí, OkHttp es mantenida por una comunidad extensa e incluso es propiedad de SquareUp_

### Fecha del último release de la lib

_Hace 1 semana_

### ¿Se va a wrappear el uso de una libreria externa? ¿Quién va a ser owner de la misma?

_Creemos que no es necesario un wrapper de la lib. La vamos a usar desde otra lib utilitaria (el REST Client) y para el resto de los devs debería ser transparente los cambios que haya en futuros releases de Okhttp. El ownership va a ser el [equipo de X](mailto:x-team@mercadolibre.com)_

### Alternativas disponibles en el mercado: Tradeoffs

_Si, existe volley. Preferimos esta porque:_
- Razon X
- Razon Y
- Razon Z

## Caso de uso donde necesitamos usar esta lib

_Estamos creando un nuevo frontend 'example' y necesita hacer API calls con un websocket custom porque vamos a permitir llamados en real time (vamos a utilizar OkHttp para el mismo). Creamos una API example-module que nos wrappea la lógica de negocio_

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

## Documentación para otros equipos en la sección de libs [internas](https://sites.google.com/mercadolibre.com/mobile/arquitectura/libs-utilitarias/libs-internas) o [externas](https://sites.google.com/mercadolibre.com/mobile/arquitectura/libs-utilitarias/libs-externas#h.p_mZ_ODrm21KPv) en la wiki de Mobile

- [ ] Ya existe, no tengo que agregar ni modificar nada.
- [ ] Hay que agregar lo que pongo a continuación... 👇
