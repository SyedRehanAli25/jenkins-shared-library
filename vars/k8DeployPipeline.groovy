def call(Map params = [:]) {
    if (!params.configFilePath) {
        error " Please provide a configFilePath like: k8DeployPipeline(configFilePath: 'resources/configs/prod-config.yaml')"
    }

    pipeline {
        agent any

        stages {
            stage('Load Config') {
                steps {
                    script {
                        // Surround with node to access workspace
                        node {
                            config = readYaml file: params.configFilePath
                        }

                        echo "Environment: ${config.ENVIRONMENT}"
                        echo " Channel: ${config.SLACK_CHANNEL_NAME}"
                        echo " Message: ${config.ACTION_MESSAGE}"

                        notifySlack(" ${config.ACTION_MESSAGE}", config.SLACK_WEBHOOK_URL)
                    }
                }
            }

            stage('Run Playbook') {
                steps {
                    script {
                        sh "ansible-playbook ${config.CODE_BASE_PATH}/playbook.yaml"
                    }
                }
            }

            stage('Success Notification') {
                steps {
                    script {
                        notifySlack(" Deployment to *${config.ENVIRONMENT}* successful!", config.SLACK_WEBHOOK_URL)
                    }
                }
            }
        }

        post {
            failure {
                script {
                    notifySlack("Deployment to *${config.ENVIRONMENT}* failed!", config.SLACK_WEBHOOK_URL)
                }
            }
        }
    }
}
