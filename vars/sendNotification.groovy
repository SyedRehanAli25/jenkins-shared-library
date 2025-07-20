def call(Map data = [:]) {
    def repoUrl = data.repoUrl ?: 'N/A'
    def branch = data.branch ?: 'N/A'
    def mavenCommand = data.mavenCommand ?: 'N/A'
    def buildStatus = data.buildStatus ?: 'UNKNOWN'

    echo "---- Build Notification ----"
    echo "Repository: ${repoUrl}"
    echo "Branch: ${branch}"
    echo "Command: mvn ${mavenCommand}"
    echo "Status: ${buildStatus}"

}

