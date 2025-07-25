def call(Map params = [:]) {
    pipeline {
        agent any

        parameters {
            choice(name: 'ENVIRONMENT', choices: ['dev', 'qa', 'prod'], description: 'Select environment to deploy')
        }

        stages {
            stage('Load Config') {
                steps {
                    script {
                        def envName = params.ENVIRONMENT ?: params.ENVIRONMENT = ENVIRONMENT  // fallback
                        def configPath = "resources/configs/${envName}-config.yaml"
                        def config = readYaml file: configPath

                        // Load into env vars
                        env.SLACK_WEBHOOK_URL     = config.SLACK_WEBHOOK_URL
                        env.SLACK_CHANNEL_NAME    = config.SLACK_CHANNEL_NAME ?: ''
                        env.ACTION_MESSAGE        = config.ACTION_MESSAGE
                        env.CODE_BASE_PATH        = config.CODE_BASE_PATH
                        env.KEEP_APPROVAL_STAGE   = config.KEEP_APPROVAL_STAGE.toString()
                        env.DEPLOY_ENV            = config.ENVIRONMENT

                        notifySlack(" ${env.ACTION_MESSAGE}", env.SLACK_WEBHOOK_URL, env.SLACK_CHANNEL_NAME)
                    }
                }
            }

            stage('Approval') {
                when {
                    expression { return env.KEEP_APPROVAL_STAGE == 'true' }
                }
                steps {
                    input message: "Do you approve deployment to *${env.DEPLOY_ENV}*?"
                }
            }

            stage('Deploy') {
                steps {
                    script {
                        echo " Deploying Kubernetes manifests from ${env.CODE_BASE_PATH} to ${env.DEPLOY_ENV}..."

                        sh """
                        cd ${env.CODE_BASE_PATH}
                        kubectl apply -f . -n ${env.DEPLOY_ENV}
                        kubectl rollout status deployment --all -n ${env.DEPLOY_ENV}
                        """

                        notifySlack(" Deployment to *${env.DEPLOY_ENV}* succeeded", env.SLACK_WEBHOOK_URL, env.SLACK_CHANNEL_NAME)
                    }
                }
            }
        }

        post {
            failure {
                script {
                    notifySlack(" Deployment to *${env.DEPLOY_ENV}* failed", env.SLACK_WEBHOOK_URL, env.SLACK_CHANNEL_NAME)
                }
            }
        }
    }
}
