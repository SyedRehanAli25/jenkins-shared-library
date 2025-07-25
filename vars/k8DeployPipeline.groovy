// vars/k8DeployPipeline.groovy

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
                        def configPath = "resources/configs/${params.ENVIRONMENT}-config.yaml"
                        def config = readYaml file: configPath

                        // Load config values into environment variables
                        env.SLACK_WEBHOOK_URL     = config.SLACK_WEBHOOK_URL
                        env.SLACK_CHANNEL_NAME    = config.SLACK_CHANNEL_NAME
                        env.ACTION_MESSAGE        = config.ACTION_MESSAGE
                        env.CODE_BASE_PATH        = config.CODE_BASE_PATH
                        env.KEEP_APPROVAL_STAGE   = config.KEEP_APPROVAL_STAGE.toString()
                        env.DEPLOY_ENV            = config.ENVIRONMENT

                        notifySlack(" ${config.ACTION_MESSAGE}", config.SLACK_WEBHOOK_URL)
                    }
                }
            }

            stage('Approval') {
                when {
                    expression { return env.KEEP_APPROVAL_STAGE == 'true' }
                }
                steps {
                    input message: " Do you approve deployment to ${env.DEPLOY_ENV}?"
                }
            }

            stage('Deploy to Kubernetes') {
                steps {
                    script {
                        echo "Deploying Kubernetes manifests from ${env.CODE_BASE_PATH} to ${env.DEPLOY_ENV}..."
                        // Replace this with your actual deploy tool logic
                        deployTool(env.DEPLOY_ENV)
                    }
                }
            }
        }

        post {
            success {
                script {
                    notifySlack(" Deployment to *${env.DEPLOY_ENV}* succeeded", env.SLACK_WEBHOOK_URL)
                }
            }
            failure {
                script {
                    notifySlack("Deployment to *${env.DEPLOY_ENV}* failed", env.SLACK_WEBHOOK_URL)
                }
            }
        }
    }
}
