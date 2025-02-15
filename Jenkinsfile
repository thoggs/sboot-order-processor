pipeline {
	agent {
		docker {
			image 'alpine:latest'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    options {
		disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5'))
        skipDefaultCheckout(true)
    }

    triggers {
		githubPush()
    }

    environment {
		DOCKER_IMAGE = 'thoggs/sboot-order-processor:latest'
    }

    stages {
		stage('Checkout') {
			steps {
				git branch: 'main', url: 'https://github.com/thoggs/sboot-order-processor.git'
            }
        }

        stage('Set up QEMU') {
			steps {
				sh 'docker run --rm --privileged multiarch/qemu-user-static --reset -p yes'
            }
        }

        stage('Set up Docker Buildx') {
			steps {
				sh 'docker buildx create --use --name mybuilder || true'
                sh 'docker buildx inspect --bootstrap'
            }
        }

        stage('Login to Docker Hub') {
			steps {
				withCredentials([
                    usernamePassword(
                        credentialsId: 'docker-hub-credentials',
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {
					sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                }
            }
        }

        stage('Build and Push Multi-Arch Docker Image') {
			steps {
				sh '''
                    docker buildx build --platform linux/amd64,linux/arm64 \
                        -t $DOCKER_IMAGE \
                        --push .
                '''
            }
        }

        stage('Cleanup') {
			steps {
				sh 'docker system prune -f --volumes || true'
            }
        }
    }
}
