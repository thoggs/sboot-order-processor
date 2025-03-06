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
		APP_IMAGE = 'public.ecr.aws/n1a9j0r1/sboot-order-processor'
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

        stage('Login AWS CLI ECR') {
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

		//stage('Login Buildah AWS ECR') {
		//	steps {
		//		container('buildah') {
		//			sh '''
		//				buildah login -u AWS -p $(cat ecr-login.txt) public.ecr.aws
		//			'''
		//		}
		//	}
		//}

		//stage('Login Buildah Docker Hub') {
		//	steps {
		//		container('buildah') {
		//			withCredentials([usernamePassword(
		//				credentialsId: 'docker-hub-credentials',
		//				usernameVariable: 'DOCKERHUB_USER',
		//				passwordVariable: 'DOCKERHUB_PASS'
		//			)]) {
		//				sh 'buildah login -u $DOCKERHUB_USER -p $DOCKERHUB_PASS docker.io'
		//			}
		//		}
		//	}
		//}

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
					sh '''
						trivy fs --skip-dirs build/static/js .
					'''
				}
			}
		}

		stage('Build Multi-Arch') {
			steps {
				container('docker') {
					writeFile file: "docker-cache.key", text: "$GIT_COMMIT"

					sh'''
						mkdir -p docker_storage_cache
						chmod -R 777 docker_storage_cache
					'''

					cache(caches: [
						arbitraryFileCache(
							path: 'docker_storage_cache',
							includes: '**/*',
							cacheValidityDecidingFile: 'docker-cache.key'
						)
					]) {

						sh'''
							cp -r docker_storage_cache /var/lib/docker/buildkit
						'''

						sh '''
							docker buildx build \
								--platform linux/amd64,linux/arm64 \
								--build-arg JAR_FILE=app.jar \
								--cache-from=type=local,src=docker_storage_cache \
								--cache-to=type=local,dest=docker_storage_cache,mode=max \
								-t $APP_IMAGE:latest \
								--push .
						'''

						sh'''
							cp -r /var/lib/docker/buildkit docker_storage_cache
						'''

					}
				}
			}
		}

		//stage('Push Image') {
		//	steps {
		//		container('buildah') {
		//			sh '''
        //        		buildah manifest push ${APP_IMAGE}:latest docker://${APP_IMAGE}:latest
        //    		'''
		//		}
		//	}
		//}

	}
}
