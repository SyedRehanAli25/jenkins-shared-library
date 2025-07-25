 // vars/k8DeployPipeline.groovy
def call(Map params = [:]) {
    if (!params.configFilePath) {
        error " k8DeployPipeline(configFilePath: 'resources/configs/prod-config.yaml')"
    }

    def config

    pipeline {
        agent any

        stages {
            stage("Load Config") {
                steps {
                    script {
                        node {
                            config = readYaml file: params.configFilePath
                        }

                        env.ENVIRONMENT       = config.ENVIRONMENT
                        env.SLACK_WEBHOOK_URL = config.SLACK_WEBHOOK_URL
                        env.ACTION_MESSAGE    = config.ACTION_MESSAGE
                        env.PLAYBOOK_PATH     = config.PLAYBOOK_PATH
                    }
                }
            }

            stage("Notify Start") {
                steps {
                    script {
                        notifySlack(" Starting: ${env.ACTION_MESSAGE}", env.SLACK_WEBHOOK_URL)
                    }
                }
            }

            stage("Run Ansible Playbook") {
                steps {
                    script {
                        sh "ansible-playbook ${env.PLAYBOOK_PATH} --extra-vars \"env=${env.ENVIRONMENT}\""
                    }
                }
            }

            stage("Notify Success") {
                steps {
                    script {
                        notifySlack("Success: ${env.ACTION_MESSAGE}", env.SLACK_WEBHOOK_URL)
                    }
                }
            }
        }

        post {
            failure {
                script {
                    notifySlack("Failed: ${env.ACTION_MESSAGE}", env.SLACK_WEBHOOK_URL)
                }
            }
        }
    }
}
