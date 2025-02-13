pipeline {
	agent any

    environment {
		DOCKER_IMAGE = 'thoggs/sboot-order-processor:latest'
    }

    stages {
		stage('Checkout') {
			steps {
				git branch: 'main', url: 'https://github.com/thoggs/sboot-order-processor.git'
            }
        }

        stage('Build Docker Image') {
			steps {
				sh 'docker build -t $DOCKER_IMAGE .'
            }
        }

        stage('Login to Docker Hub') {
			steps {
				withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials',
                                                 usernameVariable: 'DOCKER_USERNAME',
                                                 passwordVariable: 'DOCKER_PASSWORD')]) {
					sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                }
            }
        }

        stage('Push to Docker Hub') {
			steps {
				sh 'docker push $DOCKER_IMAGE'
            }
        }

        stage('Cleanup') {
			steps {
				sh 'docker rmi -f $DOCKER_IMAGE || true'
                sh 'docker system prune -f --volumes || true'
            }
        }
    }
}
