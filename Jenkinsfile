def docker_image
pipeline {
    agent any
	environment {
		REGISTRY = 'luizcssoares/crud-agenda'
		DOCKERHUB_CREDENTIALS = 'DockerHub-Login'				
		IMAGE_TAG = "latest"
        NAMESPACE = "default"
		KIND_CONTEXT = "kind-ubuntu"
		KUBECONFIG = credentials('KubeConfig-Secret')		
	}
	stages { 
		stage('GIT pull') {			
			steps{
			   git branch: 'master', url: "https://github.com/luizcssoares/crud-agenda.git"
			}
		}
		stage('Build Maven') {
			tools {				
				maven 'Maven-3.9'                
            }
			steps {
			   sh 'mvn -B -DskipTests clean package'
			}
		}
		stage('SonarQube Analysis') {
            tools {
                maven 'Maven-3.9'
            }

            steps {			   
                withSonarQubeEnv(credentialsId: 'SonarQube-Webhook-Secret', installationName: 'SonarQube-MicroK8s') {                
                    sh '''						
                        mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                        -Dsonar.projectKey=crud-agenda \
                        -Dsonar.projectName="CRUD Agenda"
                    '''
                }
            }
        }
        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {

                    waitForQualityGate(
                        abortPipeline: true,
						webhookSecretId: 'SonarQube-Webhook-Secret'
                    )
                }
            }
        }		
		stage('Docker Build'){
			steps{
			   script {			         
					 docker_image = docker.build("${REGISTRY}:${env.BUILD_NUMBER}", "--no-cache .")					 				 
			   }
			}
		}
		stage('Deploy Docker Hub') {
			steps{
			   script {				 				     
				     docker.withRegistry( 'https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS ) {
					 //docker.image("${registry}:${env.BUILD_NUMBER}").push()
				     docker_image.push()					
				  }				  				
			   }
			}
		}		
        stage('Deploy App on k8s') {
            steps {
				script {													   					                					   
					                                          
                       withKubeConfig([credentialsId: 'KubeConfig-Secret']) {
						    dir ('chart') {								
								sh '''
									pwd
									ls
									kubectl config view
									kubectl config current-context
									kubectl cluster-info
									kubectl get nodes	
								'''		
								sh 'helm install crud-agenda .'
							}																				                     							
					   }
				}
            }
        }
	}
}