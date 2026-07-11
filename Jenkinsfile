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
								sh '''
									pwd
									ls									
								'''		
								sh 'helm install crud-agenda .'
							}							
						
							//dir ('chart') {
							//	sh 'ls'
						        //sh 'kubectl get pods'
							//	sh 'helm install crud-agenda .'
						        //sh 'helm install crud-agenda .'
								//sh 'helm upgrade --install apirestcalculadora chart --namespace default --set image.repository=apirestcalculadora --set image.tag=latest'
							//}	     
                     
							
					   }
				}
            }
        }
	}
}