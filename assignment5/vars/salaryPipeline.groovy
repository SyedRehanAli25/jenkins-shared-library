def call(Map config = [:]) {
    pipeline {
        agent any

        parameters {
            booleanParam(name: 'SKIP_STABILITY', defaultValue: false, description: 'Skip Code Stability Check')
            booleanParam(name: 'SKIP_QUALITY', defaultValue: false, description: 'Skip Code Quality Check')
            booleanParam(name: 'SKIP_COVERAGE', defaultValue: false, description: 'Skip Code Coverage Check')
        }

        stages {
            stage('Checkout') {
                steps {
                    git url: config.repoUrl, branch: config.branch
                }
            }

            stage('Code Stability') {
                when {
                    expression { return !params.SKIP_STABILITY }
                }
                steps {
                    echo 'Running code stability checks...'
                    // Insert your real steps here
                }
            }

            stage('Code Quality') {
                when {
                    expression { return !params.SKIP_QUALITY }
                }
                steps {
                    echo 'Running code quality analysis...'
                    // Insert your real steps here
                }
            }

            stage('Code Coverage') {
                when {
                    expression { return !params.SKIP_COVERAGE }
                }
                steps {
                    echo 'Running code coverage tools...'
                    // Insert your real steps here
                }
            }

            stage('Publish Artifacts?') {
                steps {
                    script {
                        def userInput = input(
                            message: "Approve artifact publishing?",
                            parameters: [
                                choice(name: 'APPROVE_PUBLISHING', choices: ['Approve', 'Reject'], description: 'Should we publish?')
                            ]
                        )

                        if (userInput == 'Approve') {
                            echo 'Publishing artifacts...'
                            // Insert artifact upload logic here
                        } else {
                            echo 'Artifact publishing was rejected.'
                        }
                    }
                }
            }
        }
    }
}

