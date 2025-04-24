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
        stage('Build on main only') {
            when {
                branch 'main'
            }
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
