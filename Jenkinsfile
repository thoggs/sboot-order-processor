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

		stage('Login Buildah AWS ECR') {
			steps {
				container('buildah') {
					sh '''
						buildah login -u AWS -p $(cat ecr-login.txt) public.ecr.aws
					'''
				}
			}
		}

		stage('Login Docker Hub') {
			steps {
				container('buildah') {
					withCredentials([usernamePassword(
						credentialsId: 'docker-hub-credentials',
						usernameVariable: 'DOCKERHUB_USER',
						passwordVariable: 'DOCKERHUB_PASS'
					)]) {
						sh 'buildah login -u $DOCKERHUB_USER -p $DOCKERHUB_PASS docker.io'
					}
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
					sh '''
						trivy fs --skip-dirs build/static/js .
					'''
				}
			}
		}

		stage('Build Multi-Arch') {
			steps {
				container('buildah') {
					sh '''
						buildah bud --layers --platform linux/amd64,linux/arm64 \
							--build-arg JAR_FILE=app.jar \
							-t $DOCKER_IMAGE:latest .
					'''
            	}
        	}
    	}

    	stage('Push Image') {
			steps {
				container('buildah') {
					sh '''
                		buildah push $DOCKER_IMAGE:latest docker://${DOCKER_IMAGE}:latest
            		'''
				}
			}
		}

	}
}
