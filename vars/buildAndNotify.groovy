def call(Map config = [:]) {
    def repo = config.repoUrl ?: error("Missing repoUrl")
    def branch = config.branch ?: "master"
    def mavenCmd = config.mavenCommand ?: "mvn clean install"

    buildProject(repo, branch, mavenCmd)
    sendNotification(repo, branch, mavenCmd)
}

