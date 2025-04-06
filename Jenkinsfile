pipeline {
	agent {
		label 'kube-gradle-agent'
    }

    options {
		buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
    }

    environment {
		PROJECT_KEY = 'sboot-order-processor'
		APP_IMAGE = 'public.ecr.aws/n1a9j0r1/sboot-order-processor'
        AWS_REGION = 'us-east-1'
        RELEASE_BRANCH = 'main'
        AWS_BUCKET = 'thoggs-sboot-order-processor'
        AWS_BUCKET_RELEASE_FILE_NAME = 'release_version.txt'
        AWS_BUCKET_SNAPSHOT_FILE_NAME = 'snapshot_version.txt'
    }

    stages {
		stage('Define App Version') {
			steps {
				container('aws-cli') {
					withCredentials([
						usernamePassword(
							credentialsId: 'aws-username-password',
							usernameVariable: 'AWS_ACCESS_KEY_ID',
							passwordVariable: 'AWS_SECRET_ACCESS_KEY'
						)
            		]) {
						script {
							lock(resource: 'version-lock') {
								def fileName

								if (env.BRANCH_NAME == env.RELEASE_BRANCH) {
									fileName = env.AWS_BUCKET_RELEASE_FILE_NAME
								} else {
									fileName = env.AWS_BUCKET_SNAPSHOT_FILE_NAME
								}

								sh """
									aws s3 cp s3://${AWS_BUCKET}/${fileName} local_version.txt --region ${AWS_REGION} || true

									if [ ! -f local_version.txt ]; then
										if [ "${env.BRANCH_NAME}" = "${env.RELEASE_BRANCH}" ]; then
											echo "0.0.0" > local_version.txt
										else
											echo "0" > local_version.txt
										fi
									fi

									chmod 666 local_version.txt
								"""

								def currentVer = readFile('local_version.txt').trim()

								if (env.BRANCH_NAME == env.RELEASE_BRANCH) {
									def parts = currentVer.split('\\.')
									def major = parts[0].toInteger()
									def minor = parts[1].toInteger()
									def patch = parts[2].toInteger()

									patch = patch + 1

									def newVersion = "${major}.${minor}.${patch}"

									writeFile file: 'local_version.txt', text: newVersion

									sh """
										aws s3 cp local_version.txt s3://${AWS_BUCKET}/${fileName} --region ${AWS_REGION}
									"""

									env.VERSION = newVersion
								} else {
									def snapshotVal = currentVer.toInteger()
									snapshotVal = snapshotVal + 1

									writeFile file: 'local_version.txt', text: "${snapshotVal}"
									sh """
										aws s3 cp local_version.txt s3://${AWS_BUCKET}/${fileName} --region ${AWS_REGION}
									"""

									def dateStr = new Date().format('yyyy.MMdd')
									env.VERSION = "${dateStr}.${snapshotVal}-SNAPSHOT"
								}

								currentBuild.displayName = env.VERSION
								echo ">>> App Version: ${env.VERSION}"
							}
						}
					}
				}
			}
		}

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

		stage('Login Buildah Docker Hub') {
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

		stage('Build Multi-Arch') {
			steps {
				container('buildah') {
					writeFile file: "buildah-cache.key", text: "$GIT_COMMIT"

					sh'''
						mkdir -p buildah_storage_cache
						chmod -R 777 buildah_storage_cache
					'''

					cache(caches: [
						arbitraryFileCache(
							path: 'buildah_storage_cache',
							includes: '**/*',
							cacheValidityDecidingFile: 'buildah-cache.key'
						)
					]) {
						sh """
                            cp -r buildah_storage_cache /var/lib/containers/storage

                            buildah bud --layers --platform linux/amd64 -t ${APP_IMAGE}-amd64:${VERSION} .
                            buildah bud --layers --platform linux/arm64 -t ${APP_IMAGE}-arm64:${VERSION} .
                            buildah manifest create ${APP_IMAGE}:${VERSION} --amend ${APP_IMAGE}-amd64:${VERSION} --amend ${APP_IMAGE}-arm64:${VERSION}

                            cp -r /var/lib/containers/storage buildah_storage_cache
                        """
					}
				}
			}
		}

		stage('Push Image') {
			steps {
				container('buildah') {
					sh '''
                		buildah manifest push ${APP_IMAGE}:${VERSION} docker://${APP_IMAGE}:${VERSION}
            		'''
				}
			}
		}

	}
}
