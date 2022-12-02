'use strict';
var fs = require('fs');
var Promise = require('pinkie-promise');
module.exports = function (pth) {
	if (typeof pth !== 'string') {
		return Promise.reject(new TypeError('Expected a file path'));
	}

	return new Promise(function (resolve, reject) {
		fs.stat(pth, function (err, stat) {
			if (err) {
				reject(err);
				return;
			}

			resolve(stat.isDirectory());
		});
	});
};

module.exports.sync = function (pth) {
	if (typeof pth !== 'string') {
		throw new TypeError('Expected a file path');
	}

	return fs.statSync(pth).isDirectory();
};
