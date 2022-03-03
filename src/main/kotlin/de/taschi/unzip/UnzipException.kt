package de.taschi.unzip

class UnzipException: Exception {
    constructor() {
        Exception()
    }

    constructor(message: String) {
        Exception(message)
    }
}