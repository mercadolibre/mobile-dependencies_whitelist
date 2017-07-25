### Changelog

### 3.6.1
- Fixeamos que los locks corran porque una validacion no cumplia siempre

### 3.5.2
- Ahora si fixeamos jacoco
- Refactorizamos como funcionan los locks. Ahora busca en maven el ultimo release mientras busca la dependencia, en vez de inferirlo

### 3.5.1
- Fixeamos la publicacion
- Fixeamos que no se estaban agregando los plugins de jacoco y robolectric

### 3.5.0
#### Nuevos features:
- Podemos publicar jars desde el plugin haciendo:
    - publishJarRelease
    - publishJarExperimental
    - publishJarLocal
    - publishJarAlpha

- Agregamos nuevos tasks de publicacion

    - publishRelease
    - publishAlpha
    - publishExperimental

(los nuevos tasks se comportan como los anteriores)
