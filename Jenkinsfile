pipeline {
    triggers {
        GenericTrigger(
            token: 'my-secret-token',
            // JSONPath로 추출할 변수 정의
            genericVariables: [
                [key: 'action', value: '$.action'],
                [key: 'merged', value: '$.pull_request.merged'],
                [key: 'baseRef', value: '$.pull_request.base.ref']
            ],
            causeString: 'Building because PR was merged into main',
            // 필터식 배열로 “AND” 조건 구성
            // 각각의 expression이 모두 true일 때만 트리거
            filterExpressions: [
                [key: 'action', expression: '$.action == "closed"'],
                [key: 'merged', expression: '$.pull_request.merged == true'],
                [key: 'baseRef', expression: '$.pull_request.base.ref == "main"']
            ],
            // 디버깅용 출력 (필요 없으면 false)
            printContributedVariables: true,
            printPostContent: true
        )
    }

    agent any

    environment {
        IMAGE_NAME = 'jkweil125/wmthis-back'
        IMAGE_TAG  = "${BUILD_NUMBER}"
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
