# Allowlist Dependencies

> **Note:** This repository is not versioned. The `master` branch is consumed by default, so whenever the `master`
> branch changes, all repositories will immediately start consuming the new changes.

If you need to add or update a library, please visit
the [Wiki](https://furydocs.io/everest/latest/guide/#/develop/dependencies/dependencies?id=allow-list).

## Android Dependencies

Android allowlist dependencies consist of a set of libraries that are available for front-ends and low-level
repositories to consume from the **MercadoLibre-mobile** group. Your Frontend should not be declared here nor consumed
by any other FEnd.

These dependencies are defined in JSON format, and the root-level property is called `whitelist`.

Each dependency is a JSON object that will be matched against the unresolved dependencies of the repository. Repository
dependencies will be strings in the format `group:name:version`. The allowlist fields SUPPORT regex expressions, so you
can define matching cases for groups in single strings.

### Important Considerations:

- Remember these are **regexes**, so if you want to declare `com.example`, you should write it as `com\\.example`.
- Validation is done against unresolved dependencies. If you declare a version as `4\\.\\+`, it **will** match
  with `4.+` (but not strings like `4.2.3`).
- The `expires` field its **optional**. If no field is added, the dependency is considered non-expirable.
    - **Warning:** Expiring dependencies on Wednesdays or Thursdays will fail CI, as they are too close to release trains and
      may cause unforeseen issues.
- If no `group`, `name`, or `version` is provided, they will default to `.*` (any string).
- **All dependencies and fields are sorted**

### Basic JSON Schema:

```json
{
  "whitelist": [
    {
      "description": "(optional) description",
      "expires": "yyyy-MM-dd",
      "group": "regex_group",
      "name": "regex_name",
      "version": "regex_version"
    }
  ]
}
```

### How to test Locally (Android):

If you want to verify that your changes work correctly from your fork, simply add this line to your `/<MODULE-NAME>/build.gradle`:

```
ext["allowlistURL"] = "https://raw.githubusercontent.com/YOUR_GITHUB_USER/mobile-dependencies_whitelist/YOUR_GIT_BRANCH/android-whitelist.json"
```

## iOS Dependencies

iOS allowlist dependencies consist of a set of libraries that are available for front-ends and low-level
repositories to consume from the **MercadoLibre-mobile** group. Your Frontend should not be declared here nor consumed
by any other FEnd.

These dependencies are defined in JSON format, and the root-level property is called `whitelist`.

Each of the dependencies is an object with the following properties:

- `name`: Dependency Podname
- `source`: keyword that indicates the source where the dependency spec should be downloaded. (`public` || `private`)
- `target`: Indicates if it is a test or productive dependency. (`test` || `productive`)
- `version`: Which will be matched against each of the dependencies in the podspec. The `version` string SUPPORTS regex
  expression.

#### Optional

- `description`: (optional) some relevant description
- `expires`: You can have expirable dependencies by adding the `expires` field. If no field is added, the dependency is
  considered as non-expirable

Example:

```json
{
  "whitelist": [
    {
      "description": "# This will match with 'MeliSDK' and version '~>5.+' (version must have ~>5.x)",
      "name": "MeliSDK",
      "version": "^~>5.[0-9]+$"
    },
    {
      "description": "# This will match with 'MLRecommendations' for any version",
      "name": "MLRecommendations",
      "version": null
    }]
}
```

## Support for Granular Dependencies:

This functionality provides a more precise management of the scope of dependencies, giving us the ability to select
specific consumers for each of them.

To activate the granularity feature, it is necessary to introduce a new block within the dependency definition,
specifying which Mercado Libre projects will have access to it. This should be done as follows:

### Android Platform
#### There are two types of granularity:

* GroupId : You specify the group id of the project that will have access to the dependency.
    * Example: `com.mercadolibre.android.example`
* GroupId:name : You specify the group id and the name of the project that will have access to the dependency.
    * Example: `com.mercadolibre.android.example:exampleModule`

```json
{
  "whitelist": [
    {
      "allows_granular_projects": [ 
            "group_meli_project",
            "com.mercadolibre.android.commons"  # Example of a Mercado Libre Dependency Group.
            "com.mercadolibre.android.commons:crash-tracking"  # Example of a module in a Mercado Libre Dependency Group.
      ]
      "description": "(optional) description",
      "expires": "yyyy-MM-dd",
      "group": "group_regex",
      "name": "name_regex",
      "version": "version_regex"
    },
    ...
  ]
}
```

### iOS Platform

```json
{
  "whitelist": [
    {
      "allows_granular_projects": [
        "name_meli_lib",
        "MLRecommendations"  # Example of a Mercado Libre Dependency Lib Name.
      ],      
      "name": "MeliSDK",
      "version": "^~>5.[0-9]+$"
    },
    ...
  ]
}
```

## Support for Transitive Dependencies (ONLY ANDROID):

This functionality provides more precise control over how transitive dependencies can be excluded from projects,
allowing specific consumers to be selected for each one.

### Blocking Transitive Dependencies:

To activate this feature, introduce a new block within the dependency definition with two keys:

1. **namespace**: For non-transitive dependencies, you must specify the namespace:
   `"namespace": "com.name.path.path"`
2. **transitivity**: By default, all dependencies are transitive. To specify otherwise, set it to `false`:
   `"transitivity": "false"`

Both keys will be found within the `"transitive_configuration"` node. Here's an example to visualize it more
clearly:

### Android Platform

```json
{
  "whitelist": [
    {
      "group": "com\\.squareup\\.retrofit2",
      "name": "adapter-rxjava2",
      "version": "2\\.6\\.4",
      "transitive_configuration":
      {
          "namespace": "retrofit2",
          "transitivity": false
      }
    },
    ...
  ]
}
```

If declared `non-transitive dependency` imports are found in your code, our plugin will **block** the build, preventing
the `lintAndroid()` task from completing successfully in CI or locally.


## Basic Continuous Integration (CI) Checks!!
We have some basic checks placed in our CI to ensure that the allowlist is being used correctly. 
The checks can be found [here](https://github.com/mercadolibre/mobile-dependencies_whitelist/blob/master/scripts/checks.sh)
but basically, we are validating the following:

1. **JSON Linter**:
    - Uses the cmd `jsonlint <allowlist_file>` to check if the JSON file is well-formed.
    - you can install it from [here](https://www.npmjs.com/package/jsonlint)
2. **JSON Sorter Lint**:
    - Uses the cmd `jsonsort <allowlist_file> --arrays` to ensure the content of the JSON file is properly sorted, including arrays.
    - you can install it from [here](https://www.npmjs.com/package/json-sort-cli)
3. **Expiration Date Validator**:
    - Verify that the expiration dates in the JSON file are in the correct format (YYYY-MM-DD).
    - Verify that the expiration date isn't a `Wednesdays` or `Thursdays`.
4. **Key Names Validator**:
    - Verify that your are using the proper key names in the JSON file.
5. **Version Pattern Validator**:
    - Verify that we aren't using dynamic versions for external libs and dynamic versioning for internal libs (Meli).
      - External: `version`: `1\\.0\\.1`
      - Internal: `version`: `1\\.+`

Some other checks could be performed, check the CI Error for more information.

## Contexts Allowlist (Deprecated)

For more information, check
the [new context allowlist](https://furydocs.io/mobile-apps/v1.5.2/guide/#/lang-en/metrics/02_crash-rate?id=contexts).

