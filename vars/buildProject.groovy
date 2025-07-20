def call(Map config = [:]) {
    def repoUrl = config.repoUrl ?: 'https://github.com/example/maven-project.git'
    def branch = config.branch ?: 'main'
    def mavenCommand = config.mavenCommand ?: 'clean install'

    stage('Checkout') {
        checkout([
            $class: 'GitSCM',
            branches: [[name: "*/${branch}"]],
            userRemoteConfigs: [[url: repoUrl]]
        ])
    }

    stage('Build') {
        echo "Running Maven command: ${mavenCommand}"
        sh "mvn ${mavenCommand}"
    }
}

