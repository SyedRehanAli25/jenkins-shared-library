def call(Map config = [:]) {
    pipeline {
        agent any

        parameters {
            string(name: 'BRANCH', defaultValue: 'main', description: 'Git Branch to Build')
            string(name: 'MVN_CMD', defaultValue: 'mvn clean install -DskipTests', description: 'Maven command to run')
        }

        environment {
            REPO_URL = config.repoUrl ?: 'https://github.com/OT-MICROSERVICES/salary-api.git'
            BRANCH = params.BRANCH
            MVN_CMD = params.MVN_CMD
        }

        stages {
            stage('Clone') {
                steps {
                    echo "Cloning ${REPO_URL} on branch ${BRANCH}"
                    git url: "${REPO_URL}", branch: "${BRANCH}"
                }
            }

            stage('Build') {
                steps {
                    echo "Running Maven Command: ${MVN_CMD}"
                    sh "${MVN_CMD}"
                }
            }

            stage('Archive Artifact') {
                steps {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                }
            }
        }

        post {
            success {
                echo " Build succeeded for ${REPO_URL} [${BRANCH}]"
            }
            failure {
                echo " Build failed for ${REPO_URL} [${BRANCH}]"
            }
            always {
                echo " Notifying: Build Status = ${currentBuild.currentResult}"
            }
        }
    }
}

