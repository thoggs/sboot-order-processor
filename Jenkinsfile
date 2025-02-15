pipeline {

	agent {
		label 'ec2-agent'
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
		DOCKER_IMAGE = 'public.ecr.aws/n1d9q0i7/sboot-order-processor:latest'
        AWS_REGION = 'us-east-1'
    }

    stages {

		stage('Checkout') {
			steps {
				git branch: 'main', url: 'https://github.com/thoggs/sboot-order-dispatcher.git'
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

        stage('Login to AWS ECR Public') {
			steps {
				withAWS(credentials: 'aws-credentials', region: "$AWS_REGION") {
					sh '''
                        aws ecr-public get-login-password --region $AWS_REGION \
                        | docker login --username AWS --password-stdin public.ecr.aws
                    '''
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
