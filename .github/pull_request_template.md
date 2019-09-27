# Dependencias a proponer

- "com.squareup.okhttp3:okhttp:4.2.0"
- "com.mercadolibre.android.example:module:1.+"

#### ¿Como afecta al peso de descarga e instalacion de la Aplicacion?

_Example module esta pesando 14kb y okhttp para la version 4.2.0 ~4 terabytes. Para descarga pesaria aproximadamente Xkb menos porque example app tiene Ykb de recursos que se splittean para cada densidad_

#### ¿Existen algunos casos de uso con éxito de empresas conocidas?

_Si, OkHttp es actualmente usada por X, Y, Z, etc empresas._

#### ¿Que tan madura esta la lib?

_Bastante, OkHttp tiene ya una larga trayectoria y es utilizada incluso por Android intermanete_

#### ¿Tienen desarrollo activo? 

_Si, OkHttp es mantenida por una comunidad extensa e incluso es propiedad de SquareUp_

#### ¿Cuando fue el último release?

_Hace 1 semana_

#### ¿Existe alguna alternativa? ¿Evaluaron tradeoffs?

_Si, existe volley. Preferimos esta porque:_
- Razon X
- Razon Y
- Razon Z

#### ¿Afecta al coldstart de alguna forma?

_No, OkHttp no requiere inicializacion en el `Application`_

#### ¿Utiliza librerías nativas? Tiene soporte para las diferentes arquitecturas de devices?

_No, OkHttp no tiene codigo con NDK_

#### ¿Que versiones android/ios soportan las libs, min api level o deployment targets en ios?

_Tiene Min api level 21_

# Que caso de uso las requiere

_Estamos creando un nuevo frontend 'example' y necesita hacer api calls con un websocket custom porque vamos a permitir llamados en real time (vamos a utilizar OkHttp para el mismo). Creamos una API example-module que nos wrappea la logica de negocio_

#### Hay PRs abiertos con el use case?

- [example PR](www.github.com/mercadolibre)

#### Repositorios afectados

- [example fend](www.github.com/mercadolibre)

# En que apps impacta mi dependencia

MercadoExample
