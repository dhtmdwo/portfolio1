pipeline {
    triggers {
        GenericTrigger(
            genericVariables: [
                [key: 'action', value: '$.action'],
                [key: 'pr_state', value: '$.pull_request.state']
            ],
            causeString: 'GitHub PR closed event: $action',
            regexpFilterText: '$action $pr_state',
            regexpFilterExpression: '^closed closed$'  // PR close만 트리거
        )
    }

    agent any

    environment {
        IMAGE_NAME = 'jkweil125/wmthis-back'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Start') {
            steps {
                echo '✅ Pull Request가 닫혔습니다. 파이프라인을 실행합니다.'
            }
        }

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
