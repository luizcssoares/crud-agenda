def docker_image

pipeline {

    agent any

    environment {

        REGISTRY = 'luizcssoares/crud-agenda'

        DOCKERHUB_CREDENTIALS = 'DockerHub-Login'

        NAMESPACE = 'default'

        KUBECONFIG_CREDENTIALS = 'KubeConfig-Secret'
    }

    stages {

        stage('GIT pull') {

            steps {

                git(
                    branch: 'master',
                    url: 'https://github.com/luizcssoares/crud-agenda.git'
                )
            }
        }

        stage('Build') {

            tools {

                maven 'Maven-3.9'
            }

            steps {

                sh '''
                    mvn -B clean compile
                '''
            }
        }

        stage('Realizar Testes') {

            tools {

                maven 'Maven-3.9'
            }

            steps {

                sh '''
                    mvn -B test
                '''
            }
        }

        stage('Gerar JAR') {

            tools {

                maven 'Maven-3.9'
            }

            steps {

                sh '''
                    mvn -B package -DskipTests
                '''
            }
        }

        stage('SonarQube Analysis') {

            tools {

                maven 'Maven-3.9'
            }

            steps {

                withSonarQubeEnv(
                    'SonarQube-MicroK8s'
                ) {

                    sh '''
                        mvn -B \
                        org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970:sonar \
                        -Dsonar.projectKey=crud-agenda \
                        -Dsonar.projectName="CRUD Agenda"
                    '''
                }
            }
        }

        stage('Quality Gate') {

            steps {

                timeout(
                    time: 10,
                    unit: 'MINUTES'
                ) {

                    waitForQualityGate(
                        abortPipeline: true,
                        webhookSecretId: 'SonarQube-Webhook-Secret'
                    )
                }
            }
        }

        stage('Docker Build') {

            steps {

                script {

                    docker_image = docker.build(
                        "${REGISTRY}:${env.BUILD_NUMBER}",
                        '--no-cache .'
                    )
                }
            }
        }

        stage('Push Docker Hub') {

            steps {

                script {

                    docker.withRegistry(
                        'https://index.docker.io/v1/',
                        DOCKERHUB_CREDENTIALS
                    ) {

                        // Envia:
                        // luizcssoares/crud-agenda:25
                        docker_image.push()
                    }
                }
            }
        }

        stage('Deploy App on Kubernetes') {

            steps {

                withKubeConfig(
                    credentialsId: KUBECONFIG_CREDENTIALS
                ) {

                    dir('chart') {

                        sh """
                            helm upgrade --install crud-agenda . \
                            --namespace ${NAMESPACE} \
                            --set image.repository=${REGISTRY} \
                            --set image.tag=${BUILD_NUMBER} \
                            --set image.pullPolicy=Always \
                            --wait \
                            --timeout 5m
                        """
                    }
                }
            }
        }
    }

    post {

        success {

            echo """
            Pipeline concluída com sucesso.

            Imagem:
            ${REGISTRY}:${BUILD_NUMBER}
            """
        }

        failure {

            echo 'A pipeline falhou. Verifique o estágio que ficou vermelho.'
        }
    }
}