pipeline {
    agent any
    triggers {
            GenericWebhookTrigger(
                genericVariables: [
                    [key: 'payload', value: '$.pull_request.state']
                ],
                token: 'MY_SECRET_TOKEN',
                causeString: 'Triggered by GitHub PR close',
                filter: '$.pull_request.state == "closed"'  // PR이 close될 때만 트리거
            )
        }
        stages {
            stage('Build') {
                steps {
                    echo 'Building because the PR was closed'
                }
            }
        }

    environment {
        IMAGE_NAME = 'jkweil125/wmthis-back'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Git Clone') {
            steps {
                git branch: 'main', url: 'https://github.com/beyond-sw-camp/be12-fin-5verdose-WMTHIS-BE'
            }
        }
        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew bootJar'
            }
        }
        stage('Docker Build & Push') {
            steps {
                script {
                    docker.build("${IMAGE_NAME}:${IMAGE_TAG}")
                    withDockerRegistry([credentialsId: 'wmthis']) {
                        docker.image("${IMAGE_NAME}:${IMAGE_TAG}").push()
                    }
                }
            }
        }
    }
}
// 수정2트
// 수정3트