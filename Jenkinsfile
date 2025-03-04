pipeline {
	agent {
		label 'kub-gradle-agent'
    }

    options {
		buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
    }

    environment {
		PROJECT_KEY = 'sboot-order-processor'
		DOCKER_IMAGE = 'public.ecr.aws/n1a9j0r1/sboot-order-processor'
        AWS_REGION = 'us-east-1'
    }

    stages {

		stage('Restore & Install Deps') {
			steps {
				container('gradle') {
					writeFile file: "gradle-cache.key", text: "$GIT_COMMIT"

                    cache(caches: [
                        arbitraryFileCache(
                            path: ".gradle",
                            includes: "**/*",
                            cacheValidityDecidingFile: "gradle-cache.key"
                        ),
                        arbitraryFileCache(
                            path: "build",
                            includes: "**/*",
                            cacheValidityDecidingFile: "gradle-cache.key"
                        )
                    ]) {
						sh '''
                            gradle --build-cache clean build -x test
                            cp build/libs/app.jar ./app.jar
                        '''
                    }
                }
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

        stage('Login to AWS ECR') {
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

    	stage('SonarQube Analysis') {
			steps {
				container('sonar-scanner') {
					withSonarQubeEnv('sonarqube-server') {
						withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
							sh '''
								sonar-scanner \
								-Dsonar.projectKey=$PROJECT_KEY \
								-Dsonar.sources=. \
								-Dsonar.java.binaries=build/classes/java/main
                    		'''
						}
					}
				}
			}
    	}

    	stage('Trivy Security Scan') {
			steps {
				container('trivy') {
					dir('conecta') {
						sh '''
							trivy fs --skip-dirs build/static/js .
						'''
					}
				}
			}
		}

		stage('Build Multi-Arch') {
			steps {
				container('docker') {
					writeFile file: "docker-cache.key", text: "$GIT_COMMIT"

					cache(caches: [
						arbitraryFileCache(
							path: "/var/lib/docker/buildkit",
							includes: "**/*",
							cacheValidityDecidingFile: "docker-cache.key"
                		)
					]) {
								sh '''

								docker buildx build --debug \
									--platform linux/amd64,linux/arm64 \
									--build-arg JAR_FILE=app.jar \
									--cache-from=type=local,src=/cache/docker \
									--cache-to=type=local,dest=/cache/docker,mode=max \
									-t $DOCKER_IMAGE:latest \
									--push .
								'''
					}
				}
			}
		}



    	//stage('Build Multi-Arch') {
		//	steps {
		//		container('docker') {
		//			sh '''
        //                docker buildx build \
        //                    --platform linux/amd64,linux/arm64 \
        //                    --build-arg JAR_FILE=app.jar \
        //                    -t $DOCKER_IMAGE:latest \
        //                    --push .
        //            '''
        //        }
        //    }
    	//}
	}
}
