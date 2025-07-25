def call(Map params = [:]) {
    pipeline {
        agent any

        environment {
            CONFIG_FILE = "resources/configs/prod-config.yaml"
        }

        stages {
            stage('Load Config') {
                steps {
                    script {
                        config = readYaml file: CONFIG_FILE
                        notifySlack(" ${config.ACTION_MESSAGE}", config.SLACK_WEBHOOK_URL)
                    }
                }
            }

            stage('Run Playbook') {
                steps {
                    script {
                        sh "ansible-playbook ${config.CODE_BASE_PATH}/k8s-deploy.yml"
                    }
                }
            }
        }

        post {
            success {
                script {
                    notifySlack(" ${config.ACTION_MESSAGE} completed successfully", config.SLACK_WEBHOOK_URL)
                }
            }
            failure {
                script {
                    notifySlack(" ${config.ACTION_MESSAGE} failed", config.SLACK_WEBHOOK_URL)
                }
            }
        }
    }
}
