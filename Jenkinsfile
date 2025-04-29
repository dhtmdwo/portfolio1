pipeline {
    agent any

    triggers {
        githubPush()
    }

    environment {
        IMAGE_NAME = 'jkweil125/wmthis-back'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }


    stages {
            stage('Branch Check') {
                when {
                    expression {
                        // ain 브랜치가 아니면 빌드 중단
                        echo "expression"
                        echo env.GIT_BRANCH
                        return env.GIT_BRANCH == 'origin/main' || env.BRANCH_NAME == 'main'
                    }
                }
                steps {
                    echo "Branch is main — proceeding with build."
                }
            }
        stage('Git Clone') {
            steps {
                git branch: 'main', url: 'https://github.com/beyond-sw-camp/be12-fin-5verdose-WMTHIS-BE'
            }
        }



        stage('Build') {
            when {
                expression {
                    return env.GIT_BRANCH == 'origin/main' || env.BRANCH_NAME == 'main'
                }
            }
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew bootJar'
            }
        }

        stage('Docker Build & Push') {
            when {
                expression {
                    return env.GIT_BRANCH == 'origin/main' || env.BRANCH_NAME == 'main'
                }
            }
            steps {
                script {
                    docker.build("${IMAGE_NAME}:${IMAGE_TAG}")
                    withDockerRegistry([credentialsId: 'wmthis']) {
                        docker.image("${IMAGE_NAME}:${IMAGE_TAG}").push()
                    }
                }
            }
        }
        stage('Deploy to Kubernetes') {
            when {
                expression {
                    return env.GIT_BRANCH == 'origin/main' || env.BRANCH_NAME == 'main'
                }
            }
            steps {
                script {
                    def deploymentYaml = """
        apiVersion: apps/v1
        kind: Deployment
        metadata:
          name: wmthis-backend
        spec:
          replicas: 2
          selector:
            matchLabels:
              app: wmthis-backend
          template:
            metadata:
              labels:
                app: wmthis-backend
            spec:
              containers:
                - name: backend
                  image: ${IMAGE_NAME}:${IMAGE_TAG}
                  ports:
                    - containerPort: 8080
                  envFrom:
                    - configMapRef:
                        name: wmthis-config
                    - secretRef:
                        name: wmthis-secret
                  livenessProbe:
                    httpGet:
                      path: /api/actuator/health
                      port: 8080
                    initialDelaySeconds: 30
                    periodSeconds: 10
                  readinessProbe:
                    httpGet:
                      path: /api/actuator/health
                      port: 8080
                    initialDelaySeconds: 10
                    periodSeconds: 5
        """
                    writeFile file: 'wmthis-deployment.yaml', text: deploymentYaml
                    sh 'kubectl apply -f wmthis-deployment.yaml'
                }
            }
        }

    }
}
//쿠버