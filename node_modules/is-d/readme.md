# is-d [![Build Status](https://travis-ci.org/SamVerschueren/is-d.svg?branch=master)](https://travis-ci.org/SamVerschueren/is-d)

> Check if a file is a directory


## Install

```
$ npm install --save is-d
```


## Usage

```js
const isDirectory = require('is-d');

isDirectory.sync('index.js');
//=> false

isDirectory('foo').then(dir => {
    //=> true 
});
```


## API

### isDirectory(path)

### isDirectory.sync(path)

#### path

Type: `string`

The path of the file.


## License

MIT © [Sam Verschueren](https://github.com/SamVerschueren)
