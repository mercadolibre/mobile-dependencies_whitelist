# Dependencias a proponer

- "com.squareup.okhttp3:okhttp:4.2.0"
- "com.mercadolibre.android.example:module:1.+"

#### Impacto en el peso de descarga e instalación de la app

_Example module esta pesando 14kb y okhttp para la version 4.2.0 ~4 terabytes. Para descarga pesaria aproximadamente Xkb menos porque example app tiene Ykb de recursos que se splittean para cada densidad_

#### Empresas conocidas que actualmente usan esta lib

_Si, OkHttp es actualmente usada por X, Y, Z, etc empresas._

#### Madures de la lib

_Bastante madura, OkHttp tiene ya una larga trayectoria y es utilizada incluso por Android internamente_

#### Mantenimiento externo de la lib (A.K.A. ¿está en desarrollo activo?)

_Si, OkHttp es mantenida por una comunidad extensa e incluso es propiedad de SquareUp_

#### Fecha del último release de la lib

_Hace 1 semana_

#### ¿Se va a wrappear el uso de una libreria externa? ¿Quien va a ser owner de la misma?

_Creemos que no es necesario un wrapper de la lib. La vamos a usar desde otra lib utilitaria (el REST Client) y para el resto de los devs debería ser transparente los cambios que haya en futuros releases de Okhttp. El ownership va a ser el [equipo de X](mailto:x-team@mercadolibre.com)_

#### Alternativas disponibles en el mercado: Tradeoffs

_Si, existe volley. Preferimos esta porque:_
- Razon X
- Razon Y
- Razon Z

#### ¿Afecta al start-up time de alguna forma?

_No, OkHttp no requiere inicializacion en el `Application`_

#### ¿Utiliza libs nativas? ¿Tiene soporte para las diferentes arquitecturas de devices?

_No, OkHttp no tiene codigo con NDK_

#### Versiones mínimas y máximas del sistema operativo soportadas

_Tiene Min API level 21_

# Caso de uso donde necesitamos usar esta lib

_Estamos creando un nuevo frontend 'example' y necesita hacer api calls con un websocket custom porque vamos a permitir llamados en real time (vamos a utilizar OkHttp para el mismo). Creamos una API example-module que nos wrappea la logica de negocio_

#### PRs abiertos con este Caso de uso

- [example PR](www.github.com/mercadolibre)

#### Repositorios afectados

- [example fend](www.github.com/mercadolibre)

# En que apps impacta mi dependencia

- [ ] Mercado Libre
- [ ] Mercado Pago
- [ ] Flex / Logistics
- [ ] WMS
- [ ] Meli Store

# Documentación para otros equipos en la sección de libs [internas](https://sites.google.com/mercadolibre.com/mobile/arquitectura/libs-utilitarias/libs-internas) o [externas](https://sites.google.com/mercadolibre.com/mobile/arquitectura/libs-utilitarias/libs-externas#h.p_mZ_ODrm21KPv) en la wiki de Mobile

- [ ] Ya existe, no tengo que agregar ni modificar nada.
- [ ] Hay que agregar lo que pongo a continuación... 👇
