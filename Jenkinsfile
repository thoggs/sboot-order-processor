//pipeline {
//
//	agent {
//		label 'ec2-java17-agent'
//    }
//
//    options {
//		buildDiscarder(logRotator(numToKeepStr: '5'))
//        skipDefaultCheckout(true)
//    }
//
//    environment {
//		DOCKER_IMAGE = 'public.ecr.aws/n1a9j0r1/sboot-order-processor'
//        AWS_REGION = 'us-east-1'
//    }
//
//    stages {
//
//		stage('Checkout') {
//			steps {
//				git branch: 'main', url: 'https://github.com/thoggs/sboot-order-processor.git'
//            }
//        }
//
//        stage('Set up QEMU') {
//			steps {
//				sh 'docker run --rm --privileged multiarch/qemu-user-static --reset -p yes || true'
//    		}
//		}
//
//        stage('Set up Docker Buildx') {
//			steps {
//				sh 'docker buildx create --use --name mybuilder || true'
//                sh 'docker buildx inspect --bootstrap'
//            }
//        }
//
//        stage('Login to AWS ECR Public') {
//			steps {
//				withCredentials([
//                    usernamePassword(
//                        credentialsId: 'aws-username-password',
//                        usernameVariable: 'AWS_ACCESS_KEY_ID',
//                        passwordVariable: 'AWS_SECRET_ACCESS_KEY'
//                    )
//                ]) {
//					sh '''
//                        aws ecr-public get-login-password --region $AWS_REGION \
//                        | docker login --username AWS --password-stdin public.ecr.aws
//                    '''
//                }
//            }
//        }
//
//        stage('SonarQube Analysis') {
//			steps {
//				withSonarQubeEnv('sonarqube-server') {
//					sh 'sonar-scanner -Dsonar.projectKey=sboot-order-processor -Dsonar.sources=.'
//                }
//            }
//        }
//
//        stage('Build Multi-Arch') {
//			steps {
//				sh '''
//                    docker buildx build \
//						--platform linux/amd64,linux/arm64 \
//						-t $DOCKER_IMAGE:latest \
//						--push .
//                '''
//            }
//        }
//
//    }
//}

pipeline {
	agent {
		label 'docker-builder'
    }

    options {
		buildDiscarder(logRotator(numToKeepStr: '5'))
        skipDefaultCheckout(true)
    }

    environment {
		DOCKER_IMAGE = 'public.ecr.aws/n1a9j0r1/sboot-order-processor'
        AWS_REGION = 'us-east-1'
    }

    stages {

		stage('Checkout') {
			steps {
				git branch: 'main', url: 'https://github.com/thoggs/sboot-order-processor.git'
            }
        }

        stage('Set up QEMU') {
			steps {
				container('docker') {
					sh 'docker run --rm --privileged multiarch/qemu-user-static --reset -p yes || true'
                }
            }
        }

        stage('Set up Docker Buildx') {
			steps {
				container('docker') {
					sh 'docker buildx create --use --name mybuilder || true'
                    sh 'docker buildx inspect --bootstrap'
                }
            }
        }

        stage('Login to AWS ECR Public') {
			steps {
				container('aws-cli') {
					withCredentials([
						usernamePassword(
							credentialsId: 'aws-username-password',
							usernameVariable: 'AWS_ACCESS_KEY_ID',
							passwordVariable: 'AWS_SECRET_ACCESS_KEY'
						)
						]) {
						sh '''
							aws ecr-public get-login-password --region $AWS_REGION > ecr-login.txt
						'''
						}
				}
			}
		}

		stage('Login to Docker') {
			steps {
				container('docker') {
					sh '''
						cat ecr-login.txt | docker login --username AWS --password-stdin public.ecr.aws
					'''
				}
			}
		}

		stage('Build with Gradle') {
			steps {
				container('gradle') {
					sh 'gradle clean build'
                }
            }
        }

       stage('SonarQube Analysis') {
			steps {
				container('sonar-scanner') {
					withSonarQubeEnv('sonarqube-server') {
						withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
							sh '''
                        sonar-scanner \
                            -Dsonar.projectKey=sboot-order-processor \
                            -Dsonar.sources=. \
                            -Dsonar.login=$SONAR_TOKEN
                    '''
						}
					}
				}
			}
       }


    	stage('Build Multi-Arch') {
			steps {
				container('docker') {
					sh '''
                        docker buildx build \
                            --platform linux/amd64,linux/arm64 \
                            -t $DOCKER_IMAGE:latest \
                            --push .
                    '''
                }
            }
    	}
	}
}
