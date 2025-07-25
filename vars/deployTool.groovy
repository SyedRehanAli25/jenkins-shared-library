def call(String configFile = 'configs/prod-config.yaml') {
    def config = readYaml text: libraryResource(configFile)

    pipeline {
        agent any

        environment {
            SLACK_CHANNEL = "${config.SLACK_CHANNEL_NAME}"
            ENV = "${config.ENVIRONMENT}"
            BASE_PATH = "${config.CODE_BASE_PATH}"
            MSG = "${config.ACTION_MESSAGE}"
            KEEP_APPROVAL = "${config.KEEP_APPROVAL_STAGE}"
        }

        stages {
            stage('Clone') {
                steps {
                    echo " Cloning code from ${BASE_PATH}"
                    checkout scm
                }
            }

            stage('User Approval') {
                when {
                    expression { return KEEP_APPROVAL.toBoolean() }
                }
                steps {
                    timeout(time: 5, unit: 'MINUTES') {
                        input message: "Do you approve deployment to ${ENV}?"
                    }
                }
            }

            stage('Run Playbook') {
                steps {
                    echo " Running Ansible Playbook for Kubernetes"
                    sh "ansible-playbook ${BASE_PATH}/site.yml -i ${BASE_PATH}/inventory"
                }
            }

            stage('Notify') {
                steps {
                    script {
                        org.devopstitans.utils.Notification.sendSlack(MSG, SLACK_CHANNEL)
                    }
                }
            }
        }
    }
}

