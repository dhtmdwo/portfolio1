pipeline {
    agent any

    triggers {
        githubPush()
    }

    environment {
        IMAGE_NAME = 'jkweil125/wmthis-back'
        IMAGE_TAG  = "${BUILD_NUMBER}"
    }

    stages {
        stage('Check branch') {
            steps {
                script {
                    if (env.GIT_BRANCH != 'origin/main') {
                        echo "Not main branch (${env.GIT_BRANCH}), skipping pipeline."
                        currentBuild.result = 'SUCCESS'
                        // 즉시 종료
                        return
                    }
                }
            }
        }

        stage('Build') {
            steps {
                echo "Building ${env.GIT_BRANCH} → OK"
                sh 'chmod +x gradlew'
                sh './gradlew bootJar'
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
