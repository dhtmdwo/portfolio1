pipeline {
    triggers {
        GenericTrigger(
            token: 'my-secret-token',
            genericVariables: [
                [key: 'ref', value: '$.ref'],
                [key: 'pr_base', value: '$.pull_request.base.ref'],
                [key: 'event', value: '$.action'],
                [key: 'merged', value: '$.pull_request.merged']
            ],
            causeString: 'PR merged into $pr_base',
            regexpFilterText: '$event $merged $pr_base',
            regexpFilterExpression: '^closed true main$' // "closed" && "merged=true" && base ref = "main"
        )
    }

    agent any

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
