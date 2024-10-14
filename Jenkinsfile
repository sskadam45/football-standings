pipeline {
    agent any

    environment {
        REGISTRY_URL = 'yourregistry/user'
        IMAGE_NAME = 'football-standings-backend'
        TAG = "${env.BUILD_ID}"
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('football-standings-backend') {
                    script {
                        docker.build("-f Dockerfile -t ${REGISTRY_URL}/${IMAGE_NAME}:${TAG} .")
                    }
                }
            }
        }

        stage('Push to Docker Registry') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-credentials-id') {
                        docker.image("${REGISTRY_URL}/${IMAGE_NAME}:${TAG}").push()
                    }
                }
            }
        }

        stage('Deploy to Production') {
            steps {
                echo 'Deploying Spring Boot application from Docker Image...'
                // Placeholder for deploying the Docker image, e.g., updating service in Kubernetes
            }
        }
    }

    post {
        always {
            echo "Cleaning up workspace"
            cleanWs()
        }
    }
}