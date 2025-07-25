// vars/k8DeployPipeline.groovy
def call(Map params = [:]) {
    if (!params.configFilePath) {
        error "k8DeployPipeline(configFilePath: 'resources/configs/prod-config.yaml')"
    }

    def config = readYaml file: params.configFilePath

    pipeline {
        agent any

        environment {
            ENVIRONMENT         = "${config.ENVIRONMENT}"
            SLACK_WEBHOOK_URL   = "${config.SLACK_WEBHOOK_URL}"
            ACTION_MESSAGE      = "${config.ACTION_MESSAGE}"
            PLAYBOOK_PATH       = "${config.PLAYBOOK_PATH}"
        }

        stages {
            stage("Notify Start") {
                steps {
                    script {
                        notifySlack(" Starting: ${ACTION_MESSAGE}", SLACK_WEBHOOK_URL)
                    }
                }
            }

            stage("Run Ansible Playbook") {
                steps {
                    script {
                        sh "ansible-playbook ${PLAYBOOK_PATH} --extra-vars \"env=${ENVIRONMENT}\""
                    }
                }
            }

            stage("Notify Success") {
                steps {
                    script {
                        notifySlack("Success: ${ACTION_MESSAGE}", SLACK_WEBHOOK_URL)
                    }
                }
            }
        }

        post {
            failure {
                script {
                    notifySlack(" Failed: ${ACTION_MESSAGE}", SLACK_WEBHOOK_URL)
                }
            }
        }
    }
}
