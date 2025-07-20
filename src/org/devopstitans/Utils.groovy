package org.devopstitans

class Utils implements Serializable {
    def steps

    Utils(steps) {
        this.steps = steps
    }

    def printMessage(String msg) {
        steps.echo "${msg}"
    }
}

