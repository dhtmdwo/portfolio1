// ── 파일: Jenkinsfile (레포 루트에 위치) ──

def modules = [
  'gateway-service',
  'inventory-service',
  'market-service',
  'order-service',
  'user-service'
]

pipeline {
  agent any

  triggers {
    // GitHub에 푸시될 때마다
    githubPush()
  }

  environment {
    // Docker 레지스트리 주소 (본인 것이나 DockerHub ID)
    REGISTRY = 'jkweil125'
    // 이미지 태그로 빌드 번호 사용
    TAG      = "${BUILD_NUMBER}"
  }

  stages {

    stage('Git Checkout') {
      steps {
        // 멀티브랜치 파이프라인이라면 checkout scm
        checkout scm
        // diff 용도로 origin/main 기준 fetch
        sh 'git fetch origin main'
      }
    }

    stage('Build & Push Changed Modules') {
      steps {
        script {
          modules.each { mod ->
            // 이 모듈 디렉터리에 변경이 있는지 검사
            def diff = sh(
              script: "git diff --name-only origin/main..HEAD | grep '^${mod}/' || true",
              returnStdout: true
            ).trim()

            if (diff) {
              echo "▶︎ ${mod} 변경 감지됨, 이미지 빌드∙푸시 시작"
              dir(mod) {
                // Docker 빌드
                sh "docker build -t ${REGISTRY}/${mod}:${TAG} ."
                // 로그인 정보는 Jenkins에 'docker-creds'라는 ID로 등록했다고 가정
                withDockerRegistry([ credentialsId: 'docker-creds' ]) {
                  sh "docker push ${REGISTRY}/${mod}:${TAG}"
                }
              }
            } else {
              echo "✱ ${mod} 변경 없음, 스킵"
            }
          }
        }
      }
    }

    stage('Deploy Changed Modules to Kubernetes') {
      steps {
        script {
          modules.each { mod ->
            // 아까와 동일한 방식으로 변경된 모듈만 다시 검사
            def diff = sh(
              script: "git diff --name-only origin/main..HEAD | grep '^${mod}/' || true",
              returnStdout: true
            ).trim()

            if (diff) {
              echo "▶︎ ${mod} 배포 (kubectl set image)"
              // 미리 Jenkins에 KUBECONFIG를 credentialsId 'kubeconfig-creds'로 등록
              withCredentials([file(credentialsId: 'kubeconfig-creds', variable: 'KUBECONFIG')]) {
                sh """
                  kubectl set image deployment/${mod} \
                    ${mod}=${REGISTRY}/${mod}:${TAG} --record
                """
              }
            }
          }
        }
      }
    }
  }
}
