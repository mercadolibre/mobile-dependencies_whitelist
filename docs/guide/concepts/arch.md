# Introducción

La arquitectura se basa en los plugins existentes, siendo así cada uno de estos tiene su propia lista de Configurers que
son los encargados de administrar un aspecto básico de la configuración del módulo o el proyecto en donde están siendo
aplicado. Por ejemplo contamos con configurers que aplican plugins basicos, otro que generar extensiones, a continuación
una explicación de cada uno.

##### Android Configurer
Este configurer se encarga del funcionamiento de la extensión `BaseExtension` siendo la encargada de configurar `Android`
de esta forma podemos setear las siguientes variables dentro del modulo donde se está aplicando. A continuación una lista
de las variables que configuramos desde el plugin:

    - Variables
        - Api Sdk Level
        - Min Sdk Level
        - Build Tools Version
        - Java Version
        - Proguard Files
        - vectorDrawables
            - useSupportLibrary
            - generatedDensities


##### Module Configurer
Este configurer se encarga de administrar los modulos, teniendo una lista de ellos, llama a cada uno y le solicita que 
la aplique configuración de su funcionalidad consiguiendo así complementar el entorno de trabajo del repositorio. 

Este configurer se encarga de llamar a cada uno de los módulos que ingeniamos para que realice la configuración de su 
funcionalidad dentro del proyecto al que está apuntando este configurer consiguiendo así complementar el entorno de
trabajo del repositorio.

##### Extensions Configurer
Este configurer se encarga de generar las extensiones de On Off de los módulos para que estos sean capaces de funcionar 
de una forma correcta y en caso de que sea necesario se puedan apagar o customizar según las variables que se pasen por
u extensión.

##### Plugin Configurer
Este configurer se encarga de aplicar los plugins en los proyectos para que puedan cumplir su rol correctamente. 
Siendo el caso de una librería, una app o un dynamicfeature.

##### Basics Configurer
Este configurer se encarga de brindar los repositorios necesarios para que el proyecto envie informacion al igual que
configura la forma en que se administran las versiones dinámicas.