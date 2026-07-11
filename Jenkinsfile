pipeline {
    agent any
	environment {
		registry = 'luizcssoares/crud-agenda'
		DOCKERHUB_CREDENTIALS = 'DockerHub-Login'		
		DOCKER_IMAGE = ''
		IMAGE_TAG = "latest"
        NAMESPACE = "default"
		KIND_CONTEXT = "kind-ubuntu"
		KUBECONFIG = credentials('KubeConfig-Secret')
		//KUBECONFIG = credentials('minikube-kubeconfig')
	}
	stages { 
		stage('GIT pull') {			
			steps{
			   git branch: 'master', url: "https://github.com/luizcssoares/crud-agenda.git"
			}
		}
		stage('Build Maven') {
			tools {
				//dockerTool 'mydocker'
				maven 'Maven-3.9'
                //docker {
                //    image 'maven:3.9.11-eclipse-temurin-21'
                //}
            }
			steps {
			   sh 'mvn -B -DskipTests clean package'
			}
		}
		stage('SonarQube') {
			steps {
			   echo 'Executin SonarQube.'
			}
		}
		stage('Docker Build'){
			steps{
			   script {
			         docker_image = docker.build  registry
			   }
			}
		}
		stage('Deploy Docker Hub') {
			steps{
			   script {				 
				     // echo 'Deploy Docker Hub concluido com sucesso !'
				     docker.withRegistry( 'https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS ) {
				     docker_image.push("latest")					
				  }				  				
			   }
			}
		}		
        stage('Deploy App on k8s') {
            steps {
				script {													   					                					   
					                                          
                       withKubeConfig([credentialsId: 'KubeConfig-Secret']) {
						    dir ('chart') {
								//sh '''
								//	kubectl config current-context
								//	kubectl get nodes
								//'''

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