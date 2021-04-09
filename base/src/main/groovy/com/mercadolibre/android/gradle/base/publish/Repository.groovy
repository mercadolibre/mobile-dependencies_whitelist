package com.mercadolibre.android.gradle.base.publish

class Repository {
    private String name
    private String url
    private Credentials credentials

    Repository(String name, String url, Credentials credentials) {
        this.name = name
        this.url = url
        this.credentials = credentials
    }

    String getName() {
        return name
    }

    String getUrl() {
        return url
    }

    Credentials getCredentials() {
        return credentials
    }
}
